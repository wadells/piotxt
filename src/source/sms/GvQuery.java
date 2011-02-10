package sms;

import java.util.Date;

import core.Query;

/**
 * A simple wrapper so that queries passed back to Google Voice will have access
 * are aware of the id that google keeps.
 */
public class GvQuery extends Query {
	
	private final String id;

	/**
	 * A query from GvConnection.
	 * 
	 * @param timeSent
	 *            the timestamp from google
	 * @param body
	 *            the message body
	 * @param phoneNumber
	 *            the phone number
	 * @param googleID
	 *            the id (from json) that google uses for this query
	 */
	public GvQuery(Date timeSent, String body, String phoneNumber,
			String googleID) {
		super(timeSent, body, phoneNumber);
		this.id = googleID;
	}
	
	/**
	 * @return the google id associated with this query
	 */
	public String getGoogleID() {
		return id;
	}

}
