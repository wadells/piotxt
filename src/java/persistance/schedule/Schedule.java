package persistance.schedule;

import time.Time;
import time.TimeRange;


/** Represents a schedule of stops. */
public interface Schedule {

	/** 
	 * Returns a list of stops that occur in the time window from start to end. 
	 * 
	 * Returns no elements (for example, an empty list) if no stops found. 
	 */
	public abstract Iterable<Stop> getStops(TimeRange range);

	/** 
	 * Returns the next Stop after t occurring at location with keyword locationKeyword.
	 * 
	 * Returns null if no stop found.
	 */
	public abstract Stop getNextStop(String locationKeyword, Time t);

	/**
	 * Returns the number stops after t occurring at location with keyword locationKeyword.
	 * 
	 * Returns no elements (for example, an empty list) if no stops found.
	 */
	public abstract Iterable<Stop> getNextStops(String locationKeyword,
			Time t, int number);

}