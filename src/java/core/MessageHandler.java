package core;

import java.text.SimpleDateFormat;
import java.util.Date;

/** A service that generates responses to requests for raz scheduling info. */
public abstract class MessageHandler {

	/** The keywords this message handler uses. */
	protected Keywords keywords;

	protected MessageHandler(Keywords keywords) {
		this.keywords = keywords;
	}

	/** The format of all dates in the headers of text messages */
	public static final SimpleDateFormat STD_DATE_FORM = new SimpleDateFormat(
			"EEE, MMM d, hh:mm aaa");

	/**
	 * Generates the appropriate response to a query. The implementation is left
	 * to subclasses.
	 * 
	 * @param query
	 *            the request
	 * @return a string with the requested information
	 */
	public abstract String getResponse(Query query);

	/**
	 * The default message this handler generates for an unrecognized keyword,
	 * no keyword at all, or other lookups.
	 * 
	 * @param time
	 * @return
	 */
	public abstract String defaultMessage(Date time);

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
	 * Generates a two line header for a response message with the current time.
	 * 
	 * @return the header
	 */
	public static String getHeader() {
		return getHeader(new Date());
	}

	/**
	 * Generates a two line header for a response message using the time passed
	 * in.
	 * 
	 * @param time
	 * @return the header of the message.
	 */
	public static String getHeader(Date time) {
		String header = "TXT4RAZ\n";
		String timeString = STD_DATE_FORM.format(time);
		header += timeString + "\n";
		return header;
	}

}
