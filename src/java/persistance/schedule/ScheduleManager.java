package persistance.schedule;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;

import java.io.File;
import java.net.URISyntaxException;

import persistance.schedule.store.FileSchedule;
import persistance.schedule.store.MockSchedule;

/** A manager class for controlling the schedule singleton. 
 * 
 * If the mock.schedule property is set to true (i.e. -Dmock.schedule=true),
 * then the mock schedule is used. Otherwise, uses a FileSchedule.
 *
 */
public class ScheduleManager {

	/** The property to decide whether to use the mock schedule */
	public static final String MOCK_SCHEDULE_PROP = "mock.schedule";
	
	/** The schedule file, for when we're not bundled as a jar */
	public static final String SCHEDULES_FILE = "resources" + File.separator + "schedule.txt";
	
	/** The URI for the schedule resource. Always always always use '/' in URIs. */
	public static final String SCHEDULES_RESOURCE = "/resources/schedule.txt";
	
	private static Schedule theSchedule;
	
	public static Schedule getSchedule() {
		if(theSchedule == null) {
			theSchedule = createSchedule();
		}
		return theSchedule;
	}
	
	/** Finds or creates the schedule, based on system properties. */
	protected static Schedule createSchedule() {
		if(parseBoolean(System.getProperty(MOCK_SCHEDULE_PROP, FALSE.toString())))
			return new MockSchedule();
		else
			return loadFileSchedule();
	}
	
	/** We try to find/load the schedules file */
	protected static Schedule loadFileSchedule() {
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
		return new FileSchedule(file);
	}
	
}
