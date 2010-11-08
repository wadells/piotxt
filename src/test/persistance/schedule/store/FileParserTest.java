package persistance.schedule.store;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static time.Day.*;

import java.io.File;
import java.util.Iterator;

import org.junit.*;

import persistance.schedule.Stop;
import time.Day;
import time.Time;

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
		schedule = new ListSchedule(keywords);
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
		
		for(Day d : new Day[] { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY } ) {
			assertStopInSchedule(new Stop("First Stop", "stop1", new Time(d, 19, 30)));
			assertStopInSchedule(new Stop("Second Stop", "stop1", new Time(d, 20, 16)));
		}
	}
	
	@Test
	public void testOverwritesStops() {
		fail("Not yet implemented.");
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
		Iterator<Stop> stops = schedule.getStops(stop.getTime(), stop.getTime()).iterator(); 
		assertTrue(stops.hasNext());
		Stop next = stops.next();
		// We intentionally don't check the name
		assertEquals(stop.getKeyword(), next.getKeyword());
		assertEquals(stop.getDirection(), next.getDirection());
		assertEquals(stop.getTime(), next.getTime());		
	}

}
