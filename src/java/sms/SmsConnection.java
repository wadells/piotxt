package sms;

import java.util.List;

import core.Query;

/**
 * A connection to a sms service that can be queried for new sms and used to
 * send outgoing sms.
 */
public interface SmsConnection {

	/**
	 * Returns a list of recent queries without a reply. This may return an
	 * empty list if retrieval is sucessful, but there are no new queries.
	 * 
	 * @return a list of queries
	 * 
	 * @throws SmsRecieveException
	 *             if the connection is unable to retrieve or parse the sms
	 */
	public List<Query> getNewMessages() throws SmsRecieveException;

	/**
	 * Sends a single Sms from the connection to the number.
	 * 
	 * 
	 * @param number
	 *            the destination of the Sms
	 * @param message
	 *            the body of the Sms
	 * @throws SmsSendException
	 *             if the connection is unable to send the Sms
	 */
	public void sendSms(String number, String message) throws SmsSendException;

}
