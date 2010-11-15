package core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/** A service that generates responses to requests for raz scheduling info. */
public abstract class MessageHandler {

	/** Preset help keyword. */
	public static final String KEY_HELP = "help";

	/** The keywords this message handler uses. */
	protected Keywords keywords;

	/** The format of all dates in the headers of text messages */
	public static final SimpleDateFormat STD_DATE_FORM = new SimpleDateFormat(
			"EEE, MMM d, hh:mm aaa");

	protected MessageHandler() {
		this.keywords = new Keywords();
		keywords.add(KEY_HELP, "how to use the service");
	}

	/** Initializes this MessageHandler with the given properties. */
	public void initialize(Properties props) {
		// Do nothing here, yet.
	}
	
	/**
	 * The default message this handler generates for an unrecognized keyword,
	 * no keyword at all, or other lookups.
	 * 
	 * @param time
	 * @return
	 */
	public abstract String defaultMessage(Date time);

	/**
	 * Generates a response from the handler specific keywords, ignoring any
	 * global or system default keywords (e.g. help). The implementation is left
	 * to subclasses. This should not be called when a final result is expected.
	 * 
	 * @param query
	 *            the request
	 * @return the list of stops in response to this query
	 */
	public abstract String getHandlerResponse(Query query);

	/**
	 * Generates the appropriate response to a query.
	 * <p>
	 * The method identifyKeyword(Query q) should be called to assign a keyword
	 * to the query, otherwise it may be parsed as a null query.
	 * 
	 * @param query
	 *            the request
	 * @return a string with the requested information
	 */
	public final String getResponse(Query query) {
		identifyKeyword(query);
		if (query.getKeyword() == null) {
			return defaultMessage(query.getTimeReceived());
		}
		if (query.getKeyword().equals(KEY_HELP)) {
			return helpMessage(query.getTimeReceived());
		}
		return getHandlerResponse(query);
	}

	/**
	 * This is a "helper" method for generating a message in response to the
	 * help keyword.
	 * 
	 * @return the help response
	 */
	public String helpMessage(Date time) {
		String response = getHeader(time);
		for (String k : keywords.words()) {
			response += "\n" + k.toUpperCase() + " "
					+ keywords.getDefinition(k);
		}
		return response;
	}

	/**
	 * Identifies the keyword in a query based on the keywords this message
	 * handler recognizes. This must be called for a query to have a Keyword.
	 * Otherwise, it will have a null keyword.
	 * <p>
	 * This method alters the state of the Query object.
	 * 
	 * @param query
	 *            the query to be tagged
	 */
	private void identifyKeyword(Query query) {
		String word = keywords.extract(query.getBody());
		query.setKeyword(word);
	}

	/**
	 * Generates a two line header for a response message using the time passed
	 * in.
	 * 
	 * @param time
	 * @return the header of the message.
	 */
	public static String getHeader(Date time) {
		String header = "PioTxt\n";
		String timeString = STD_DATE_FORM.format(time);
		header += timeString + "\n";
		return header;
	}

}
