/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.start;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

public class StartProgramOutput {

	@Schema(description = "A list of StartProgram objects. Represents the JSON data output for all programs from the command")
	List<StartProgram> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	HashMap<String, Message> messages;

	public HashMap<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public List<StartProgram> getData() {
		return data;
	}

	public void setData(List<StartProgram> data) {
		this.data = data;
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
