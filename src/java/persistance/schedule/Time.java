package persistance.schedule;

/**
 * A simple immutable time of a particular day.
 */
public class Time implements Comparable<Time> {

	/** Will be in the range [0, 23] */
	private final byte hours;

	/** Will be in the range [0, 59] */
	private final byte minutes;
	
	/** The day that this time falls on */
	private final Day day;
	
	public Time(int hours, int minutes, Day day) {
		if(hours < 0 || hours > 23) {
			throw new IllegalArgumentException(String.format("%d is not a valid hour. Hours must be in the range [0, 23].", hours));
		} 
		if(minutes < 0 || minutes > 59) {
			throw new IllegalArgumentException(String.format("%d is not a valid minute. Minutes must be in the range [0, 59].", minutes));
		}
		this.hours = (byte) hours;
		this.minutes = (byte) minutes;
		this.day = day;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}
	
	public Day getDay() {
		return day;
	}
	
	/** Returns a Time that is 'hours' hours offset from time (i.e. a positive value will be in the future and a negative value will be in the past). If taken past the week boundary (e.g. adding hours to 11:00pm Sunday night) this will wrap as expected (returning a time early Monday morning). */  
	public Time addHours(int hours) {
		return null;
	}
	
	/** Returns a Time that is 'minutes' minutes offset from this time (i.e. a positive value will be in the future and a negative value will be in the past). If taken past the week boundary (e.g. adding hours to 11:00pm Sunday night) this will wrap as expected (returning a time early Monday morning). */  
	public Time addMinutes(int minutes) {
		return null;
	}
	
	/** Returns a Time that is 'days' days offset from this time (i.e. a positive value will be in the future and a negative value will be in the past). If taken past the week boundary (e.g. adding hours to 11:00pm Sunday night) this will wrap as expected (returning a time early Monday morning). */  
	public Time addDays(int days) {
		return null;
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
		if (military) {
			return String.format("%02d:%02d", hours, minutes);
		} else {
			int hr = hours;
			boolean am = true;
			if (hours == 0) {
				hr = 12;
			} else if (hours > 12) {
				am = false;
				hr = hours - 12;
			}
			return String.format("%02d:%02d%s", hr, minutes, (am? "am" : "pm"));
		}
	}

	public int compareTo(Time o) {
		if (day != o.day) {
			return day.compareTo(o.day);
		}
		int dh = hours - o.hours, dm = minutes - o.minutes;
		return (dh == 0? dm : dh);
	}

	/** Returns the current time represented as a Time. */
	public static Time now() {
		return null;
	}

}
