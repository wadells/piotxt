package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static persistance.Log.*;

import sms.ConnectionException;
import sms.GvConnection;
import sms.SmsConnection;
import sms.SmsRecieveException;
import sms.SmsSendException;

/**
 * The main program manager. This is what should be run in a jar.
 */
public class PioText {

	public final static String VERSION = "piotxt-v0.5";

	/** The file where setup information is stored. */
	public final static File PROPERTY_FILE = new File(
			"resources/pio_text.properties");

	/** How often PioText will check for new messages. */
	final static long CHECK_FREQ = 60000;

	/**
	 * The number of time PioText will try to retrieve new messages before
	 * giving up.
	 */
	final static long RECIEVE_RETRY = 3;

	/**
	 * The number of time PioText will try to send response messages before
	 * giving up.
	 */
	final static long SEND_RETRY = 3;

	/** The message handler that will process queries. */
	private MessageHandler handler;

	/** The connection to send and recieve sms through */
	private SmsConnection connection;

	/**
	 * Creates a new PioText with the given properties.
	 * 
	 * @param props
	 */
	protected PioText(Properties props) {
		String gvUsername = props.getProperty("gv_user");
		String gvPassword = props.getProperty("gv_pass");
		connection = new GvConnection(gvUsername, gvPassword);
		handler = new TestHandler();
	}

	public static void main(String[] args) {
		// load properties
		Properties props = null;
		try {
			props = load(PROPERTY_FILE);
		} catch (IOException e) {
			System.out.println("Could load properties at " + PROPERTY_FILE
					+ ".  Exiting...");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Sucessfully loaded properties!");

		// set up pio text
		PioText piotxt = new PioText(props);
		try {
			piotxt.initialize();
		} catch (ConnectionException e) {
			System.out
					.println("Could not establish sms connection. Exiting...");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Sucessfully initialized PioText.");

		// run
		piotxt.run();
	}

	/**
	 * Load a Properties file.
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(File propsFile) throws IOException {
		Properties result = null;
		InputStream in = new FileInputStream(propsFile);
		if (in != null) {
			result = new Properties();
			result.load(in); // Can throw IOException
		}
		return result;
	}

	public void run() {
		HashSet<Query> processed = new HashSet<Query>();
		List<Query> queries;
		while (true) {
			try {
				queries = connection.getNewMessages();
				int newQueries = 0;
				for (Query q : queries) {
					if (!processed.contains(q)) {
						newQueries++;
						respondToQuery(q);
						processed.add(q);
					}
				}
				if (newQueries > 0) {
					System.out.println(newQueries + " new queries.");
				}
			} catch (SmsRecieveException e) {
				// TODO error log this
				e.printStackTrace();
			}
			// sleep for a while before cheking again
			try {
				Thread.sleep(CHECK_FREQ);
			} catch (InterruptedException e) {
				System.out
						.println("Something has gone terribly wrong, and the PioText thread has been interrupted. Exiting...");
				e.printStackTrace();
				System.exit(1);
			}

		}
	}

	private void respondToQuery(Query query) {
		handler.identifyKeyword(query);
		String response = handler.getResponse(query);
		try {
			connection.sendSms(query.getPhoneNumber(), response);
			query.setTimeResponded(new Date());
			query.setResponse(response);
			log(query);
		} catch (SmsSendException e) {
			// TODO error log this
			e.printStackTrace();
		}
	}

	/** Initializes the sms connection. */
	public void initialize() throws ConnectionException {
		connection.connect();
	}

}
