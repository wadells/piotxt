package core;

import java.util.Date;

public class TestHandler extends MessageHandler {
	

	protected TestHandler() {
		super(new Keywords());
		keywords.add("zork", "easter egg");
		keywords.add("lc", "Undergraduate campus");
		keywords.add("templeton", "the raz stop at Templeton");
	}

	@Override
	public String defaultMessage(Date time) {
		return "This message is a test default response.";
	}

	@Override
	public String getResponse(Query query) {
		if (query.getKeyword() != null) {
			if (query.getKeyword().equals(Keywords.KEY_HELP)) {
				return helpMessage(new Date());
			}
			return "Keyword recognized: " + query.getKeyword();
		}
		return defaultMessage(query.getTimeReceived());
	}

}
