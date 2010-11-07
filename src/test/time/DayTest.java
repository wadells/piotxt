package time;

import static org.junit.Assert.*;
import static time.Day.*;

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
	public void testDayRange() {
		DayRange d = new DayRange(MONDAY, MONDAY);
		assertTrue(d.hasNext());
		assertEquals(MONDAY, d.next());
		assertFalse(d.hasNext());
		
		d = new DayRange(MONDAY, WEDNESDAY);
		assertTrue(d.hasNext());
		assertEquals(MONDAY, d.next());
		assertEquals(TUESDAY, d.next());
		assertEquals(WEDNESDAY, d.next());
		assertFalse(d.hasNext());

		d = new DayRange(FRIDAY, MONDAY);
		assertTrue(d.hasNext());
		assertEquals(FRIDAY, d.next());
		assertEquals(SATURDAY, d.next());
		assertEquals(SUNDAY, d.next());
		assertEquals(MONDAY, d.next());
		assertFalse(d.hasNext());
	}
	
	@Test
	public void testDaysBetween() {
		days = new Day[]{ MONDAY }; 
		assertEqualsDays(days, Day.daysBetween(MONDAY, MONDAY));

		days = new Day[]{ MONDAY, TUESDAY }; 
		assertEqualsDays(days, Day.daysBetween(MONDAY, TUESDAY));
		
		days = new Day[]{ MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }; 
		assertEqualsDays(days, Day.daysBetween(MONDAY, SUNDAY));

		days = new Day[]{ MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }; 
		assertEqualsDays(days, Day.daysBetween(SUNDAY, SATURDAY));

		days = new Day[]{ SATURDAY, SUNDAY }; 
		assertEqualsDays(days, Day.daysBetween(SATURDAY, SUNDAY));

		days = new Day[]{ WEDNESDAY, THURSDAY, FRIDAY }; 
		assertEqualsDays(days, Day.daysBetween(WEDNESDAY, FRIDAY));
	}

}
