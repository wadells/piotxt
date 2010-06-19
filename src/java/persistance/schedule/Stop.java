package persistance.schedule;

/** Represents a single shuttle bus stop at a certain time. */
public class Stop {
	
	private final String stopName;
	
	private final String stopKeyword;
	
	private final Time time;

	public Stop(String stopName, String stopKeyword, Time time) {
		this.stopName = stopName;
		this.stopKeyword = stopKeyword;
		this.time = time;
	}

	public String getStopName() {
		return stopName;
	}

	public String getStopKeyword() {
		return stopKeyword;
	}

	public Time getTime() {
		return time;
	}
	
}
