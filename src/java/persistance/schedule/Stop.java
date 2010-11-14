package persistance.schedule;

import time.Time;


/** Represents a single shuttle bus stop at a certain time. */
public class Stop {
	
	public enum Direction {
		NONE(""), INBOUND("i"), OUTBOUND("o");
		
		private String marker;
		
		private Direction(String marker) {
			this.marker = marker;
		}
		
		public String getMarker() {
			return marker;
		}
	}
	
	private final String stopKeyword;
	
	private final Time time;
	
	private final Direction direction;

	public Stop(String stopKeyword, Time time) {
		this(stopKeyword, time, Direction.NONE);
	}
	
	public Stop(String stopKeyword, Time time, Direction direction) {
		this.stopKeyword = stopKeyword;
		this.time = time;
		this.direction = direction;
	}

	public String getKeyword() {
		return stopKeyword;
	}

	public Time getTime() {
		return time;
	}

	public Direction getDirection() {
		return direction;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s%s", stopKeyword, time, direction.getMarker());
	}
}
