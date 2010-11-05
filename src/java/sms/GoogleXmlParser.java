package sms;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import persistance.Log;
import static utils.FileUtils.readFile;

import core.Query;

/**
 * A parser for the xml that the google-voice-api returns when queried about
 * recent sms. This is a library of one really awesome static function.
 * <p>
 * Currently there is some messyness, because it is in the xml parsing where it
 * is decided which messages have been responded to. Basically if gv has us as
 * the bottom message in a conversation, we ignore it, because we have already
 * responed. If, on the other hand, someone else has the last message in a
 * conversation, then that is a new query.
 * <p>
 * This seems fishy to have such important logic in the parser, as opposed to
 * the connection. But it works, and it was a lot easier to implement than
 * dragging out the whole conversation, only to ignore most of it.
 * 
 */
public class GoogleXmlParser {

	private final static DateFormat SMALL_TIME = new SimpleDateFormat("h:mm a"); // e.g.
	// 6:27
	// PM
	private final static DateFormat BIG_TIME = new SimpleDateFormat(
			"MM/d/yy h:mm a"); // e.g. 11/3/10 4:33 PM

	public static void main(String[] args) {
		// try {
		// Date d = parseTime("11/3/10 4:33 AM");
		// System.out.println(d.toString());
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String fileLocation = "resources/gv_dump";
		for (int i = 0; i < 5; i++) {
			try {
				String xml = readFile(fileLocation + i + ".xml");
				List<Query> list = parse(xml);
				System.out.println(list.size() + " new queries.\n");
				for (Query q : list) {
					System.out.println(Log.queryToString(q));
				}
			} catch (IOException ignored) {
				System.out.println("Could not read " + fileLocation + ".");
			}
		}
	}

	/**
	 * A helper method to parse the time stamps properly. Google serves them in
	 * two different formats, but this will sort through it all.
	 * 
	 * Using the deprecated Date object methods is sloppy, but Calendar is such
	 * a huge pain that it isn't really worth dragging all of that out when Date
	 * does it efficiently enough.
	 * 
	 * @param time
	 *            the time string Google returns
	 * @return a date object representing the actual time recieved
	 * @throws ParseException
	 *             if the date can't be parsed
	 */
	@SuppressWarnings("deprecation")
	static Date parseTime(String time) throws ParseException {
		Date date;
		if (time.length() <= "hh:mm PM".length()) {
			date = SMALL_TIME.parse(time);
			Date today = new Date();
			date = new Date(today.getYear(), today.getMonth(), today.getDay(),
					date.getHours(), date.getMinutes());
		} else {
			date = BIG_TIME.parse(time);
		}
		return date;
	}

	/**
	 * This processes the raw xml/html in a screen scrapey fashion to produce a
	 * list of queries.
	 * 
	 * @param xml
	 *            the xml google's "api" returns
	 * @return a list of user queries for the system to process
	 */
	public static List<Query> parse(String xml) {
		List<Query> found = new ArrayList<Query>();

		// clean up xml
		String niceXml = removeNastyBits(xml);
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(new InputSource(new StringReader(niceXml)));
			Element root = dom.getDocumentElement();
			List<Element> conversations = getConversations(root);
			for (Element conversation : conversations) {
				List<Element> rows = getElementsWithAttributeValue(
						conversation, "class", "gc-message-sms-row", false);
				// The last message in the conversation is the only one we
				// care about. If it is from us, ignore it, because we've dealt
				// with that coversation, if it is from someone else, then we
				// have a new query.
				Element lastrow = rows.get(rows.size() - 1);
				// there is only one message per row, so grab index 0
				String from = getElementsWithAttributeValue(lastrow, "class",
						"gc-message-sms-from", false).get(0).getTextContent();
				// If not from us, ignore
				if (!from.equalsIgnoreCase("Me:")) {
					String msg = getElementsWithAttributeValue(lastrow,
							"class", "gc-message-sms-text", false).get(0)
							.getTextContent();
					String time = getElementsWithAttributeValue(lastrow,
							"class", "gc-message-sms-time", false).get(0)
							.getTextContent();

					found.add(new Query(parseTime(time), msg, from));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	/**
	 * Returns a list of the top level dom objects representing conversations.
	 * 
	 * @param root
	 *            the root of the dom to search
	 */
	private static List<Element> getConversations(Element root) {
		List<Element> conv = new ArrayList<Element>();
		// Traverse the children of the html element
		NodeList nl = root.getElementsByTagName("html").item(0).getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element e = (Element) nl.item(i);
				// if they have an id, then they reresent a conversation
				if (e.hasAttribute("id")) {
					conv.add(e);
				}
			}
		}
		return conv;
	}

	/**
	 * A helper method to trim out the CDATA bits and html refrences in what
	 * google returns. This seems incredibly hacky, but necessary for the dom
	 * processor to work.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String removeNastyBits(String in) {
		String clean = in;
		clean = clean.replaceAll("<..CDATA.", "");
		clean = clean.replaceAll("\\]\\]>", "");
		clean = clean.replaceAll("&nbsp;", "");
		clean = clean.replaceAll("&copy;", "");
		// The following needs to be scrubbed because of url queries that java's
		// parser interprets as entities. This is a non-greedy replace that
		// should catch every url with an ampersand in it.
		clean = clean.replaceAll("href=\"http://.*?&.*?\"", "");
		return clean;

	}

	/**
	 * Recursively searched the children of an element to find all elements that
	 * have an attribute with a value. The contains flag signals whether the
	 * attribute value should equal the search value (true) or simply contain it
	 * (false).
	 * 
	 * This uses width first search.
	 * 
	 * @param search
	 *            the element to be searched
	 * @param attribute
	 *            the attribute name
	 * @param value
	 *            the value of the attribute
	 * @param contains
	 * @return
	 */
	private static List<Element> getElementsWithAttributeValue(Element search,
			String attribute, String value, boolean equals) {
		List<Element> found = new ArrayList<Element>();
		Queue<Element> q = new LinkedList<Element>();
		q.add(search);

		if (equals) {
			while (!q.isEmpty()) {
				Element e = q.poll();
				addChildrenToQueue(e, q);
				if (e.hasAttribute(attribute)) {
					String v = e.getAttribute(attribute);
					if (v.equals(value)) {
						found.add(e);
					}
				}
			}
		} else { // contains
			while (!q.isEmpty()) {
				Element e = q.poll();
				addChildrenToQueue(e, q);
				if (e.hasAttribute(attribute)) {
					String v = e.getAttribute(attribute);
					if (v.contains(value)) {
						found.add(e);
					}
				}
			}
		}
		return found;
	}

	/**
	 * A helper method for searching through Element trees.
	 * 
	 * @param e
	 *            the Element with children
	 * @param q
	 *            the Queue to add the children to
	 */
	private static void addChildrenToQueue(Element e, Queue<Element> q) {
		NodeList nl = e.getChildNodes();
		int size = nl.getLength();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				// if we have an element, and not an attribute, add it
				if (nl.item(i) instanceof Element) {
					q.add((Element) nl.item(i));
				}
			}
		}
	}

	// def extractsms(htmlsms) :
	// """
	// extractsms -- extract SMS messages from BeautifulSoup tree of Google
	// Voice SMS HTML.
	//
	// Output is a list of dictionaries, one per message.
	// """
	// msgitems = [] # accum message items here
	// # Extract all conversations by searching for a DIV with an ID at top
	// level.
	// tree = BeautifulSoup.BeautifulSoup(htmlsms) # parse HTML into tree
	// conversations = tree.findAll("div",attrs={"id" : True},recursive=False)
	// for conversation in conversations :
	// # For each conversation, extract each row, which is one SMS message.
	// rows = conversation.findAll(attrs={"class" : "gc-message-sms-row"})
	// for row in rows : # for all rows
	// # For each row, which is one message, extract all the fields.
	// msgitem = {"id" : conversation["id"]} # tag this message with
	// conversation ID
	// spans = row.findAll("span",attrs={"class" : True}, recursive=False)
	// for span in spans : # for all spans in row
	// cl = span["class"].replace('gc-message-sms-', '')
	// msgitem[cl] = (" ".join(span.findAll(text=True))).strip() # put text in
	// dict
	// msgitems.append(msgitem) # add msg dictionary to list
	// return msgitems

}
