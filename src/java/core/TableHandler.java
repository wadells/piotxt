package core;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import persistance.schedule.Schedule;
import persistance.schedule.Stop;
import persistance.schedule.store.DefaultSchedule;
import persistance.schedule.store.FileParser;
import time.Day;
import time.Time;
import time.TimeRange;

/** A message handler that performs a simple lookup in from a weekly schedule. */
public class TableHandler extends MessageHandler {

	/**
	 * If there are more than this number of minutes between stops, then
	 * BREAK_NOTICE will be inserted.
	 */
	private static final int GAP_MINUTES = 120;

	/** The message that is displayed when there is a break in service. */
	private static final String BREAK_NOTICE = "(not in service)";

	private Schedule schedule;

	@Override
	public void initialize(Properties props) {
		super.initialize(props);
		keywords.add("all", "all stops in the next hour");
		String url = props.getProperty("schedule_file");
		File f = new File(url);
		DefaultSchedule sched = new DefaultSchedule();
		new FileParser(sched, keywords, f).parse();
		this.schedule = sched;

	}

	@Override
	public String getHandlerResponse(Query query) {
		// initialize response
		StringBuilder response = new StringBuilder();
		String header = getHeader(query.getTimeReceived());
		response.append(header);
		// add one location declaration, because this is single stop
		String location = "\n" + keywords.getDefinition(query.getKeyword());
		response.append(location);
		// get list of stops
		Time requestTime = new Time(query.getTimeReceived().getTime());
		Iterable<Stop> stops = schedule.getNextStops(query.getKeyword(),
				requestTime, 20);
		// add a time for each stop
		Time previousStopTime = Time.now();
		for (Stop s : stops) {
			Time currentStopTime = s.getTime();
			// calculate if there is a break in Pio Express service
			if (GAP_MINUTES < minutesBetween(previousStopTime, currentStopTime)) {
				response.append("\n" + BREAK_NOTICE);
			}
			// don't exceed the boundries of one sms
			String time = "\n" + currentStopTime.toString(false);
			if (!s.getDirection().getMarker().isEmpty()) {
				time += " " + s.getDirection().getMarker();
			}
			if (response.length() + time.length() > PioText.SMS_LENGTH) {
				break;
			} else {
				response.append(time);
				previousStopTime = currentStopTime;
			}
		}
		return response.toString();
	}

	@Override
	public String defaultMessage(Date date) {
		// initialize response
		StringBuilder response = new StringBuilder();
		String header = getHeader(date);
		response.append(header);
		// get list of stops
		Time requestTime = new Time(date.getTime());
		TimeRange range = new TimeRange(requestTime, requestTime.addDays(1));
		Iterable<Stop> stops = schedule.getStops(range);
		// add a time @ location line for each stop
		Time previousStopTime = Time.now();
		for (Stop s : stops) {
			Time currentStopTime = s.getTime();
			// calculate if there is a break in Pio Express service
			if (GAP_MINUTES < minutesBetween(previousStopTime, currentStopTime)) {
				response.append("\n" + BREAK_NOTICE);
			}
			String location = keywords.getDefinition(s.getKeyword());
			String line = String.format("\n%s @ %s",
					currentStopTime.toString(), location);
			// don't exceed the boundries of one sms
			if (response.length() + line.length() > PioText.SMS_LENGTH) {
				break;
			} else {
				response.append(line);
				previousStopTime = currentStopTime;
			}
		}
		return response.toString();
	}

	private long minutesBetween(Time begin, Time end) {
		int days;
		if (!begin.getDay().equals(end.getDay())) {
			days = Day.daysBetween(begin.getDay(), end.getDay()).size() - 1;
		} else {
			days = 0;
		}
		int hours = end.getHour() - begin.getHour();
		int minutes = end.getMinute() - begin.getMinute();
		return 1440 * days + 60 * hours + minutes;
	}

}
