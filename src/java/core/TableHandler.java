package core;

import java.util.Date;
import java.util.Properties;

import static core.Keywords.*;

/** A message handler that performs a simple lookup in from a weekly schedule. */
public class TableHandler extends MessageHandler {

	@Override
	public void initialize(Properties props) {
		super.initialize(props);

		// TODO: Load in the schedule
	}

	@Override
	public String getResponse(Query q) {
		identifyKeyword(q);
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
