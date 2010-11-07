package time;

import static time.Day.daysBetween;

/**
 * An immutable range representing the slice of time between the beginning and
 * the end.
 * 
 */
public class TimeRange {

	private static TimeRange emptyRange;

	/** A convenience method for an empty time range. */
	protected static TimeRange emptyRange() {
		if (emptyRange == null) {
			Time t = new Time(Day.SUNDAY, 0, 0);
			emptyRange = new TimeRange(t, t);
		}
		return emptyRange;
	}

	private Time begin;
	private Time end;

	/**
	 * Construct a time range [begin, end). If begin occurs at the end, this
	 * time range is said to be empty.
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
		if (isEmpty())
			return false;

		if (!daysBetween(begin.getDay(), end.getDay()).contains(t.getDay()))
			return false;

		// If this is a one-day range, we perform the check all at once
		if (begin.getDay() == end.getDay())
			return begin.compareTo(t) <= 0 && end.compareTo(t) > 0;

		if (begin.getDay() == t.getDay())
			return begin.compareTo(t) <= 0; // Must come after or at begin time

		if (end.getDay() == t.getDay())
			return end.compareTo(t) > 0; // Must come before end time

		// If it's not on the first or last day of a multi-day range, but it is
		// in the day range, then it's in.
		return true;
	}

	/** Returns the slice of time that exists in both ranges. */
	public TimeRange intersect(TimeRange r) {
		return null;
	}

	/** Tests whether the intersection of this time range with r is nonzero. */
	public boolean doesIntersect(TimeRange r) {
		return !intersect(r).isEmpty();
	}
}

/*
 * TODO: Is this necessary? Time ranges also have the expected ordering within
 * the week, where Sunday is considered to be the first day of the week and
 * Saturday the last. The time range that starts before the other is always said
 * to come earlier.
 */