package time;

public class TimeFormatException extends IllegalArgumentException {
	private static final long serialVersionUID = -1860952420331814581L;

	static TimeFormatException forInputString(String s) {
        return new TimeFormatException("For input string: \"" + s + "\"");
    }
	
	public TimeFormatException() {
		super();
	}

	public TimeFormatException(String s) {
		super(s);
	}
}
