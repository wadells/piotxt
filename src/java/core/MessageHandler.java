package core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/** A service that generates responses to requests for raz scheduling info. */
public abstract class MessageHandler {

	/** Preset help keyword. */
	public static final String KEY_HELP = "help";

	/** The format of all dates in the headers of text messages */
	public static final SimpleDateFormat STD_DATE_FORM = new SimpleDateFormat(
			"EEE, MMM d, hh:mm aaa");

	/** The keywords this message handler uses. */
	protected Keywords keywords;

	protected MessageHandler() {
		this.keywords = new Keywords();
		keywords.add(KEY_HELP, "how to use the service");
	}

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
		StringBuilder response = new StringBuilder();
		String header = getHeader(query.getTimeReceived());
		response.append(header);
		int charactersRemaining = PioText.SMS_LENGTH - response.length();
		String message;
		if (query.getKeyword() == null) {
			message = unrecognizedKeywordMessage(query.getTimeReceived(),
					charactersRemaining);
		} else if (query.getKeyword().equals(KEY_HELP)) {
			message = helpMessage(query.getTimeReceived());
		} else {
			message = keywordMessage(query, charactersRemaining);
		}
		response.append(message);
		return response.toString();
	}

	/**
	 * This is a "helper" method for generating the body of the response for the
	 * help keyword.
	 * 
	 * @return the help response
	 */
	public String helpMessage(Date time) {
		StringBuilder message = new StringBuilder();
		for (String k : keywords.words()) {
			message.append(String.format("\n%s %s", k.toUpperCase(), keywords
					.getDefinition(k)));
		}
		return message.toString();
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

	/** Initializes this MessageHandler with the given properties. */
	public void initialize(Properties props) {
		// Do nothing here, yet.
	}

	/**
	 * Generates the body of a response for one of the handler specific
	 * keywords. This should not be called when a final result is expected, as
	 * the message will not include a header.
	 * 
	 * @param query
	 *            the request
	 * @return the list of stops in response to this query
	 */
	public abstract String keywordMessage(Query query, int maxLength);

	/**
	 * The default message this handler generates for an unrecognized keyword,
	 * no keyword at all, or other lookups.
	 * 
	 * @param time
	 * @return
	 */
	public abstract String unrecognizedKeywordMessage(Date time, int maxLength);

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
