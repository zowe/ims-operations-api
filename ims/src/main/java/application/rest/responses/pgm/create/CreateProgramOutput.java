/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.create;

import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that represents all the data returned from OM following a Create PGM command")
public class CreateProgramOutput {
	
	@Schema(description = "A list of CreateProgram objects. Represents the JSON data output for all programs from the command")
	List<CreateProgram> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	HashMap<String, Message> messages;
	
	public HashMap<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public List<CreateProgram> getData() {
		return data;
	}

	public void setData(List<CreateProgram> data) {
		this.data = data;
	}
	

}
