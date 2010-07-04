package time;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import utils.MathUtils;

/** The days of the week. */
public enum Day {

	SUNDAY(Calendar.SUNDAY),

	MONDAY(Calendar.MONDAY),

	TUESDAY(Calendar.TUESDAY),

	WEDNESDAY(Calendar.WEDNESDAY),

	THURSDAY(Calendar.THURSDAY),

	FRIDAY(Calendar.FRIDAY),

	SATURDAY(Calendar.SATURDAY);

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
	 * Returns true if this is the same day as the integer <code>calendarDay</code>.
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
		if(days == 0)
			return this;
		
		// Unfortunately, the values provided by the Calendar class are 1-7
		// rather than 0-6, so we tweak the values by +/-1 here.
		return valueOf(MathUtils.mod(day + days - 1, 7) + 1);
	}
}
