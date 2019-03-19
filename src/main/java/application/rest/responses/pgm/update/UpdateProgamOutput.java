/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.update;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import application.rest.responses.Message;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that represents all the data returned from OM following a Update PGM command")
public class UpdateProgamOutput {
	
	@Schema(description = "A list of UpdateProgram objects. Represents the JSON data output for all programs from the command")
	List<UpdateProgram> data;
	@Schema(description = "A map that represents messages that are returned from OM after submitting the command. The key is either the IMS member or OM that returned the message")
	HashMap<String, Message> messages;
	
	public List<UpdateProgram> getData() {
		return data;
	}
	public void setData(List<UpdateProgram> data) {
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
