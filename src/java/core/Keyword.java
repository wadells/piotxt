package core;

/** The keywords this system recognizes. */
public enum Keyword {

	/** Key word for the Pioneer Square stop. */
	SQUARE("square"),

	/** Key word for the Templeton stop. */
	CAMPUS("lc"),

	/** Key word for the Law School stop. */
	LAW("law"),

	/** Key word for the Burlingame Fred Meyer's Stop */
	FREDDYS("fred"),

	/** Key word for the next hours worth of schedule. */
	NEXT_HOUR("hour"),

	/** Key word for a message on how to use the service. */
	HELP("help");

	/** The actual text string representing the key word. */
	private final String word;

	Keyword(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return word;
	}

	/**
	 * Returns true if the keyword matches the text
	 * 
	 * @param text
	 *            the text to compare to
	 * @return true if the keyword matched the text
	 */
	public boolean matches(String text) {
		return word.equalsIgnoreCase(text.trim());
	}

}
