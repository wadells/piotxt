package persistance.schedule;

import time.Time;


/** Represents a schedule of stops. */
public interface Schedule {

	/** 
	 * Returns a list of stops that occur in the time window from start to end. 
	 * 
	 * Returns no elements (i.e. an empty list) if no stops found. 
	 */
	public abstract Iterable<Stop> getStops(Time start, Time end);

	/** 
	 * Returns the next Stop occurring at location with keyword locationKeyword.
	 * 
	 * Returns null if no stop found.
	 */
	public abstract Stop getNextStop(String locationKeyword);

	/**
	 * Returns the number stops occurring at location with keyword locationKeyword.
	 * 
	 * Returns no elements (i.e. an empty list) if no stops found.
	 */
	public abstract Iterable<Stop> getNextStops(String locationKeyword,
			int number);

}