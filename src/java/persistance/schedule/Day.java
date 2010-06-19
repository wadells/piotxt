package persistance.schedule;

import java.util.Calendar;

/** The days of the week. */
public enum Day {
	
	SUNDAY("Sunday", Calendar.SUNDAY),
	
	MONDAY("Monday", Calendar.MONDAY),
	
	TUESDAY("Tuesday", Calendar.TUESDAY),

	WEDNESDAY("Wednesday", Calendar.WEDNESDAY),	

	THURSDAY("Thursday", Calendar.THURSDAY),
	
	FRIDAY("Friday", Calendar.FRIDAY),

	SATURDAY("Saturday", Calendar.SATURDAY);
	
	private final String name;
	
	private final int day;
	
	/** Stores the human-preferred name and the day as per the Calendar class. */
	private Day(String name, int day) {
		this.name = name;
		this.day = day;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/** Returns true if this is the same day as the integer calendarDay. 
	 * @see Calendar.DAY_OF_WEEK
	 */
	public boolean matches(int calendarDay) {
		return day == calendarDay;
	}
}
