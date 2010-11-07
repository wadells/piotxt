package time;

import static org.junit.Assert.*;
import static time.Day.MONDAY;

import java.util.*;

import org.junit.Test;

public class DayTest {
	
	Day[] days;
	
	public static void assertEqualsDays(Day[] expected, Set<Day> actual) {
		EnumSet<Day> expctd = EnumSet.copyOf(Arrays.asList(expected));
		assertEquals(expctd.size(), actual.size());
		assertTrue(String.format("Expected %s but was %s", expctd, actual), actual.containsAll(expctd));
	}
	
	@Test
	public void testDaysBetween() {
		days = new Day[]{ MONDAY }; 
		
		
		
		fail("Not yet implemented");
	}

}
