package application.rest.responses;

import java.util.HashMap;

public class Messages {
	
	public HashMap<String, Message> messages;

	public HashMap<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

}
