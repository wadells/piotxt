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
	 * @throws ConnectionException
	 *             if the connection is unable to retrieve or parse the sms
	 */
	public List<? extends Query> getNewMessages() throws ConnectionException;

	/**
	 * Sends a single Sms from the connection to the number.
	 * 
	 * 
	 * @param number
	 *            the destination of the Sms
	 * @param message
	 *            the body of the Sms
	 * @throws ConnectionException
	 *             if the connection is unable to send the Sms
	 */
	public void sendSms(String number, String message)
			throws ConnectionException;

	/**
	 * Connects to the service. This must be called before any other methods may
	 * be called.
	 * 
	 * @throws ConnectioException
	 *             if unable to connect
	 */
	public void connect() throws ConnectionException;

	/**
	 * Deletes a query from the service. If it exists.
	 * 
	 * @param query
	 *            the message to delete
	 * 
	 * @throws ConnectionException
	 *             if unable to connect
	 */
	public void deleteSms(Query query) throws ConnectionException;

}
