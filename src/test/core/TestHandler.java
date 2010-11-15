package core;

import java.util.Date;

public class TestHandler extends MessageHandler {

	protected TestHandler() {
		keywords.add("zork", "easter egg");
		keywords.add("lc", "Undergraduate campus");
		keywords.add("templeton", "the raz stop at Templeton");
	}

	@Override
	public String defaultMessage(Date time) {
		String response = getHeader(time);
		response += "\nTest Handler\nThis is a default response.";
		return response;
	}

	@Override
	public String getHandlerResponse(Query query) {
		String response = getHeader(query.getTimeReceived());
		response += "\nTest Handler\nKeyword recognized: " + query.getKeyword();
		return response;
	}

}
