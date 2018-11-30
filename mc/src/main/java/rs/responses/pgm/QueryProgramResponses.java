package rs.responses.pgm;

import java.util.List;
import java.util.Map;

import rs.responses.Message;

public class QueryProgramResponses {

	List<QueryProgramResponse> data;
	Map<String, Message> messages;

	public Map<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(Map<String, Message> messages) {
		this.messages = messages;
	}

	public List<QueryProgramResponse> getData() {
		return data;
	}

	public void setData(List<QueryProgramResponse> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Query Program Response Data");
		for (QueryProgramResponse q : data) {
			sb.append(q.toString());
		}
		sb.append("\n");
		for (String key : messages.keySet()) {
			sb.append(key + " : " + messages.get(key));
		}
		
		return sb.toString();
	}
	
	
	
}
