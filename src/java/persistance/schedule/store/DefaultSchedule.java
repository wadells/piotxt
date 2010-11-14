package persistance.schedule.store;

import java.util.*;

import persistance.schedule.Stop;
import time.*;


public class DefaultSchedule implements MutableSchedule {

	private Map<Day, Set<Stop>> stops;
	
	public DefaultSchedule() {
		this.stops = new EnumMap<Day, Set<Stop>>(Day.class);
		for(Day d : Day.values())
			stops.put(d, new TreeSet<Stop>(new Comparator<Stop>() {
				@Override
				public int compare(Stop s1, Stop s2) {
					return s1.getTime().compareTo(s2.getTime());
				}
			}));
	}

	@Override
	public void addStop(Stop s) {
				
	}

	@Override
	public Stop getNextStop(String locationKeyword, Time t) {
		return null;
	}

	@Override
	public Iterable<Stop> getNextStops(String locationKeyword, Time t, int number) {
		return new ArrayList<Stop>();
	}

	@Override
	public Iterable<Stop> getStops(TimeRange range) {
		return new ArrayList<Stop>();
	}

	@Override
	public void removeStops(TimeRange range) {
		
	}

	@Override
	public boolean stopInSchedule(Stop s) {
		return false;
	}

}
