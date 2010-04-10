package persistance;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/** A log of all requests recieved. */
public class Log {

	/**
	 * A helper class so that the counts in the hash table can be easily
	 * incremented.
	 */
	class Int {

		int i;

		Int(int i) {
			this.i = i;
		}

	}

	/** The file that the data will be saved and restored from. */
	// private static final String DATA_FILE = ".data";

	/** The total number of text messages processed by this system, ever. */
	private static long total;

	/** The individual keyword totals. */
	private final static Map<String, Int> wordCounts = new HashMap<String, Int>();

	private static final Log LOG = new Log();

	public static final Log getLog() {
		return LOG;
	}

	public Log() {
		loadCounts();
	}

	protected static final void loadCounts() {
		total = 0;
	}

	public static void saveCounts() {
	}

	public static void log(String message, final GregorianCalendar time,
			String response) {
		getLog().record(message, time, response);
	}

	public void record(String message, final GregorianCalendar time,
			String response) {
		total++;
		Int value = wordCounts.get(message.toLowerCase());
		if (value == null) {
			wordCounts.put(message, new Int(1));
		} else {
			value.i++;
		}

		// save to file system periodically in case of failure
		if (total % 10 == 0) {
			saveCounts();
		}

	}

}
