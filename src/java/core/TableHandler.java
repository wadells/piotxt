package core;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import persistance.schedule.Schedule;
import persistance.schedule.Stop;
import persistance.schedule.store.DefaultSchedule;
import persistance.schedule.store.FileParser;
import time.Time;
import time.TimeRange;

/** A message handler that performs a simple lookup in from a weekly schedule. */
public class TableHandler extends MessageHandler {

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
		for (Stop s : stops) {
			String time = "\n" + s.getTime().toString(false);
			if (response.length() + time.length() > PioText.SMS_LENGTH) {
				break;
			} else {
				response.append(time);
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
		for (Stop s : stops) {
			String location = s.getKeyword();
			Time time = s.getTime();
			String line = String.format("\n%s @ %s", time.toString(), location);
			if (response.length() + line.length() > PioText.SMS_LENGTH) {
				break;
			} else {
				response.append(line);
			}
		}
		return response.toString();
	}

}
