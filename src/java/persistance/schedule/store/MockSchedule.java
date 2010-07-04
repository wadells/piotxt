package persistance.schedule.store;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import persistance.schedule.Schedule;
import persistance.schedule.Stop;
import time.Time;

/** A mock schedule that provides some plausible, if nonexistant, stops. */
public class MockSchedule implements Schedule {

	public Stop getNextStop(String locationKeyword) {
		return new Stop("Mock Stop", locationKeyword, Time.now().addMinutes(5));
	}

	public Iterable<Stop> getNextStops(String locationKeyword, int number) {
		Time now = Time.now();
		List<Stop> stops = new ArrayList<Stop>();
		for(int i = 0; i < number; i++)
			stops.add(new Stop(format("Mock Stop %d", i), locationKeyword, now.addMinutes(7*i)));
		return stops;
	}

	public Iterable<Stop> getStops(Time start, Time end) {
		// Bad fake implemntation: if the times are equal or only a minute apart, this will break.
		List<Stop> stops = new ArrayList<Stop>();
		stops.add(new Stop("Mock Start Stop", "keyword0", start.addMinutes(1)));
		stops.add(new Stop("Mock End Stop", "keyword1", end.addMinutes(-1)));
		return stops;
	}
}
