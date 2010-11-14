package persistance;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.junit.*;

import core.Query;
import static persistance.Log.*;

public class MessageLogTest {

	// a file that is created and deleted during testing
	private static final File testFile = new File("log/test_message.log");
	// used througout the tests
	private final Date then = new Date();
	private final Date now = new Date(then.getTime() + 54321l);
	private final String phone = "+15551118888";
	Query query = new Query(then, "keyword {&\\rawr\"junk data", phone);
	private Log log;

	@Before
	public void setUp() {
		log = new Log(testFile);
		query.setTimeResponded(now);
		query.setKeyword("keyword");
	}

	@Test
	public void testFlatten() {
		String nasty = "\ta\tb \nc\t \n d e\nf g\n";
		String flattened = flatten(nasty);
		String expected = "a b c d e f g";
		assertEquals(expected, flattened);

	}

	/**
	 * BEFORE EDITING THIS: Are you sure you want to change the log format? This
	 * may make past logs incompatible.
	 * 
	 * Current log format:
	 * 
	 * <pre>
	 * fonehash S[MM/dd/yy@hh:mm:ss] R[MM/dd/yy@hh:mm:ss] (systinfo) {keyword} "flattened text of message"
	 * </pre>
	 */
	@Test
	public void testFormat() {
		String test = Log.queryToString(query);

		// check whitespace
		assertEquals(' ', test.charAt(8));
		assertEquals(' ', test.charAt(29));
		assertEquals(' ', test.charAt(50));
		assertEquals(' ', test.charAt(61));

		// split on leftmost quotations
		String[] pts = test.split("\"", 2);
		// check body
		String b = pts[1].substring(0, pts[1].length() - 1); // message body
		assertEquals(query.getBody(), b);

		// check other data
		String[] parts = pts[0].split(" ");

		// eight char phonehash
		assertEquals(8, parts[0].length());

		// sent time
		assertEquals(20, parts[1].length());
		String sent = parts[1].substring(2, parts[1].length() - 1);
		Date s = null;
		try {
			s = LOG_DATE_FORM.parse(sent);
		} catch (ParseException e) {
			assert false : "Sent date in log unparseable: " + parts[1];
		}
		 assertEquals(then.toString(), s.toString());

		// responded time
		assertEquals(20, parts[2].length());
		String recieved = parts[2].substring(2, parts[2].length() - 1);
		Date r = null;
		try {
			r = LOG_DATE_FORM.parse(recieved);
		} catch (ParseException e) {
			assert false : "Recieve date in log unparseable: " + parts[2];
		}
		 assertEquals(now.toString(), r.toString());

		// sysinfo
		assertEquals(10, parts[3].length());
		assertEquals('(', parts[3].charAt(0));
		assertEquals(')', parts[3].charAt(9));

		// keyword
		int lastchar = parts[4].length() - 1;
		assertEquals('{', parts[4].charAt(0));
		assertEquals('}', parts[4].charAt(lastchar));
		String k = parts[4].substring(1, lastchar);
		assertTrue(k.equals("keyword"));
	}

	@Test
	public void testLog() {
		int n = 9;
		for (int i = 0; i < 9; i++) {
			log.record(new Query(then, "help", phone));
		}
		assertEquals(n, log.getTotal());
	}

	@Test
	public void testSave() {
		int n = 30;
		for (int i = 0; i < n; i++) {
			log.record(query);
		}
		int lines = countLines(testFile);
		assertEquals(n, lines);
		assertEquals(lines, log.getTotal());
		testFile.delete();
	}

	@Test
	public void testAppend() {
		int n = 20;
		// record first batch
		for (int i = 0; i < n; i++) {
			log.record(query);
		}
		assertEquals(n, countLines(testFile));
		for (int i = 0; i < n; i++) {
			log.record(query);
		}
		assertEquals(2 * n, countLines(testFile));
		testFile.delete();
	}

	/**
	 * A helper method tat counts the number of non-empty and non-comment lines
	 * in a .log file.
	 */
	public static int countLines(File file) {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String line;
			int i = 0;
			while ((line = in.readLine()) != null) {
				if (!line.isEmpty() && !line.startsWith("#")) {
					i++;
				}
			}
			in.close();
			return i;
		} catch (IOException e) {
			assert false : "Could not read file: " + testFile.getAbsolutePath();
		}
		return -1;
	}

}
