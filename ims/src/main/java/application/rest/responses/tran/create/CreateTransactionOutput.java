/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.tran.create;

import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that represents all the data returned from OM following a Create TRAN command")
public class CreateTransactionOutput {
	
	@Schema(description = "A list of CreateTransaction objects. Represents the JSON data output for all transaction from the command")
	List<CreateTransaction> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	HashMap<String, Message> messages;
	
	public HashMap<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public List<CreateTransaction> getData() {
		return data;
	}

	public void setData(List<CreateTransaction> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Create Transaction Response Data");
		for (CreateTransaction q : data) {
			sb.append(q.toString());
		}
		sb.append("\n");
		for (String key : messages.keySet()) {
			sb.append(key + " : " + messages.get(key));
		}
		
		return sb.toString();
	}



}
