package persistance.schedule.store;

import java.util.ArrayList;
import java.util.List;

import persistance.schedule.Schedule;
import persistance.schedule.Stop;
import time.Time;
import time.TimeRange;

/** A mock schedule that provides some plausible, if nonexistant, stops. */
public class MockSchedule implements Schedule {

	@Override
	public Stop getNextStop(String locationKeyword, Time t) {
		return new Stop(locationKeyword, Time.now().addMinutes(5));
	}

	@Override
	public Iterable<Stop> getNextStops(String locationKeyword, Time t, int number) {
		Time now = Time.now();
		List<Stop> stops = new ArrayList<Stop>();
		for(int i = 0; i < number; i++)
			stops.add(new Stop(locationKeyword, now.addMinutes(7*i)));
		return stops;
	}

	@Override
	public Iterable<Stop> getStops(TimeRange range) {
		// Bad fake implemntation: if the times are equal or only a minute apart, this will break.
		List<Stop> stops = new ArrayList<Stop>();
		stops.add(new Stop("keyword0", range.getBeginning().addMinutes(1)));
		stops.add(new Stop("keyword1", range.getEnd().addMinutes(-1)));
		return stops;
	}
}
