package core;

import java.util.GregorianCalendar;
//import static core.Keyword.*;

/** A message handler that performs a simple lookup in from a weekly schedule. */
public class TableHandler extends MessageHandler {

	public String getResponse(Keyword key, GregorianCalendar time) {
		String response;
		switch (key) { // TODO: Fill these responses in with real logic
		case SQUARE:
			response = "";
			break;
		case CAMPUS:
			response = "";
			break;
		case LAW:
			response = "";
			break;
		case FREDDYS:
			response = "";
			break;
		case NEXT_HOUR:
			response = "";
			break;
		case HELP:
			response = "";
			break;
		default:
			response = "";
			break;
		}
		return response;
	}

	@Override
	public String defaultMessage(GregorianCalendar time) {
		return "Useful information" + "\nUnrecognized key word.  Text HELP for a list of key words.";
	}

}
