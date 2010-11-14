package persistance.schedule.store;

import static java.util.regex.Pattern.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import time.Day;
import time.Time;
import time.TimeRange;

import core.Keywords;

public class FileParser {

	protected class NoScheduleParseException extends ParseException {
		private static final long serialVersionUID = 1L;

		public NoScheduleParseException(String message) {
			super("Could not associate stops with a schedule. Please define a schedule before defining stops.", message, lineNumber);
		}
	}

	protected class KeywordParseException extends ParseException {
		private static final long serialVersionUID = 1L;

		public KeywordParseException(String message) {
			super("Could not parse keyword from line. Expected <Name of stop>, <Keyword for stop>.", message, lineNumber);
		}
	}

	protected class IllegalKeywordParseException extends ParseException {
		private static final long serialVersionUID = 1L;

		public IllegalKeywordParseException(String keyword, String message) {
			super(String.format("Found unexpected character in keyword '%s', please do not use spaces or tabs in keywords.", keyword), message, lineNumber);
		}
	}

	private File file;

	private MutableSchedule schedule;
	
	private Collection<TimeRange> currentSchedule = null;

	private Keywords keywords;

	private long lineNumber;

	public FileParser(MutableSchedule schedule, Keywords keywords, File file) {
		this.file = file;
		this.schedule = schedule;
		this.keywords = keywords;
		this.currentSchedule = new LinkedList<TimeRange>();
	}

	public void parse() {
		BufferedReader reader = null;
		// Oh Java's unnecessary exception handling
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not find file.", e);
		}

		lineNumber = 0;
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				try {
					lineNumber++;
					parseLine(line);
				} catch (ParseException e) {
					// TODO: Improved error logging.
					// For now, just print the stack trace
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Could not parse file.", e);
		}
	}

	protected void parseLine(String original) {
		String line = original.trim();
		if (line.length() == 0 || line.startsWith("#"))
			return; // Nothing to do

		String[] tokens = line.split("\\s*,\\s*");

		if(tokens.length < 1)
			throw new ParseException("Failed to parse any tokens from line '%s'.", "Encountered an unknown error.", lineNumber);
		else if(tokens[0].startsWith("Schedule:")) 
			parseSchedule(tokens[0].substring(tokens[0].indexOf(':') + 1));
		else if(isValidKeyword(tokens[0]) && keywords.contains(tokens[0]))
			parseStops(tokens);
		else
			parseKeyword(tokens);
	}

	protected boolean isValidKeyword(String word) {
		// Apparently Java likes to pretend regexes are all bounded
		// i.e. if we ask if a string matches /\s/ it will try to match /^\s$/
		final String regex = ".*\\s.*";
		return !word.matches(regex);
	}
	
	private void parseKeyword(String[] tokens) {
		if (tokens.length != 2)
			throw new KeywordParseException(String.format("Wrong number of tokens passed to parseKeyword. Expected 2, but was %d.", tokens.length));
		if (!isValidKeyword(tokens[1]))
			throw new IllegalKeywordParseException(tokens[1], String.format("'%s' failed to pass keyword validation.", tokens[1]));

		keywords.add(tokens[1], tokens[0]);
	}

	private void parseSchedule(String timeperiod) {
		//  Monday-Friday 7:05am-11:00pm
		Pattern dayPattern = compile("(\\w+)\\s*-\\s*(\\w+)");
		Pattern timePattern = compile("(\\d{1,2}:\\d{2}(?:am|pm)*)\\s*-\\s*(\\d{1,2}:\\d{2}(?:am|pm)*)");
		Matcher days = dayPattern.matcher(timeperiod);
		Matcher times = timePattern.matcher(timeperiod);
		
		if(!days.find())
			// TODO: graceful error
			return;
		
		if(!times.find())
			// TODO: graceful error
			return;
		
		Day start = Day.findByName(days.group(1)), end = Day.findByName(days.group(2));
		String startTime = times.group(1), endTime = times.group(2);
		Collection<TimeRange> newSchedule = new LinkedList<TimeRange>();
		
		// TODO: Graceful error
		for(Day d : Day.daysBetween(start, end)) {
			newSchedule.add(new TimeRange(Time.parse(startTime, d), Time.parse(endTime, d)));
		}
		
		// We wait to do this until now to preserve the previous schedule if we fail to properly parse this one.
		// TODO: Better to just clear out the schedule? We'll error out on every stop until the next schedule...
		currentSchedule.clear();
		currentSchedule.addAll(newSchedule);
		
		// Overwrite the previously existing schedule (if any) with this one.
//		for(TimeRange r : currentSchedule)
//			schedule.removeStops(r);
	}

	private void parseStops(String[] tokens) {
		// square, 7:05, 8:05, 9:05, 10:05, 3:21, 4:18, 5:18, 6:29, 7:35, 8:36, 9:36, 10:35
		if(currentSchedule.isEmpty())
			throw new NoScheduleParseException("Attempted to parse stops without an enclosing schedule.");
		
		String keyword = tokens[0];
		// If our schedule starts in the PM, then start in the PM
		boolean pm = currentSchedule.iterator().next().getBeginning().getHour() >= 12;
		for (int i = 1; i < tokens.length; i++) {
			// TODO: this still does nothing....
		}
	}
}
