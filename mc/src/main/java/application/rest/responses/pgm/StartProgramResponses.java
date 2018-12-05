package application.rest.responses.pgm;

import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;

public class StartProgramResponses {

	List<StartProgramResponse> data;
	HashMap<String, Message> messages;

	public HashMap<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public List<StartProgramResponse> getData() {
		return data;
	}

	public void setData(List<StartProgramResponse> data) {
		this.data = data;
	}
	
}
