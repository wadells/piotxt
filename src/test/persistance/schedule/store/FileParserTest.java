package persistance.schedule.store;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static persistance.schedule.Stop.Direction.INBOUND;
import static persistance.schedule.Stop.Direction.OUTBOUND;
import static time.Day.FRIDAY;
import static time.Day.MONDAY;
import static time.Day.SATURDAY;
import static time.Day.THURSDAY;
import static time.Day.TUESDAY;
import static time.Day.WEDNESDAY;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import persistance.schedule.Stop;
import time.Day;
import time.Time;
import time.TimeRange;
import core.Keywords;

/**
 * Tests for the FileParser. Below, a "graceful" error is one that has real
 * English before the stacktrace.
 */
public class FileParserTest {

	MutableSchedule schedule;
	FileParser parser;
	Keywords keywords;

	@Before
	public void setup() {
		keywords = new Keywords();
		schedule = new DefaultSchedule();
		// As long as we don't call parseFile, this is fine.
		parser = new FileParser(schedule, keywords, new File(""));
	}

	@Test
	public void testIgnoreComment() {
		int count = keywords.definitions().size();
		parser.parseLine("# This is a comment");
		parser.parseLine("   # This is a comment");
		parser.parseLine(" \t # This is a comment");

		parser.parseLine("# Pioneer Square, square");
		parser.parseLine("   # Pioneer Square, square");
		parser.parseLine(" \t # Pioneer Square, square");

		assertEquals(count, keywords.definitions().size());
	}

	@Test
	public void testAddsKeyword() {
		parser.parseLine(" Stop 1, stop1");
		parser.parseLine("    Stop 2  \t ,   stop2");
		parser.parseLine(" \t  Stop 3  ,  \t\t stop3");
		parser.parseLine("Stop 4,stop4");

		for (int i : new int[] { 1, 2, 3, 4, })
			assertEquals(format("Stop %d", i), keywords.getDefinition(format("stop%d", i)));
	}

	@Test
	public void testRequiresSchedule() {
		// Add in a keyword for our stop
		parser.parseLine("Stop, stop");
		// Make sure we can't add a time for the stop
		assertGracefulError(FileParser.NoScheduleParseException.class, "stop, 8:30pm");
		// Make a schedule, and try again
		parser.parseLine("Schedule: Monday-Friday 7:00pm-9:00pm");
		parser.parseLine("stop, 8:30pm");
	}
	

	@Test
	public void testAddStops() {
		parser.parseLine("First Stop, stop1");
		parser.parseLine("Second Stop, stop2");
		parser.parseLine("Schedule: Monday-Friday 7:00pm-9:00pm");
		parser.parseLine("stop1, 7:30pm");
		parser.parseLine("stop2, 8:16pm");
		
		for(Day d : new Day[] { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY } ) {
			assertStopInSchedule(new Stop("stop1", new Time(d, 19, 30)));
			assertStopInSchedule(new Stop("stop2", new Time(d, 20, 16)));
		}
	}
	
	@Test
	public void testDirection() {
		parser.parseLine("First Stop, stop1");
		parser.parseLine("Second Stop, stop2");
		parser.parseLine("Schedule: Monday-Friday 7:00pm-9:00pm");
		parser.parseLine("stop1, 7:30pm o, 8:54pmo");
		parser.parseLine("stop2, 8:16pm i");
		
		for(Day d : new Day[] { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY } ) {
			assertStopInSchedule(new Stop("stop1", new Time(d, 19, 30), OUTBOUND));
			assertStopInSchedule(new Stop("stop2", new Time(d, 20, 16), INBOUND));
			assertStopInSchedule(new Stop("stop1", new Time(d, 20, 54), OUTBOUND));
		}
	}
	
	@Test
	public void testOvernightSchedule() {
		parser.parseLine("First Stop, stop1");
		parser.parseLine("Second Stop, stop2");
		parser.parseLine("Schedule: Friday-Saturday 7:00pm-2:00am");
		parser.parseLine("stop1, 7:30pm, 11:59pm, 12:17, 1:59am");
		parser.parseLine("stop2, 8:16pm, 23:44, 12:43am");
		
		for(Day d : new Day[] { FRIDAY, SATURDAY } ) {
			assertStopInSchedule(new Stop("stop1", new Time(d, 19, 30)));
			assertStopInSchedule(new Stop("stop1", new Time(d, 23, 59)));
			assertStopInSchedule(new Stop("stop1", new Time(d.add(1), 00, 17)));
			assertStopInSchedule(new Stop("stop1", new Time(d.add(1), 1, 59)));
			assertStopInSchedule(new Stop("stop2", new Time(d, 20, 16)));
			assertStopInSchedule(new Stop("stop2", new Time(d, 23, 44)));
			assertStopInSchedule(new Stop("stop2", new Time(d.add(1), 00, 43)));
		}
	}
	
	@Test
	public void testOverwritesStops() {
		parser.parseLine("First Stop, stop1");
		parser.parseLine("Second Stop, stop2");
		parser.parseLine("Schedule: Monday-Friday 7:00pm-9:00pm");
		parser.parseLine("stop1, 7:30pm, 8:45pm");
		parser.parseLine("stop2, 8:16pm");
		parser.parseLine("Schedule: Friday 7:00pm-9:00pm");
		parser.parseLine("stop1, 7:40pm");
		parser.parseLine("stop2, 8:23pm");
		
		for(Day d : new Day[] { MONDAY, TUESDAY, WEDNESDAY, THURSDAY } ) {
			assertStopInSchedule(new Stop("stop1", new Time(d, 19, 30)));
			assertStopInSchedule(new Stop("stop1", new Time(d, 20, 45)));
			assertStopInSchedule(new Stop("stop2", new Time(d, 20, 16)));
		}
		
		assertEquals(new Stop("stop1", new Time(FRIDAY, 19, 40)), schedule.getNextStop("stop1", new Time(FRIDAY, 19, 00)));
		assertEquals(new Stop("stop2", new Time(FRIDAY, 20, 23)), schedule.getNextStop("stop2", new Time(FRIDAY, 19, 00)));
		// Ensure the 8:45 stop is gone
		assertFalse(schedule.getStops(new TimeRange(new Time(FRIDAY, 20, 45), new Time(FRIDAY, 20, 46))).iterator().hasNext());		
	}

	@Test
	public void testMagicAmPm() {
		parser.parseLine("Stop, stop");
		parser.parseLine("Schedule: Monday-Friday 7:00am-2:00am");
		parser.parseLine("stop, 8:30, 12:01pm, 12:30, 7:00, 12:10, 12:50, 1:40");
		
		for(Day d : new Day[] { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY } ) {
			assertStopInSchedule(new Stop("stop", new Time(d, 8, 30)));
			assertStopInSchedule(new Stop("stop", new Time(d, 12, 01)));
			assertStopInSchedule(new Stop("stop", new Time(d, 12, 30)));
			assertStopInSchedule(new Stop("stop", new Time(d, 19, 00)));
			assertStopInSchedule(new Stop("stop", new Time(d.add(1), 00, 10)));
			assertStopInSchedule(new Stop("stop", new Time(d.add(1), 00, 50)));
			assertStopInSchedule(new Stop("stop", new Time(d.add(1), 01, 40)));
		}
	}
	
	@Test
	public void testGracefulKeywordError() {
		Class<? extends ParseException> simpleKeywordException = FileParser.KeywordParseException.class;
		assertGracefulError(simpleKeywordException, " Stop 1");
		assertGracefulError(simpleKeywordException, " Stop 1,  ");
		assertGracefulError(simpleKeywordException, "    Stop 2, stop2, stop4, stop5,");
		assertGracefulError(simpleKeywordException, "Stop 3,");
		
		Class<? extends ParseException> illegalKeywordException = FileParser.IllegalKeywordParseException.class;
		assertGracefulError(illegalKeywordException, "Stop 3,stop 3");
		assertGracefulError(illegalKeywordException, "Stop 3,st\top3");
	}

	@Test
	public void testGracefulScheduleError() {
		// parser.parseLine("Schedule:Monday\t-  Friday    7:00pm\t\t-9:00pm");
		fail("Not yet implemented.");
	}

	@Test
	public void testGracefulStopError() {
		// TODO: Test case where a stop doesn't fit in the current schedule
		// TODO: Test case where a stop's keyword is invalid/unknown
		fail("Not yet implemented.");
	}

	protected void assertGracefulError(Class<? extends ParseException> expected, String input) {
		try {
			parser.parseLine(input);
			fail("Parser failed to throw an exception on invalid input.");
		} catch (ParseException e) {
			// Is it the type we expected to catch?
			if (!expected.isInstance(e))
				// We re-throw the caught exception here because, arguably, the
				// debugging programmer cares more about what actually happened
				// and where rather than that the wrong kind of exception was
				// thrown.
				throw e;
			// else pass
		}
	}
	
	protected void assertStopInSchedule(Stop stop) {
		Iterator<Stop> stops = schedule.getStops(new TimeRange(stop.getTime(), stop.getTime().addMinutes(1))).iterator(); 
		assertTrue(format("Could not find stop '%s' in schedule.", stop), stops.hasNext());
		Stop next = stops.next();

		assertEquals(stop.getKeyword(), next.getKeyword());
		assertEquals(stop.getDirection(), next.getDirection());
		assertEquals(stop.getTime(), next.getTime());		
	}

}
