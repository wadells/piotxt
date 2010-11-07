package time;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An immutable range representing the slice of time between the beginning and
 * the end.
 * 
 */
public class TimeRange {

	private static TimeRange emptyRange;
	
	/** A convenience method for an empty time range. */
	protected static TimeRange emptyRange() {
		if(emptyRange == null) {
			Time t = new Time(Day.SUNDAY, 0, 0);
			emptyRange = new TimeRange(t, t);
		}
		return emptyRange;
	}
	
	private Time begin;
	private Time end;

	/**
	 * Construct a time range [begin, end). If begin occurs at the
	 * end, this time range is said to be empty.
	 */
	public TimeRange(Time begin, Time end) {
		this.begin = begin;
		this.end = end;
	}

	/** Tests whether this time range represents any nonzero length of time. */
	public boolean isEmpty() {
		return begin.equalToTime(end);
	}

	/** Tests whether t falls into this range. */
	public boolean isInRange(Time t) {
		if(isEmpty())
			return false;
		
		 return false;
	}
	
	/** Returns the slice of time that exists in both ranges. */
	public TimeRange intersect(TimeRange r) {
		return null;
	}
	
	/** Tests whether the intersection of this time range with r is nonzero. */
	public boolean overlaps(TimeRange r) {
		return false;
	}
}

/* TODO: Is this necessary?
 * Time ranges also have the expected ordering within the week, where Sunday is
 * considered to be the first day of the week and Saturday the last. The time
 * range that starts before the other is always said to come earlier.
*/