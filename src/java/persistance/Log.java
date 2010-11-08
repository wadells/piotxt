package persistance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	/**
	 * The format log dates are stored in. More precise than the general format
	 * used for the messages.
	 */
	public static final SimpleDateFormat LOG_DATE_FORM = new SimpleDateFormat(
			"MM/dd/yy@kk:mm:ss");

	/** The file that this log saves to. */
	public static final File LOG_FILE = new File("log/raz.log");

	/** The total number of text messages processed by this instance of log. */
	private int total;

	private ArrayList<Query> buffer;

	/** The file this instance of log saves to. */
	private final File file;

	/** The singleton instance of log. */
	private static Log instance;

	public static final Log getInstance() {
		if (instance == null) {
			instance = new Log();
		}
		return instance;
	}

	protected Log() {
		this.file = LOG_FILE;
		buffer = new ArrayList<Query>();
	}

	public Log(File file) {
		this.file = file;
		buffer = new ArrayList<Query>();
	}

	public static void log(Query query) {
		getInstance().record(query);
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
		String phonehash = String.format("%08x", q.getPhoneNumber().hashCode());
		String sent = LOG_DATE_FORM.format(q.getTimeSent());
		String responded = LOG_DATE_FORM.format(q.getTimeResponded());
		String sysinfo = "--------"; // TODO : put actuall system information
		// here
		String keyword = q.getKeyword() == null ? "null" : q.getKeyword();
		String body = flatten(q.getBody());
		return String.format("%s S[%s] R[%s] (%s) {%s} \"%s\"", phonehash,
				sent, responded, sysinfo, keyword, body);
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
	 * Records a single query in this log and occasionally saves the log to its
	 * file.
	 * 
	 * @param query
	 *            the query to be logged
	 */
	public void record(Query query) {
		total++;
		buffer.add(query);
		// if (total % 10 == 0) { // save every time for now
		try {
			save(buffer, file);
			buffer.clear();
		} catch (IOException e) {
			// TODO: inform user
			e.printStackTrace();
			// for now do nothing but save again in another 10 messages
		}
		if (total % 500 == 0) {
			// TODO: backup
		}
		// }

	}

	/** @return the total number of queries this log has recorded */
	public int getTotal() {
		return total;
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
	public static void save(List<Query> queries, File file) throws IOException {
		boolean append = true;
		FileWriter writer = new FileWriter(file, append);
		for (Query q : queries) {
			writer.write(queryToString(q)
					+ System.getProperty("line.separator"));
		}
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws InterruptedException {
		Date then = new Date();
		Thread.sleep(1000);
		for (int i = 0; i < 20; i++) {
			Query q = new Query(then, "help", "15036666666");
			q.setResponse("no");
			q.setTimeResponded(new Date());
			log(q);
		}
	}
}
