package persistance.schedule.store;

import static org.junit.Assert.*;
import static time.Day.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import persistance.schedule.Stop;
import persistance.schedule.Stop.Direction;
import time.Day;
import time.Time;
import time.TimeRange;

// TODO: Test idempotency of adding stops
public abstract class MutableScheduleTest {

	private MutableSchedule schedule;
	
	@Before
	public void setUp() throws Exception {
		schedule = createSchedule();
	}
	
	protected abstract MutableSchedule createSchedule();

	@Test
	public void testAddStop() {
		Stop s = new Stop("test", new Time(MONDAY, 12, 00));
		
		assertFalse(schedule.stopInSchedule(s));
		schedule.addStop(s);
		assertTrue(schedule.stopInSchedule(s));
	}
	
	@Test
	public void testRemoveStops() {
		Stop s0 = new Stop("test0", new Time(MONDAY, 12, 00));
		Stop s1 = new Stop("test1", new Time(MONDAY, 12, 10));
		Stop s2 = new Stop("test2", new Time(MONDAY, 12, 20));
		
		schedule.addStop(s0);
		schedule.addStop(s1);
		schedule.addStop(s2);
		assertTrue(schedule.stopInSchedule(s0));
		assertTrue(schedule.stopInSchedule(s1));
		assertTrue(schedule.stopInSchedule(s2));
		
		schedule.removeStops(new TimeRange(new Time(MONDAY, 12, 00), new Time(MONDAY, 12, 20)));
		assertFalse(schedule.stopInSchedule(s0));
		assertFalse(schedule.stopInSchedule(s1));
		assertTrue(schedule.stopInSchedule(s2));
		
		// Test removing over week/day boundary
		Stop s3 = new Stop("test3", new Time(SATURDAY, 23, 59));
		Stop s4 = new Stop("test4", new Time(SUNDAY, 00, 01));
		
		schedule.addStop(s3);
		schedule.addStop(s4);
		assertTrue(schedule.stopInSchedule(s3));
		assertTrue(schedule.stopInSchedule(s4));
		schedule.removeStops(new TimeRange(new Time(SATURDAY, 23, 00), new Time(SUNDAY, 1, 00)));
		assertFalse(schedule.stopInSchedule(s3));
		assertFalse(schedule.stopInSchedule(s4));
		
	}
	
	@Test
	public void testDirection() {
		Stop si = new Stop("test", new Time(MONDAY, 12, 00), Direction.INBOUND);
		Stop so = new Stop("test", new Time(MONDAY, 12, 00), Direction.OUTBOUND);
		
		schedule.addStop(si);
		assertTrue(schedule.stopInSchedule(si));
		assertFalse(schedule.stopInSchedule(so));

		schedule.addStop(so);
		assertTrue(schedule.stopInSchedule(si));
		assertTrue(schedule.stopInSchedule(so));
	}
	
	@Test
	public void testGetNextStop() {
		for(Day d : Day.values()) {
			schedule.addStop(new Stop("test", new Time(d, 12, 00)));
			schedule.addStop(new Stop("wrong", new Time(d, 12, 02)));
		}
		
		for(Day d : Day.values()) {
			assertTrue(schedule.stopInSchedule(new Stop("test", new Time(d, 12, 00))));
			
			Stop s = schedule.getNextStop("test", new Time(d, 11, 59));
			assertEquals("test", s.getKeyword());
			assertEquals(new Time(d, 12, 00), s.getTime());
			
			s = schedule.getNextStop("test", new Time(d, 12, 01));
			assertEquals("test", s.getKeyword());
			assertEquals(new Time(d.add(1), 12, 00), s.getTime());
		}
	}

	@Test
	public void testGetNextStops() {
		final int stops_per_day = 5;
		
		for(Day d : Day.values()) {
			schedule.addStop(new Stop("wrong", new Time(d, 11, 20)));
			for (int i = 0; i < stops_per_day; i++) 
				schedule.addStop(new Stop("test", new Time(d, 11, 30 + i)));
			schedule.addStop(new Stop("wrong", new Time(d, 11, 40)));
		}
		
		for(Day d : Day.values()) {
			Iterable<Stop> stops = schedule.getNextStops("test", new Time(d, 11, 00), stops_per_day);
			int i = 0;
			for(Stop s : stops) {
				assertEquals("test", s.getKeyword());
				// Here we ensure correct order
				assertEquals(new Time(d, 11, 30 + i), s.getTime());
				i++;
			}
			// Make sure we got as many as we were expecting
			assertEquals(i, stops_per_day);
		}
		
		// make sure we get all the stops and no more when we ask for too many
		// we start on Monday to test the week boundary
		Iterable<Stop> stops = schedule.getNextStops("test", new Time(MONDAY, 11, 00), 10*stops_per_day);
		Day d = MONDAY;
		int i = 0;
		for(Stop s : stops) {
			assertEquals("test", s.getKeyword());
			assertEquals(new Time(d, 11, 30 + i), s.getTime());
			// still ensuring correct ordering, this time of days
			i = (i + 1) % stops_per_day;
			d = (i == 0 ? d.add(1) : d); // If we've looked at stops_per_day stops, then move on to the next day.
		}
	}

	@Test
	public void testGetStops() {
		schedule.addStop(new Stop("test-m1", new Time(MONDAY, 11, 20)));
		schedule.addStop(new Stop("test-m2", new Time(MONDAY, 11, 30)));
		schedule.addStop(new Stop("test-m3", new Time(MONDAY, 23, 30)));
		schedule.addStop(new Stop("test-t1", new Time(TUESDAY, 1, 30)));
		schedule.addStop(new Stop("test-st1", new Time(SATURDAY, 23, 30)));
		schedule.addStop(new Stop("test-sn1", new Time(SUNDAY, 1, 30)));
		
		Iterator<Stop> iter = schedule.getStops(new TimeRange(new Time(SUNDAY, 11, 00), new Time(MONDAY, 11, 40))).iterator();
		Stop s = iter.next();
		assertEquals("test-m1", s.getKeyword());
		assertEquals(new Time(MONDAY, 11, 20), s.getTime());
		s = iter.next();
		assertEquals("test-m2", s.getKeyword());
		assertEquals(new Time(MONDAY, 11, 30), s.getTime());
		assertFalse(iter.hasNext());
		
		iter = schedule.getStops(new TimeRange(new Time(SUNDAY, 11, 00), new Time(TUESDAY, 11, 40))).iterator();
		s = iter.next();
		assertEquals("test-m1", s.getKeyword());
		assertEquals(new Time(MONDAY, 11, 20), s.getTime());
		s = iter.next();
		assertEquals("test-m2", s.getKeyword());
		assertEquals(new Time(MONDAY, 11, 30), s.getTime());
		s = iter.next();
		assertEquals("test-m3", s.getKeyword());
		assertEquals(new Time(MONDAY, 23, 30), s.getTime());
		s = iter.next();
		assertEquals("test-t1", s.getKeyword());
		assertEquals(new Time(TUESDAY, 1, 30), s.getTime());
		assertFalse(iter.hasNext());
		
		iter = schedule.getStops(new TimeRange(new Time(SATURDAY, 11, 00), new Time(SUNDAY, 11, 40))).iterator();
		s = iter.next();
		assertEquals("test-st1", s.getKeyword());
		assertEquals(new Time(SATURDAY, 23, 30), s.getTime());
		s = iter.next();
		assertEquals("test-st1", s.getKeyword());
		assertEquals(new Time(SUNDAY, 1, 30), s.getTime());
		assertFalse(iter.hasNext());	
	}
}
