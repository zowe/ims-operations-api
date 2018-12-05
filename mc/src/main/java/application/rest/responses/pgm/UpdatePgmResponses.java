package application.rest.responses.pgm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;

public class UpdatePgmResponses {
	List<UpdateProgramsResponse> data;
	HashMap<String, Message> messages;
	
	public List<UpdateProgramsResponse> getData() {
		return data;
	}
	public void setData(List<UpdateProgramsResponse> data) {
		this.data = data;
	}
	public HashMap<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			result.append("  ");
			try {
				result.append( field.getName() );
				result.append(": ");
				//requires access to private field:
				result.append( field.get(this) );
			} catch ( IllegalAccessException ex ) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		return result.toString();
	}
	
	
}
