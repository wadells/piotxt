package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static persistance.Log.*;

import sms.ConnectionException;
import sms.GvConnection;
import sms.SmsConnection;

/**
 * The main program manager. This is what should be run in a jar.
 */
public class PioText {

	public final static String VERSION = "piotxt-v0.5";

	/** How often PioText will check for new messages, in ms. */
	private final static long CHECK_FREQ = 60000;

	/** A half hour, in ms. */
	private final static long HALF_HOUR = 1800000;

	/** The file where setup information is stored. */
	public final static File PROPERTY_FILE = new File(
			"resources/pio_text.properties");

	/** The file where sensitive information is stored. */
	public final static File SECURE_PROPERTY_FILE = new File(
			"resources/secure.properties");

	/** The format for printing dates when running in verbose mode */
	public final static SimpleDateFormat SYSOUT_FORMAT = new SimpleDateFormat(
			"MM/d h:mm a");

	/** Whether or not PioText should output basic data to the console. */
	private static boolean verbose;

	/** The connection to send and recieve sms through */
	private SmsConnection connection;

	/** The message handler that will process queries. */
	private MessageHandler handler;

	/**
	 * Creates a new PioText with the given properties.
	 * 
	 * @param props
	 */
	protected PioText(Properties props) {
		String gvUsername = props.getProperty("gv_user");
		String gvPassword = props.getProperty("gv_pass");
		connection = new GvConnection(gvUsername, gvPassword);
		try {
			handler = (MessageHandler) Class.forName(
					props.getProperty("message_handler")).newInstance();
		} catch (Exception e) {
			System.err
					.println("Error loading the message handler. Make sure the class name is correct.");
			e.printStackTrace();
			System.exit(1);
		}
		handler.intialize(props);
	}

	/** Initializes the sms connection. */
	public void initialize() throws ConnectionException {
		connection.connect();
	}

	/** @return true if PioText is running in verbose mode. */
	public boolean isVerbose() {
		return verbose;
	}

	/** @return false if the query is more than a half hour old */
	private boolean isNotOld(Query q) {
		long diff = System.currentTimeMillis() - q.getTimeSent().getTime();
		return diff < HALF_HOUR;
	}

	/** Helper for printing strings in verbose mode. Acts like printf. */
	private static void printv(String format, Object... args) {
		if (!verbose)
			return;
		System.out.printf(format, args);
	}

	/** Helper for printing strings in verbose mode. Acts like println. */
	private static void printlnv(String string) {
		if (!verbose)
			return;
		System.out.println(string);
	}

	/**
	 * If the query is within the last half hour, this will generate a and send
	 * a reply, then log the query.
	 * 
	 * @param query
	 *            the query to be processed
	 * @throws ConnectionException
	 *             if the message cannot be sent
	 */
	private void processQuery(Query query) throws ConnectionException {
		if (isNotOld(query)) {
			String response = handler.getResponse(query);
			connection.sendSms(query.getPhoneNumber(), response);
			query.setTimeResponded(new Date());
			query.setResponse(response);
			log(query);
			// TODO syslog sent responses
		}
		// TODO syslog old queries without sent responses
	}

	public void run() {
		System.out.println("PioText is running...");
		printlnv("-----------------------------------");
		HashSet<Query> processed = new HashSet<Query>();
		List<? extends Query> queries;
		boolean firstTime = true;
		while (true) {
			try {
				queries = connection.getNewMessages();
				int newQueries = 0;
				for (Query q : queries) {
					if (!processed.contains(q)) {
						newQueries++;
						try {
							processQuery(q);
							processed.add(q);
							connection.deleteSms(q);
							// TODO: syslog delete count
						} catch (ConnectionException e) {
							// TODO: retry/system log this
							e.printStackTrace();
						}
					}
				}
				if ((newQueries > 0 || firstTime)) {
					// TODO: system log this with timestamp
					printv("%-20s", SYSOUT_FORMAT.format(new Date()));
					String plural = "queries.";
					if (newQueries == 1)
						plural = "query.";
					printv("%2d new %-7s\n", newQueries, plural);
					firstTime = false;
				}
			} catch (ConnectionException e) {
				// TODO system log this
				e.printStackTrace();
			}
			// sleep for a while before checking again
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

	/**
	 * Load a Properties file.
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(File propsFile, File securePropsFile)
			throws IOException {
		Properties result = null;
		InputStream propStream = new FileInputStream(propsFile);
		InputStream securePropStream = new FileInputStream(securePropsFile);
		if (propStream != null && securePropStream != null) {
			result = new Properties();
			result.load(propStream); // Can throw IOException
			result.load(securePropStream);
		}
		return result;
	}

	public static void main(String[] args) {
		// Say Hi!
		System.out.println("PioText Sms Server");
		System.out.println("-----------------------------------");

		// load properties
		System.out.printf("%-30s", "Loading properties...");
		Properties props = null;
		try {
			props = load(PROPERTY_FILE, SECURE_PROPERTY_FILE);
		} catch (IOException e) {
			System.err.println("Could load properties at " + PROPERTY_FILE
					+ ".  Exiting...");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done.");

		// set verbosity, defaults to false
		verbose = Boolean.parseBoolean(props.getProperty("verbose"));

		// Random sillyness
		printv("%-30sDone.\n", "Corralling enough bits...");

		// set up pio text
		System.out.printf("%-30s", "Initializing PioText...");
		PioText piotxt = new PioText(props);
		try {
			piotxt.initialize();
		} catch (ConnectionException e) {
			System.out
					.println("Could not establish sms connection. Exiting...");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done.");

		// Random sillyness
		if (verbose)
			printv("%-30sDone.\n\n", "Compacting widgets...");

		// Run PioText
		piotxt.run();
	}

}
