package persistance.schedule;

import java.io.File;
import java.util.ArrayList;

import persistance.schedule.store.FileParser;

/** Loads and stores the Raz schedule. */
public class FileSchedule implements Schedule {
	
	public FileSchedule(File schedule) {
		new FileParser(schedule);
	}
	
	public Iterable<Stop> getStops(Time start, Time end) {
		// TODO: this
		return new ArrayList<Stop>();
	}
	
	public Stop getNextStop(String locationKeyword) {
		// Get the next 1 stops, and return it
		Iterable<Stop> stops = getNextStops(locationKeyword, 1);
		return stops.iterator().hasNext() ? stops.iterator().next() : null;
	}
	
	public Iterable<Stop> getNextStops(String locationKeyword, int number) {
		// TODO: this
		return new ArrayList<Stop>();
	}
	
	public static void main(String[] args) {
		//System.out.println("it worked!");
	}
}
