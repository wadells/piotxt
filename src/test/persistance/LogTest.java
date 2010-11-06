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

public class LogTest {

	// constants
	private final Date then = new Date();
	private final String phone = "123-456-7890";
	private static final File testFile = new File("test.log");

	private Log log;
	private Date now;

	@Before
	public void setUp() {
		log = new Log(testFile);
		now = new Date();
	}

	@Test
	public void testFlatten() {
		String nasty = "\ta\tb \nc\t \n d e\nf g\n";
		String flattened = flatten(nasty);
		String expected = "a b c d e f g";
		assertEquals(expected, flattened);

	}

	/*
	 * BEFORE EDITING THIS: Are you sure you want to change the log format? This
	 * may make past logs incompatible.
	 */
	@Test
	public void testFormat() {
		Query q = new Query(then, "hour", phone);
		q.setTimeResponded(now);
		String format = Log.queryToString(q);
		assertTrue(format.charAt(0) == '[');
		assertTrue(format.charAt(20) == ']');
		assertTrue(format.charAt(22) == '[');
		assertTrue(format.charAt(29) == ']');
		assertTrue(format.charAt(40) == '{');
		String[] pts = format.split(" ");
		assertEquals(5, pts.length);
		String date = pts[0].substring(1, pts[0].length() - 1);
		Date time = null;
		try {
			time = LOG_DATE_FORM.parse(date);
		} catch (ParseException e) {
			assert false : "Date in log unparseable: " + date;
		}
		assertEquals(then.toString(), time.toString());
		String keyword = pts[3].substring(1, pts[3].length() - 1);
		assertTrue(keyword.equals("hour"));

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
			log.record(new Query(then, "help", phone));
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
			log.record(new Query(then, "hour", phone));
		}
		assertEquals(n, countLines(testFile));
		for (int i = 0; i < n; i++) {
			log.record(new Query(then, "help", phone));
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
