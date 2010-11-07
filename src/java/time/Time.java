package time;

import java.util.ArrayList;
import java.util.Calendar;

import utils.MathUtils;

/**
 * A simple immutable time of a particular day.
 */
public class Time implements Comparable<Time> {

	/**
	 * Returns the current time represented as a Time. This is locale-aware, so
	 * if the host JVM's timezone is set to be, say, PST, the current time will
	 * reflect that.
	 * 
	 * @see System.currentTimeMillis()
	 */
	public static Time now() {
		return new Time(System.currentTimeMillis());
	}

	/** Will be in the range [0, 23] */
	private final byte hours;

	/** Will be in the range [0, 59] */
	private final byte minutes;

	/** The day that this time falls on */
	private final Day day;

	/**
	 * Constructs a new time at the given <code>hour</code>, <code>minute</code>
	 * , and <code>day</code>. Hours are assumed to be in 24-hour notation.
	 */
	public Time(Day day, int hours, int minutes) {
		if (hours < 0 || hours > 23) {
			throw new IllegalArgumentException(String.format("%d is not a valid hour. Hours must be in the range [0, 23].", hours));
		}
		if (minutes < 0 || minutes > 59) {
			throw new IllegalArgumentException(String.format("%d is not a valid minute. Minutes must be in the range [0, 59].", minutes));
		}
		this.day = day;
		this.hours = (byte) hours;
		this.minutes = (byte) minutes;
	}

	/**
	 * Constructs a time that corresponds to the date given by
	 * <code>epoch</code>, assumed to be in milliseconds.
	 */
	public Time(long epoch) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(epoch);

		this.hours = (byte) c.get(Calendar.HOUR_OF_DAY);
		this.minutes = (byte) c.get(Calendar.MINUTE);
		this.day = Day.valueOf(c.get(Calendar.DAY_OF_WEEK));
	}

	/**
	 * Returns a Time that is <code>days</code> days offset from this time (i.e.
	 * a positive value will be in the future and a negative value will be in
	 * the past). If taken past the week boundary (e.g. adding a few hours to
	 * 11:00pm Sunday night) this will wrap as expected (returning a time early
	 * Monday morning).
	 */
	public Time addDays(int days) {
		if (days == 0)
			return this;
		return new Time(day.add(days), hours, minutes);
	}

	/**
	 * Returns a Time that is <code>hours</code> hours offset from time (i.e. a
	 * positive value will be in the future and a negative value will be in the
	 * past). If taken past the week boundary (e.g. adding a few hours to
	 * 11:00pm Sunday night) this will wrap as expected (returning a time early
	 * Monday morning).
	 */
	public Time addHours(int hours) {
		if (hours == 0)
			return this;
		int h = this.hours + hours;
		Day d = day.add(h < 0 ? (h + 1) / 24 - 1 : h / 24);
		return new Time(d, MathUtils.mod(h, 24), minutes);
	}

	/**
	 * Returns a Time that is <code>minutes</code> minutes offset from this time
	 * (i.e. a positive value will be in the future and a negative value will be
	 * in the past). If taken past the week boundary (e.g. adding a few hours to
	 * 11:00pm Sunday night) this will wrap as expected (returning a time early
	 * Monday morning).
	 */
	public Time addMinutes(int minutes) {
		if (minutes == 0)
			return this;
		int m = this.minutes + minutes;
		int h = this.hours + (m < 0 ? (m + 1) / 60 - 1 : m / 60);
		Day d = day.add(h < 0 ? (h + 1) / 24 - 1 : h / 24);
		return new Time(d, MathUtils.mod(h, 24), MathUtils.mod(m, 60));
	}

	public int compareTo(Time o) {
		if (day != o.day) {
			return day.compareTo(o.day);
		}
		int dh = hours - o.hours, dm = minutes - o.minutes;
		return (dh == 0 ? dm : dh);
	}
	
	public boolean equalToTime(Time t) {
		return this.compareTo(t) == 0;
	}

	public Day getDay() {
		return day;
	}

	public int getHour() {
		return hours;
	}

	public int getMinute() {
		return minutes;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Returns the time of day as a string. Uses 24-hour time if military is
	 * set, 12-hour time otherwise.
	 */
	public String toString(boolean military) {
		return toString(military, false);
	}

	/**
	 * Returns the time of day as a string. Uses 24-hour time if
	 * <code>military</code> is set, 12-hour time otherwise. Includes the short
	 * name of the day if <code>includeDay</code> is set.
	 */
	public String toString(boolean military, boolean includeDay) {
		ArrayList<Object> params = new ArrayList<Object>(5);
		StringBuilder format = new StringBuilder();

		if (includeDay) {
			format.append("%s ");
			params.add(day.getShortName());
		}

		// Default to 24-hour time
		format.append("%02d:%02d");
		if (military) {
			params.add(hours);
			params.add(minutes);
		} else {
			// If we're not, then convert the hours
			int hr = hours;
			boolean am = true;
			if (hr == 0) {
				hr = 12;
			} else if (hr > 12) {
				am = false;
				hr -= 12;
			}
			params.add(hr);
			params.add(minutes);

			// and add an am/pm
			format.append("%s");
			params.add(am ? "am" : "pm");
		}

		return String.format(format.toString(), params.toArray());
	}
}
