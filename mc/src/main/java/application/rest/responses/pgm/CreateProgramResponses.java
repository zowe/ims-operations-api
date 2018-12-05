package application.rest.responses.pgm;

import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;

public class CreateProgramResponses {
	
	List<CreateProgramResponse> data;
	HashMap<String, Message> messages;
	
	public HashMap<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public List<CreateProgramResponse> getData() {
		return data;
	}

	public void setData(List<CreateProgramResponse> data) {
		this.data = data;
	}
	

}
