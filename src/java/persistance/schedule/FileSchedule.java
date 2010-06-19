package persistance.schedule;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import persistance.schedule.store.FileParser;

/** Loads and stores the Raz schedule. */
public class FileSchedule implements Schedule {
	
	/** The schedule file, for when we're not bundled as a jar */
	public static final String SCHEDULES_FILE = "resources" + File.separator + "schedule.txt";
	
	/** The URI for the schedule resource. Always always always use '/' in URIs. */
	public static final String SCHEDULES_RESOURCE = "/resources/schedule.txt";
	
	private static final Schedule theSchedules;
	
	/** We try to find/load the schedules file */
	static {
		// First, see if it's in the local filesystem (i.e. running from eclipse)
		File file = new File(SCHEDULES_FILE);
		if(!file.canRead()) {
			// Otherwise, look for it somewhere the classloader can get at it
			// i.e., if we're running from a JAR
			try {
				file = new File(FileSchedule.class.getResource(SCHEDULES_RESOURCE).toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		theSchedules = new FileSchedule(file);
	}

	/** Returns the one and only list of schedules  */
	public static Schedule getSchedules() {
		return theSchedules;
	}
	
	private FileSchedule(File schedule) {
		// TODO: Store this, or maybe these? (i.e. time based structure and stop-based?)
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
