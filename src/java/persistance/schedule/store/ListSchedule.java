package persistance.schedule.store;

import java.util.ArrayList;
import java.util.List;

import core.Keywords;
import persistance.schedule.Stop;
import time.Time;
import time.TimeRange;

public class ListSchedule implements MutableSchedule {

	// TODO: break this out by stop keyword so it's not so ugly
	private List<Stop> stops;
	
	private Keywords keywords;
	
	public ListSchedule(Keywords keywords) {
		this.keywords = keywords;
		this.stops = new ArrayList<Stop>();
	}

	@Override
	public void addStop(Stop s) {
		
	}

	@Override
	public Stop getNextStop(String locationKeyword) {
		return null;
	}

	@Override
	public Iterable<Stop> getNextStops(String locationKeyword, int number) {
		return new ArrayList<Stop>();
	}

	@Override
	public Iterable<Stop> getStops(Time start, Time end) {
		return new ArrayList<Stop>();
	}

	@Override
	public void removeStops(TimeRange range) {
		
	}

}
