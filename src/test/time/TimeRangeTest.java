package time;

import static org.junit.Assert.*;
import static time.Day.*;
import static time.TimeRange.emptyRange;

import org.junit.Before;
import org.junit.Test;

public class TimeRangeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsEmpty() {
		Time t1 = new Time(MONDAY, 00, 00);
		Time t2 = new Time(SUNDAY, 23, 59);
		assertFalse(new TimeRange(t2, t1).isEmpty());
		assertFalse(new TimeRange(t1, t2).isEmpty());
		assertTrue(new TimeRange(t1, t1).isEmpty());
		
		t1 = new Time(SATURDAY, 23, 59);
		assertFalse(new TimeRange(t1, t2).isEmpty());
		
		t1 = new Time(SUNDAY, 23, 59);
		assertTrue(new TimeRange(t1, t2).isEmpty());
		
		assertTrue(emptyRange().isEmpty());
	}

	@Test
	public void testIsInRange() {
		Time t1 = new Time(SATURDAY, 12, 00);
		Time t2 = new Time(SUNDAY, 23, 59);
		
		// Test an empty range
		assertFalse(emptyRange().isInRange(t1));
		assertFalse(emptyRange().isInRange(t2));
				
		TimeRange r = new TimeRange(t1, t2);
		assertFalse(r.isEmpty());
		
		// Test exclusion
		assertFalse(r.isInRange(new Time(MONDAY, 00, 00)));
		assertFalse(r.isInRange(new Time(MONDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(TUESDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(WEDNESDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(THURSDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(FRIDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(SATURDAY, 00, 00)));
		assertFalse(r.isInRange(new Time(SATURDAY, 11, 59)));
		
		// Test inclusion
		assertTrue(r.isInRange(new Time(SATURDAY, 13, 00)));
		assertTrue(r.isInRange(new Time(SATURDAY, 23, 45)));
		assertTrue(r.isInRange(new Time(SUNDAY, 1, 21)));
		assertTrue(r.isInRange(new Time(SUNDAY, 5, 33)));
		assertTrue(r.isInRange(new Time(SUNDAY, 19, 00)));
		
		// Test boundaries
		assertTrue(r.isInRange(t1));
		assertFalse(r.isInRange(t2));
	}
	
	public void testIsInRangeSameDay() {
		Time t1 = new Time(SATURDAY, 12, 00);
		Time t2 = new Time(SATURDAY, 23, 59);
				
		TimeRange r = new TimeRange(t1, t2);
		assertFalse(r.isEmpty());
		
		// Test boundaries
		assertTrue(r.isInRange(t1));
		assertFalse(r.isInRange(t2));
		
		// Test inclusion
		assertTrue(r.isInRange(new Time(SATURDAY, 13, 00)));
		assertTrue(r.isInRange(new Time(SATURDAY, 1, 21)));
		assertTrue(r.isInRange(new Time(SATURDAY, 5, 33)));
		assertTrue(r.isInRange(new Time(SATURDAY, 19, 00)));
		assertTrue(r.isInRange(new Time(SATURDAY, 23, 45)));
		
		// Test exclusion
		assertFalse(r.isInRange(new Time(SATURDAY, 00, 00)));
		assertFalse(r.isInRange(new Time(SATURDAY, 11, 59)));
		assertFalse(r.isInRange(new Time(MONDAY, 00, 00)));
		assertFalse(r.isInRange(new Time(MONDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(TUESDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(WEDNESDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(THURSDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(FRIDAY, 14, 15)));
		assertFalse(r.isInRange(new Time(SUNDAY, 14, 15)));
	}

	@Test
	public void testIntersect() {
		fail("Not yet implemented");
	}

	@Test
	public void testOverlaps() {
		fail("Not yet implemented");
	}

}
