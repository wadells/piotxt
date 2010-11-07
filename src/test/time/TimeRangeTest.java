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
	
	@Test
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
		assertTrue(r.isInRange(new Time(SATURDAY, 15, 33)));
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
	public void testIntersection() {
		// Test null intersection
		Time t1 = new Time(SATURDAY, 12, 00);
		Time t2 = new Time(SUNDAY, 23, 59);
		assertTrue(new TimeRange(t1, t2).intersect(new TimeRange(t2, t1)).isEmpty());
		// Ensure this agrees with doesIntersect
		assertFalse(new TimeRange(t1, t2).doesIntersect(new TimeRange(t2, t1)));
		
		// Test same start, different end
		Time t3 = new Time(WEDNESDAY, 15, 20);
		TimeRange i = new TimeRange(t1, t2).intersect(new TimeRange(t1, t3));
		assertTrue(new TimeRange(t1, t2).doesIntersect(new TimeRange(t1, t3)));
		assertTrue(i.isInRange(t1));
		assertTrue(i.isInRange(new Time(SUNDAY, 6, 30)));
		assertTrue(i.isInRange(new Time(SUNDAY, 23, 58)));
		assertFalse(i.isInRange(t2));
		assertFalse(i.isInRange(new Time(MONDAY, 5, 58)));
		
		// Test same end, different start
		t3 = new Time(WEDNESDAY, 15, 20);
		i = new TimeRange(t1, t3).intersect(new TimeRange(t2, t3));
		assertTrue(i.isInRange(t2));
		assertTrue(i.isInRange(new Time(MONDAY, 5, 58)));
		assertTrue(i.isInRange(new Time(WEDNESDAY, 15, 19)));
		assertFalse(i.isInRange(t1));
		assertFalse(i.isInRange(t3));
		assertFalse(i.isInRange(new Time(SUNDAY, 6, 30)));
		
		// Test subset
		Time t4 = new Time(TUESDAY, 9, 00);
		i = new TimeRange(t1, t3).intersect(new TimeRange(t2, t4));
		assertTrue(i.isInRange(t2));
		assertTrue(i.isInRange(new Time(MONDAY, 5, 58)));
		assertFalse(i.isInRange(t1));
		assertFalse(i.isInRange(t3));
		assertFalse(i.isInRange(t4));
		assertFalse(i.isInRange(new Time(TUESDAY, 12, 15)));
		
		// Test reflexivity (same as above, but the intersect call is reversed)
		i = new TimeRange(t2, t4).intersect(new TimeRange(t1, t3));
		assertTrue(i.isInRange(t2));
		assertTrue(i.isInRange(new Time(MONDAY, 5, 58)));
		assertFalse(i.isInRange(t1));
		assertFalse(i.isInRange(t3));
		assertFalse(i.isInRange(t4));
		assertFalse(i.isInRange(new Time(TUESDAY, 12, 15)));
	}

}
