/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.delete;

import java.util.List;
import java.util.Map;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that represents all the data returned from OM following a Delete PGM command")
public class DeleteProgramOutput {

	@Schema(description = "A list of DeleteProgram objects. Represents the JSON data output for all programs from the command")
	List<DeleteProgram> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	Map<String, Message> messages;

	public Map<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(Map<String, Message> messages) {
		this.messages = messages;
	}

	public List<DeleteProgram> getData() {
		return data;
	}

	public void setData(List<DeleteProgram> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Query Program Response Data");
		for (DeleteProgram q : data) {
			sb.append(q.toString());
		}
		sb.append("\n");
		for (String key : messages.keySet()) {
			sb.append(key + " : " + messages.get(key));
		}
		
		return sb.toString();
	}
	
	
	
}
