package persistance.schedule.store;

import persistance.schedule.Schedule;
import persistance.schedule.Stop;
import time.TimeRange;

/** A schedule that can be modified. For use when loading the schedule initially. */
public interface MutableSchedule extends Schedule {
	
	/** Adds a stop that corresponds with the specified keyword at the given time. */
	public void addStop(Stop stop);
	
	/** Removes all stops that are in the specified range. */
	public void removeStops(TimeRange range);
	
	/** Convenience method for testing/package internal use. */
	boolean stopInSchedule(Stop s);
}
