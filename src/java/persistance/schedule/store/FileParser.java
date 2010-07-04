package persistance.schedule.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import core.Keywords;

public class FileParser {
	
	protected static final String INVALID_KEYWORD_ERROR = "Could not parse keyword from line '%s'. Expected <Name of stop>, <Keyword for stop>.";
	
	private File file;
	
	private FileSchedule schedule; 
	
	private Keywords keywords;
	
	private long lineNumber;
	
	public FileParser(FileSchedule schedule, File file) {
		this.file = file;
		this.schedule = schedule;
		this.keywords = Keywords.instance();
	}
	
	public void parse() {
		BufferedReader reader = null;
		// Oh Java's unnecessary exception handling
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not parse file.", e);
		}
		
		lineNumber = 0;
		String line;
		try {
			while( (line = reader.readLine()) != null) {
				try {
					lineNumber++;
					parseLine(line);
				} catch(ParseException e) {
//					Log.error("Can has error logging? Caught an error, going to pretend like it didn't happen and continue.");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Could not parse file.", e);
		}
	}

	protected void parseLine(String original) {
		String line = original.trim();
		if(line.length() == 0 || line.startsWith("#")) 
			return; // Nothing to do
		
		String[] tokens = line.split("\\s*,\\s*");
		
		try {
			parseKeyword(tokens);
		} catch (IllegalArgumentException e) {
			throw new ParseException(String.format(INVALID_KEYWORD_ERROR, original), lineNumber, e);
		}
	}
	
	private void parseKeyword(String[] tokens) {
		if(tokens.length != 2)
			throw new IllegalArgumentException(String.format("Wrong number of tokens passed to parseKeyword. Expected 2, but was %d.", tokens.length));
		
		keywords.add(tokens[1], tokens[0]);
	}
	
	private void parseSchedule(String[] tokens) {
		
	}
	
	
	private void parseStops(String[] tokens) {
		
	}
}
