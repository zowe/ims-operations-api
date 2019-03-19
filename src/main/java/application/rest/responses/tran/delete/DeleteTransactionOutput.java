/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.tran.delete;

import java.util.List;
import java.util.Map;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

public class DeleteTransactionOutput {
	@Schema(description = "A list of DeleteTransaction objects. Represents the JSON data output for all transactions from the command")
	List<DeleteTransaction> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	Map<String, Message> messages;

	public Map<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(Map<String, Message> messages) {
		this.messages = messages;
	}

	public List<DeleteTransaction> getData() {
		return data;
	}

	public void setData(List<DeleteTransaction> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Delete Transaction Response Data");
		for (DeleteTransaction q : data) {
			sb.append(q.toString());
		}
		sb.append("\n");
		for (String key : messages.keySet()) {
			sb.append(key + " : " + messages.get(key));
		}
		
		return sb.toString();
	}
	
	
	
}
