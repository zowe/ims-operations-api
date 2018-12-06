package application.rest.responses;

import java.util.HashMap;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that represents all the messages returned from OM following the execution of an IMS command")
public class Messages {
	
	public HashMap<String, Message> messages;

	public HashMap<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

}
