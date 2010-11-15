package persistance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import core.Query;

/**
 * A log of all requests received. This class follows the Singleton pattern. The
 * log file format is:
 * 
 * <pre>
 * fonehash S[MM/dd/yy@hh:mm:ss] R[MM/dd/yy@hh:mm:ss] (systinfo) {keyword} "flattened text of message"
 * </pre>
 * 
 * For further discussion of log format check out the wiki entry at:
 * <p>
 * http://github.com/javins/piotxt/wiki/Message-Logging-and-Statistics
 */
public class Log {

	/** The default location the log will save to. */
	public static final File DEFAULT_FILE = new File("piotxt_messages.log");

	/**
	 * The format log dates are stored in. More precise than the general format
	 * used for the messages.
	 */
	public static final SimpleDateFormat LOG_DATE_FORM = new SimpleDateFormat(
			"MM/dd/yy@kk:mm:ss");

	/** An algorithm for hashing phone numbers. */
	private static MessageDigest sha;

	/** The file this instance of log saves to. */
	private final File logFile;

	/** The total number of text messages processed by this instance of log. */
	private int total;

	Log(File logFile) {
		this.logFile = logFile;
		try {
			sha = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Log(Properties props) {
		String url = props.getProperty("message_log_file");
		if (url != null) {
			// TODO check to makesure url is valid/reachable before accepting
			logFile = new File(url);
		} else {
			System.err.println("No message log file specified. Using "
					+ DEFAULT_FILE.getPath() + ".");
			logFile = DEFAULT_FILE;
		}
		try {
			sha = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** @return the total number of queries this log has recorded */
	public int getTotal() {
		return total;
	}

	/**
	 * Records a single query in this log and occasionally saves the log to its
	 * file.
	 * 
	 * @param query
	 *            the query to be logged
	 */
	public void record(Query query) {
		total++;
		try {
			save(query, logFile);
		} catch (IOException e) {
			// TODO: inform user
			e.printStackTrace();
			// for now do nothing but save again in another 10 messages
		}
		if (total % 500 == 0) {
			// TODO: backup
		}
	}

	/**
	 * Returns a version of the string stripped of all newlines, tabs and extra
	 * whitespace.
	 * 
	 * @param s
	 *            the string to flatten
	 * @return a version of the string with all extra whitespace removed
	 */
	static String flatten(String s) {
		return s.replaceAll("\\s+", " ").trim();
	}

	/**
	 * Generates a cryptographically secure eight digit hash of a phone number.
	 * 
	 * @param phoneNumber
	 *            the phone number to be hashed
	 * @return the eight digit truncated sha-1
	 */
	public static String generateID(String phoneNumber) {
		sha.reset();
		byte[] buffer = phoneNumber.getBytes();
		sha.update(buffer);
		byte[] hash = sha.digest();
		String hex = new BigInteger(1, hash).toString(16);
		return hex.substring(0, 8);
	}

	public static void main(String[] args) throws InterruptedException {
		Date then = new Date();
		Log log = new Log(new File("log/messages.log"));
		Thread.sleep(1000);
		for (int i = 0; i < 20; i++) {
			Query q = new Query(then, "help", "15036666666");
			q.setResponse("no");
			q.setTimeResponded(new Date());
			log.record(q);
		}
	}

	/**
	 * Returns a single line log format of a message. For example
	 * <p>
	 * The first four fields should all be justified / equally spaced. The phone
	 * number is hashed because we want to know about user statistics, but
	 * storing peoples phone number is just creepy. Currently it isn't a secure
	 * hash, but that could be fixed if security becomes an issue.
	 * 
	 * @see {@link Log}
	 * 
	 * @param q
	 *            the query to be flattened into a single log entry
	 * @return the string representing the query
	 */
	public static String queryToString(Query q) {
		String phonehash = generateID(q.getPhoneNumber());
		String sent = LOG_DATE_FORM.format(q.getTimeSent());
		String responded = LOG_DATE_FORM.format(q.getTimeResponded());
		String sysinfo = "--------"; // TODO : put actual system information
		// here
		String keyword = q.getKeyword() == null ? "null" : q.getKeyword();
		String body = flatten(q.getBody());
		return String.format("%s S[%s] R[%s] (%s) {%s} \"%s\"", phonehash,
				sent, responded, sysinfo, keyword, body);
	}

	/**
	 * Saves a list of queries to a file. This appends the queries to the file
	 * instead of overwriting.
	 * 
	 * @param queries
	 *            a list of queries to be saved
	 * @param file
	 *            the file to append them to
	 * @throws IOException
	 *             if the file cannot be read
	 */
	public static void save(Query query, File file) throws IOException {
		boolean append = true;
		FileWriter writer = new FileWriter(file, append);
		writer.write(queryToString(query)
				+ System.getProperty("line.separator"));
		writer.flush();
		writer.close();
	}
}
