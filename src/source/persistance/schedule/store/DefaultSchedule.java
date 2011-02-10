package persistance.schedule.store;

import java.util.*;

import persistance.schedule.Stop;
import time.*;

/** A default schedule implementation. Does not support multiple stops at the same time, regardless of keyword or direction. 
 *  In fact, as far as this implementation is concerned, two stops are identical iff they are at the same time.
 */
public class DefaultSchedule implements MutableSchedule {

	private TreeSet<Stop> stops;
	
	public DefaultSchedule() {
		this.stops = new TreeSet<Stop>(new Comparator<Stop>() {
				@Override
				public int compare(Stop s1, Stop s2) {
					Time t1 = s1.getTime(), t2 = s2.getTime();
					// If on different days, we allow the natural ordering of the
					// enum to take over so we only have to special case the week
					// boundary
					if(t1.getDay() != t2.getDay())
							return t1.getDay().compareTo(t2.getDay());
					else
						return t1.compareTo(t2);
				}
			});
	}

	@Override
	public void addStop(Stop s) {
		stops.add(s);
	}

	@Override
	public Stop getNextStop(final String locationKeyword, Time t) {
		Iterator<Stop> iter = getNextStops(locationKeyword, t, 1).iterator();
		if(iter.hasNext())
			return iter.next();
		return null;
	}

	@Override
	public Iterable<Stop> getNextStops(final String locationKeyword, Time t, int number) {
		Collection<Stop> c = getStopsStartingAt(t, new StopPredicate() {
			@Override
			public boolean applies(Stop s) {
				return s.getKeyword().equals(locationKeyword);
			}
		}, number);

		return c;
	}

	@Override
	public Iterable<Stop> getStops(final TimeRange range) {
		Collection<Stop> c = getStopsStartingAt(range.getBeginning(), new StopPredicate() {
			@Override
			public boolean applies(Stop s) {
				return range.isInRange(s.getTime());
			}
		}, -1);  // The -1 is a hack, true, but that's why those methods are private.

		return c;
	}

	// Oh for the want of closures... we make the TimeRange final so we can use it in the anonymous class.
	@Override
	public void removeStops(final TimeRange range) {
		// This is an expensive call, but this should only happen a few times at startup.
		Collection<Stop> toRemove = getAllStops(new StopPredicate() {
			@Override
			public boolean applies(Stop s) {
				return range.isInRange(s.getTime());
			}
		});
		
		for(Stop s : toRemove) 
			stops.remove(s);
	}

	@Override
	public boolean stopInSchedule(Stop s) {
		return stops.contains(s);
	}

	/** Returns all stops in this schedule that match the predicate p.
	 * 
	 * This method will check every stop in the entire set.
	 */
	private Collection<Stop> getAllStops(StopPredicate p) {
		LinkedList<Stop> matchedStops = new LinkedList<Stop>();
		// Iterate over every element for each operation.
		// TODO: A better way.
		for(Stop s : stops) {
			if(p.applies(s))
				matchedStops.add(s);
		}
		return matchedStops;
	}
	
	/** Returns a collection of at most limit stops maching p with their "natural" ordering. */
	private Collection<Stop> getStopsStartingAt(Time t, StopPredicate p, int limit) {
		Collection<Stop> matchedStops = new LinkedList<Stop>();
		
		// Start with everything "greater" than time t
		SortedSet<Stop> subset = stops.tailSet(new Stop("", t));
		for(Stop s : subset) {
			if(p.applies(s)) {
				matchedStops.add(s);
				if(matchedStops.size() == limit)
					break;
			}
		}
		
		if(matchedStops.size() == limit)
			return matchedStops;
		
		// If we still have more to look at, start back at the beginning of the week
		subset = stops.headSet(new Stop("", t));
		for(Stop s : subset) {
			if(p.applies(s)) {
				matchedStops.add(s);
				if(matchedStops.size() == limit)
					break;
			}
		}
		
		return matchedStops;
	}
}
