package time;

import java.text.DateFormatSymbols;
import java.util.*;

import utils.MathUtils;

/** The days of the week. */
@SuppressWarnings("unchecked")
public enum Day {

	SUNDAY(Calendar.SUNDAY),

	MONDAY(Calendar.MONDAY),

	TUESDAY(Calendar.TUESDAY),

	WEDNESDAY(Calendar.WEDNESDAY),

	THURSDAY(Calendar.THURSDAY),

	FRIDAY(Calendar.FRIDAY),

	SATURDAY(Calendar.SATURDAY);

	/** Holds all of the sets of days like so: [begin][end] */
	private static Set<Day>[][] dayRanges;

	static {
		// Needs to be 8x8 because the days are 1-7 rather than 0-6
		dayRanges = new Set[8][8];
		for (Day begin : new DayRange(SUNDAY, SATURDAY)) {
			EnumSet<Day> days = EnumSet.of(begin);
			for (Day end : new DayRange(begin, begin.add(-1))) {
				days.add(end);
				dayRanges[begin.day][end.day] = days.clone();
			}
		}
	}

	/**
	 * Returns a set containing all the days starting with begin and ending with
	 * end.
	 * 
	 * e.g. daysBetween(SATURDAY, MONDAY) would contain Sat, Sun, and Mon. e.g.
	 * daysBetween(MONDAY, FRIDAY) would contain the usual weekdays.
	 */
	public static Set<Day> daysBetween(Day begin, Day end) {
		return dayRanges[begin.day][end.day];
	}

	/**
	 * Returns the day represented by the integer <code>day</code>.
	 * 
	 * @see Calendar.DAY_OF_WEEK
	 */
	public static final Day valueOf(int day) {
		for (Day d : values()) {
			if (d.matches(day)) {
				return d;
			}
		}
		throw new IllegalArgumentException(String.format("'%d' does not map to a day of the week.", day));
	}

	/**
	 * Returns the day represented by the string <code>day</code>.
	 */
	public static final Day findByName(String day) {
		for (Day d : values()) {
			// English-only for now. If America ends up changing its national
			// language, this will need to be rewritten.
			if (d.getShortName().equalsIgnoreCase(day) || d.getName().equalsIgnoreCase(day)) {
				return d;
			}
		}
		throw new IllegalArgumentException(String.format("'%d' does not map to a day of the week.", day));
	}

	private final int day;

	/** Stores the human-preferred name and the day as per the Calendar class. */
	private Day(int day) {
		this.day = day;
	}

	/** Gets the short name (i.e. "Sun") based on the default locale. */
	public String getShortName() {
		return DateFormatSymbols.getInstance().getShortWeekdays()[day];
	}

	/** Gets the name (i.e. "Sunday") based on the default locale. */
	public String getName() {
		return DateFormatSymbols.getInstance().getWeekdays()[day];
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns true if this is the same day as the integer
	 * <code>calendarDay</code>.
	 * 
	 * @see Calendar.DAY_OF_WEEK
	 */
	public boolean matches(int calendarDay) {
		return day == calendarDay;
	}

	/**
	 * Returns the day offset by <code>days</code> from this one.
	 */
	public Day add(int days) {
		if (days == 0)
			return this;

		// Unfortunately, the values provided by the Calendar class are 1-7
		// rather than 0-6, so we tweak the values by +/-1 here.
		return valueOf(MathUtils.mod(day + days - 1, 7) + 1);
	}

	/**
	 * Iterates over the specified range of days (minimally one, if start ==
	 * end). The follows the usual week ordering.
	 */
	protected static class DayRange implements Iterable<Day>, Iterator<Day> {

		private Day current;
		private Day end;
		private boolean finished;

		public DayRange(Day start, Day end) {
			this.current = start;
			this.end = end;
			this.finished = false;
		}

		public Iterator<Day> iterator() {
			return this;
		}

		public boolean hasNext() {
			return !finished;
		}

		public Day next() {
			if (!hasNext())
				throw new NoSuchElementException();

			Day next = current;
			if (current != end)
				current = current.add(1);
			else
				finished = true;
			return next;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
