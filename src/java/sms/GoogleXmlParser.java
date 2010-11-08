package sms;

import static utils.FileUtils.readFile;
import gvjava.org.json.JSONException;
import gvjava.org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import persistance.Log;
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

	/**
	 * Builds a new query from various bits of google data.
	 * 
	 * @param id
	 *            the json/xml id of the conversation
	 * @param msg
	 *            the xml containing the body of the message
	 * @param json
	 *            the json containing the metadata of the message
	 * @return a new google voice query
	 * @throws JSONException
	 *             if the json is unparseable
	 */
	private static GvQuery buildQuery(String id, Element msg, JSONObject json)
			throws JSONException {
		// the body is only avaliable through the xml
		String body = getElementsWithAttributeValue(msg, "class",
				"gc-message-sms-text", false).get(0).getTextContent();
		// get other data from the json metadata
		JSONObject message = json.getJSONObject("messages").getJSONObject(id);
		String number = message.getString("phoneNumber");
		Long time = message.getLong("startTime");
		return new GvQuery(new Date(time), body, number, id);
	}

	/**
	 * Creates a Document Object Model from the raw xml string.
	 * 
	 * @param xml
	 *            the unsanitized response from google
	 * @return the DOM representing the xml
	 * @throws SAXException
	 *             if the xml is unparseable
	 */
	private static Document createDom(String xml) throws SAXException {
		String niceXml = removeNastyBits(xml);
		// Using factory get an instance of document builder
		Document dom = null;
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(new InputSource(new StringReader(niceXml)));
		} catch (ParserConfigurationException ignored) { // won't fail
			ignored.printStackTrace();
		} catch (SAXException e) {
			throw e;
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
		return dom;
	}

	/**
	 * Returns a list of the top level dom objects representing conversations.
	 * 
	 * @param root
	 *            the root of the dom to search
	 */
	private static List<Element> extractConversations(Document dom) {
		Element root = dom.getDocumentElement();
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
	 * Extracts the text in the 'from:' field from the dom.
	 * 
	 * @param message
	 *            the google xml message to be processed
	 * @return the text of the from field, either a number or 'Me:'
	 */
	private static String extractFromField(Element message) {
		return getElementsWithAttributeValue(message, "class",
				"gc-message-sms-from", false).get(0).getTextContent().trim();
	}

	/**
	 * Extracts the JSON Object from the Google xml.
	 * 
	 * @param dom
	 *            the dom of the xml
	 * @return The JSON Object
	 * @throws JSONException
	 *             if the object is unparseable
	 */
	private static JSONObject extractJson(Document dom) throws JSONException {
		String text = dom.getElementsByTagName("json").item(0).getTextContent();
		JSONObject json = new JSONObject(text);
		return json;
	}

	/**
	 * Extracts the last message in a conversation.
	 * 
	 * @param conversation
	 *            the conversation
	 * @return the last message in the conversation
	 */
	private static Element extractLastMessage(Element conversation) {
		List<Element> rows = getElementsWithAttributeValue(conversation,
				"class", "gc-message-sms-row", false);
		Element lastrow = rows.get(rows.size() - 1);
		return lastrow;
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

	public static void main(String[] args) {

		String fileLocation = "resources/gv_dump";
		for (int i = 0; i < 5; i++) {
			String file = fileLocation + i + ".xml";
			try {
				String xml = readFile(file);
				List<GvQuery> list = parse(xml);
				System.out.println("\n" + list.size() + " new queries.");
				for (Query q : list) {
					System.out.println(Log.queryToString(q));
				}
			} catch (IOException ignored) {
				System.out.println("Could not read " + file + ".");
			} catch (Exception e) {
				System.out.println("Could not parse file " + file + ".");
				e.printStackTrace();
			}
		}
	}

	/**
	 * This processes the raw xml/html in a screen scrapey fashion to produce a
	 * list of queries.
	 * 
	 * @param xml
	 *            the xml google's "api" returns
	 * @return a list of user queries for the system to process
	 * @throws SAXException
	 * @throws JSONException
	 */
	public static List<GvQuery> parse(String xml) throws SAXException,
			JSONException {
		List<GvQuery> found = new ArrayList<GvQuery>();
		Document dom = createDom(xml);
		JSONObject json = extractJson(dom);
		List<Element> conversations = extractConversations(dom);
		for (Element conversation : conversations) {
			// The last message in the conversation is the only one we
			// care about. If it is from us, ignore it, because we've dealt
			// with that coversation, if it is from someone else, then we
			// have a new query.
			String id = conversation.getAttribute("id");
			Element lastmsg = extractLastMessage(conversation);
			// there is only one message per row, so grab index 0
			String from = extractFromField(lastmsg);
			// If from someone else, process it
			if (!from.equals("Me:")) {
				GvQuery query = buildQuery(id, lastmsg, json);
				found.add(query);
			}
		}

		return found;
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

}
