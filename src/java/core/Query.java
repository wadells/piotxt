package core;

import java.util.Date;

/**
 * The fundamental object representing a users request from reception, until
 * response and logging. Any and all relevant information should probably be
 * stored and accessible in this class.
 */
public class Query {

	/** The time the sms was sent. */
	private final Date timeSent;

	/** The time the request was received. */
	private final Date timeReceived;

	/** The time the response finished sending. */
	private Date timeResponded;

	/** The body of the received text message. */
	private final String body;

	/** The phone number the text message came from. */
	private final String phoneNumber;

	/** A system recognized keyword or null. */
	private String keyword;

	/** The appropriate response to the message. */
	private String response;

	/**
	 * Create a new query, ready to be processed.
	 * 
	 * @param timeSent
	 *            the time the sms was sent
	 * @param body
	 *            the body of the text message
	 * @param phoneNumber
	 *            the sending phone number
	 */
	public Query(Date timeSent, String body, String phoneNumber) {
		this.timeSent = timeSent;
		this.timeReceived = new Date();
		this.body = body;
		this.phoneNumber = phoneNumber;

	}

	// This is important for tracking which queries have been processed by the
	// system
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Query)) {
			return false;
		}
		Query that = (Query) obj;
		if (this.timeSent.equals(that.timeSent)
				&& this.phoneNumber.equals(that.phoneNumber)
				&& this.body.equals(that.body)) {
			return true;
		}
		return false;
	}

	public String getBody() {
		return body;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getResponse() {
		return response;
	}

	public Date getTimeReceived() {
		return timeReceived;
	}

	public Date getTimeResponded() {
		return timeResponded;
	}

	public Date getTimeSent() {
		return timeSent;
	}

	// This is important for tracking which queries have been processed by the
	// system
	@Override
	public int hashCode() {
		String prehash = timeSent.getTime() + phoneNumber;
		return prehash.hashCode();
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void setTimeResponded(Date timeResponded) {
		this.timeResponded = timeResponded;
	}

	public String toString() {
		return phoneNumber + " " + timeSent.toString() + " " + body;
	}

}
