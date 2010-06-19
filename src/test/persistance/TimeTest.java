package persistance;

import org.junit.Test;

import persistance.schedule.Time;

import static persistance.schedule.Day.*;
import static org.junit.Assert.*;

public class TimeTest {
	
	private Time time;
	
	@Test
	public void testTime() {
		Time time = new Time(22, 10, SUNDAY); // 22:10
		assertEquals(22, time.getHours());
		assertEquals(10, time.getMinutes());
		assertEquals(SUNDAY, time.getDay());
		assertEquals("10:10pm", time.toString());
		assertEquals("22:10", time.toString(true));

		time = new Time(10, 59, SUNDAY); // 10:10
		assertEquals("10:59am", time.toString());
		assertEquals("10:59", time.toString(true));

		time = new Time(0, 01, SUNDAY); // 00:59
		assertEquals("12:01am", time.toString());
		assertEquals("00:01", time.toString(true));
	}
	
	@Test
	public void testIllegal() {
		try {
			time = new Time(-1, 00, SUNDAY);
			fail("Passing illegal times did not result in an exception.");
		} catch(IllegalArgumentException ex) {
			// Do nothing
		}

		try {
			time = new Time(24, 00, SUNDAY);
			fail("Passing illegal times did not result in an exception.");
		} catch(IllegalArgumentException ex) {
			// Do nothing
		}
		
		try {
			time = new Time(2, -1, SUNDAY);
			fail("Passing illegal times did not result in an exception.");
		} catch(IllegalArgumentException ex) {
			// Do nothing
		}
		
		try {
			time = new Time(2, 60, SUNDAY);
			fail("Passing illegal times did not result in an exception.");
		} catch(IllegalArgumentException ex) {
			// Do nothing
		}
	}

	@Test
	public void testCompare() {
		// TODO: Re-evaluete the compare to--should Saturday be > all other days? 
		time = new Time(0, 01, SUNDAY);
		Time t1 = new Time(0, 00, SATURDAY);
		assertTrue(time.compareTo(t1) < 0 && t1.compareTo(time) > 0);
		
		t1 = new Time(12, 0, MONDAY);
		assertTrue(t1.compareTo(time) > 0);
		
		time = new Time(12, 0, MONDAY);
		assertTrue(time.compareTo(t1) == time.compareTo(t1) && time.compareTo(t1) == 0);
		
		t1 = new Time(12, 0, TUESDAY);
		assertTrue(time.compareTo(t1) < 0);
		
		t1 = new Time(11, 59, MONDAY);
		assertTrue(time.compareTo(t1) > 0 && t1.compareTo(time) < 0);
	}
	
	@Test
	public void testAdd() {
		// Don't forget negatives
		fail("Not yet implemented.");
	}
	
	@Test
	public void testNow() {
		fail("Not yet implemented.");
	}
}
