package core;

import java.util.Date;
import static core.Keywords.*;

/** A message handler that performs a simple lookup in from a weekly schedule. */
public class TableHandler extends MessageHandler {

	protected TableHandler() {
	}

	@Override
	public String getResponse(Query q) {
		if (q.getKeyword() == null) {
			return defaultMessage(q.getTimeReceived());
		} else if (q.getKeyword().equals(KEY_HELP)) {
			return helpMessage(q.getTimeReceived());
		}
		return defaultMessage(q.getTimeReceived());
	}

	@Override
	public String defaultMessage(Date date) {
		return "";
	}

}
