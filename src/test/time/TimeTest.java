package time;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

import time.Day;
import time.Time;

import static java.util.Calendar.MINUTE;
import static org.junit.Assert.*;
import static time.Day.*;
import static time.Time.parse;
import static time.Time.timeFrom;

/**
 * Due to the size of the time space, even over this limited domain, many of
 * these tests depend on when they were run for their inputs. They should always
 * all pass, however.
 * 
 */
public class TimeTest {

	public static final String TIME_TESTS_FULL_PROP = "time.tests.full";

	private Time time;

	protected void assertTimeEqualToCalendar(Calendar cal, Time time) {
		assertTimeEqualToCalendar("", cal, time);
	}

	/** Compares the actual Time object to the (expected) Calendar object. */
	protected void assertTimeEqualToCalendar(String message, Calendar cal, Time time) {
		assertEquals(message, cal.get(Calendar.MINUTE), time.getMinute());
		assertEquals(message, cal.get(Calendar.HOUR_OF_DAY), time.getHour());
		assertEquals(message, Day.valueOf(cal.get(Calendar.DAY_OF_WEEK)), time.getDay());
	}

	@Test
	public void testTime() {
		Time time = new Time(SUNDAY, 22, 10); // 22:10
		assertEquals(22, time.getHour());
		assertEquals(10, time.getMinute());
		assertEquals(SUNDAY, time.getDay());
		assertEquals("10:10pm", time.toString());
		assertEquals("22:10", time.toString(true));

		String[] shrtwk = DateFormatSymbols.getInstance().getShortWeekdays();

		time = new Time(SUNDAY, 10, 59); // 10:10
		assertEquals("10:59am", time.toString());
		assertEquals("10:59", time.toString(true));
		assertEquals(shrtwk[Calendar.SUNDAY] + " 10:59", time.toString(true, true));

		time = new Time(WEDNESDAY, 0, 01); // 00:59
		assertEquals("12:01am", time.toString());
		assertEquals("00:01", time.toString(true));
		assertEquals(shrtwk[Calendar.WEDNESDAY] + " 00:01", time.toString(true, true));
		assertEquals(shrtwk[Calendar.WEDNESDAY] + " 12:01am", time.toString(false, true));
	}

	@Test
	public void testIllegal() {
		try {
			time = new Time(SUNDAY, -1, 00);
			fail("Illegal constructor arguments did not result in an exception.");
		} catch (IllegalArgumentException ex) {
			// Do nothing
		}

		try {
			time = new Time(SUNDAY, 24, 00);
			fail("Illegal constructor arguments did not result in an exception.");
		} catch (IllegalArgumentException ex) {
			// Do nothing
		}

		try {
			time = new Time(SUNDAY, 2, -1);
			fail("Illegal constructor arguments did not result in an exception.");
		} catch (IllegalArgumentException ex) {
			// Do nothing
		}

		try {
			time = new Time(SUNDAY, 2, 60);
			fail("Illegal constructor arguments did not result in an exception.");
		} catch (IllegalArgumentException ex) {
			// Do nothing
		}
	}

	@Test
	public void testCompare() {
		time = new Time(MONDAY, 0, 01);
		Time t1 = new Time(SATURDAY, 0, 00);
		try {
			assertTrue(time.compareTo(t1) < 0 && t1.compareTo(time) > 0);
			fail("No exception thrown");
		} catch (UnsupportedOperationException e) {
			// We expect to be unable to compare different days
		}

		t1 = new Time(MONDAY, 12, 0);
		assertTrue(t1.compareTo(time) > 0);

		time = new Time(MONDAY, 12, 0);
		assertTrue(time.compareTo(t1) == time.compareTo(t1) && time.compareTo(t1) == 0);

		t1 = new Time(MONDAY, 11, 59);
		assertTrue(time.compareTo(t1) > 0 && t1.compareTo(time) < 0);
	}

	@Test
	public void testEpoch() {
		Calendar cal = Calendar.getInstance();

		cal.setTimeInMillis(0);
		assertTimeEqualToCalendar(cal, new Time(0));

		long ms = new Random().nextLong();
		cal.setTimeInMillis(ms);
		assertTimeEqualToCalendar(cal, new Time(ms));
	}

	@Test
	public void testNow() {
		Calendar now1, now2;

		// Ensure that all three "nows" occur within the same minute
		do {
			now1 = Calendar.getInstance();
			time = Time.now();
			now2 = Calendar.getInstance();
		} while (now1.get(MINUTE) != now2.get(MINUTE));

		assertTimeEqualToCalendar(now1, time);
	}

	@Test
	public void testAddDays() {
		int[] toAdd = new int[] { -8, -7, -5, -1, 0, 1, 4, 7, 12 };

		Calendar cal = Calendar.getInstance();
		// Promote seconds to hours so the test is less dependent on when it's
		// run
		long start = System.currentTimeMillis() * 3600;

		for (int d : toAdd) {
			cal.setTimeInMillis(start);
			cal.add(Calendar.DAY_OF_WEEK, d);

			time = new Time(start).addDays(d);

			assertTimeEqualToCalendar(String.format("Adding %d days to , should be %s", d, new Time(start).toString(true, true), cal.getTime()), cal, time);
		}

		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 55);
		time = new Time(SATURDAY, 23, 55);

		time = new Time(SATURDAY, 23, 55).addDays(1);
		cal.add(Calendar.DAY_OF_WEEK, 1);

		assertTimeEqualToCalendar(cal, time);

		// Test zero
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);

		time = new Time(SATURDAY, 12, 0).addDays(-7);
		cal.add(Calendar.DAY_OF_WEEK, -7);

		assertTimeEqualToCalendar(cal, time);
	}

	@Test
	public void testAddHours() {
		int[] toAdd = new int[] { -178, -25, -24, -6, -1, 0, 1, 2, 8, 24, 200 };

		Calendar cal = Calendar.getInstance();
		// Promote seconds to hours so the test is less dependent on when it's
		// run
		long start = System.currentTimeMillis() * 3600;

		for (int h : toAdd) {
			cal.setTimeInMillis(start);
			cal.add(Calendar.HOUR_OF_DAY, h);

			time = new Time(start).addHours(h);

			assertTimeEqualToCalendar(String.format("Adding %d hours to %s, should be %s (start was %d)", h, new Time(start).toString(true, true), cal.getTime(), start), cal, time);
		}
		// TODO: Look into these. Bug in util.Calendar? 
		/*java.lang.AssertionError: Adding -178 hours to Wed 18:05, should be Wed Mar 06 07:05:52 PST 149039 expected:<7> but was:<8>
		at org.junit.Assert.fail(Assert.java:71)
		at org.junit.Assert.failNotEquals(Assert.java:451)
		at org.junit.Assert.assertEquals(Assert.java:99)
		at time.TimeTest.assertTimeEqualToCalendar(TimeTest.java:36)
		at time.TimeTest.testAddHours(TimeTest.java:195) */

		/* java.lang.AssertionError: Adding -178 hours to Thu 12:25, should be Thu Mar 07 01:25:22 PST 149039 expected:<1> but was:<2>
	at org.junit.Assert.fail(Assert.java:71)
	at org.junit.Assert.failNotEquals(Assert.java:451)
	at org.junit.Assert.assertEquals(Assert.java:99)
	at time.TimeTest.assertTimeEqualToCalendar(TimeTest.java:36)
	at time.TimeTest.testAddHours(TimeTest.java:195)*/
		
		/*java.lang.AssertionError: Adding -178 hours to Fri 15:46, should be Fri Mar 08 04:46:33 PST 149039 expected:<4> but was:<5>
		at org.junit.Assert.fail(Assert.java:71)
		at org.junit.Assert.failNotEquals(Assert.java:451)
		at org.junit.Assert.assertEquals(Assert.java:99)
		at time.TimeTest.assertTimeEqualToCalendar(TimeTest.java:36)
		at time.TimeTest.testAddHours(TimeTest.java:195)
		*/

		// Test week wrap
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 55);

		time = new Time(SATURDAY, 23, 55).addHours(2);
		cal.add(Calendar.HOUR, 2);

		assertTimeEqualToCalendar(cal, time);

		// Test zero
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 55);

		time = new Time(SATURDAY, 0, 55).addHours(-168);
		// cal.add(Calendar.HOUR, 0);

		assertTimeEqualToCalendar(cal, time);
	}

	@Test
	public void testAddMinutes() {
		int[] toAdd = new int[] { -127, -60, -12, -5, -1, 0, 1, 7, 14, 460 };

		Calendar cal = Calendar.getInstance();
		// Promote seconds to hours so the test is less start time dependent
		long start = System.currentTimeMillis() * 3600;

		for (int m : toAdd) {
			cal.setTimeInMillis(start);
			cal.add(Calendar.MINUTE, m);

			time = new Time(start).addMinutes(m);

			assertTimeEqualToCalendar(String.format("Adding %d minutes to %s, should be %s", m, new Time(start).toString(true, true), cal.getTime()), cal, time);
		}

		// Test week wrap
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 55);

		time = new Time(SATURDAY, 23, 55).addMinutes(10);
		cal.add(Calendar.MINUTE, 10);

		assertTimeEqualToCalendar(cal, time);

		// Test zeros
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);

		time = new Time(SATURDAY, 12, 0).addMinutes(-60);
		cal.add(Calendar.MINUTE, -60);

		assertTimeEqualToCalendar(cal, time);

		// Test all zeros
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);

		time = new Time(SATURDAY, 0, 0).addMinutes(-1440);
		cal.add(Calendar.MINUTE, -1440);

		assertTimeEqualToCalendar(cal, time);
	}

	@Test
	public void testParse() {
		Time time = new Time(SUNDAY, 22, 10); // 22:10
		assertTrue(time.equalToTime(parse(time.toString(), SUNDAY)));
		assertTrue(time.equalToTime(parse(time.toString(true), SUNDAY)));
		
		try {
			parse("77:221am", SUNDAY);
			fail("Did not throw expected exception.");
		} catch(TimeFormatException e) {
			// pass
		}
		try {
			parse("06:21zz", SUNDAY);
			fail("Did not throw expected exception.");
		} catch(TimeFormatException e) {
			// pass
		}
		try {
			parse("22:07pm", SUNDAY);
			fail("Did not throw expected exception.");
		} catch(TimeFormatException e) {
			// pass
		}
	}
	
	@Test
	public void testTimeFromDate() {
		long epoch = System.currentTimeMillis();
		Time t = new Time(epoch);
		Date d = new Date(epoch);
		
		assertTrue(t.equalToTime(timeFrom(d)));
	}
}
