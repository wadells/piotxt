package persistance.schedule.store;

/**
 * An exception for errors during parsing. Attempts to create a more
 * user-friendly message out of the raw stacktrace.
 */
public class ParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	// Messages in string constants make localization panda a sad panda.
	// Luckily, no localization needed?
	protected static final String STACKTRACE_INTRO = "\nError occurred on line #%d\n\nStacktrace for a java programmer:\n---------------------------------------\n";

	/**
	 * Creates a parse exception with reason <code>message</code> that occurred
	 * at line <code>lineNumber</code> in the file, caused by <code>cause</code>
	 */
	public ParseException(String userMessage, String javaMessage, long lineNumber, Throwable cause) {
		super(userMessage + String.format(STACKTRACE_INTRO, lineNumber) + javaMessage, cause);
	}

	public ParseException(String userMessage, String javaMessage, long lineNumber) {
		this(userMessage, javaMessage, lineNumber, null);
	}
}
