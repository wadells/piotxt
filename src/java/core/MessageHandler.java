package core;

import java.util.GregorianCalendar;

/** A service that generates responses to requests for raz scheduling info. */
public abstract class MessageHandler {

	/**
	 * A helper method that must be fulfilled by subclasses. This is where the
	 * logic of generating a response will be done.
	 * 
	 * @param key
	 *            the key word
	 * @param time
	 *            the time the message was recieved at
	 * @return the appropriate response for this key word
	 */
	public String getResponse(String key, final GregorianCalendar time) {
		for (Keyword k : Keyword.values()) {
			if (k.matches(key)) {
				return getResponse(k, time);
			}
		}
		return null;
	}

	/**
	 * The lookup method that should be fulfilled by sub-classes
	 * 
	 * @param key
	 *            a keyword from a text message
	 * @param time
	 *            the time the lookup should be performed for
	 * @return a string with the requested information
	 */
	public abstract String getResponse(Keyword key, final GregorianCalendar time);
	
	
	public abstract String defaultMessage(GregorianCalendar time);
	
	

}
