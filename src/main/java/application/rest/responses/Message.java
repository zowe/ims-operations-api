/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses;

import java.lang.reflect.Field;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO that describes a single message element in the messages portion of the JSON output. Returned by an IMS mbr or OM")
public class Message {

	@Schema(description = "Reason Code Text")
	String rsntxt;
	@Schema(description = "Message from OM. Usually returned only when there is an error")
	String message;
	@Schema(description = "Return Code")
	String rc;
	@Schema(description = "The command executed")
	String command;
	@Schema(description = "Reason Code")
	String rsn;
	
	public String getRsntxt() {
		return rsntxt;
	}
	public void setRsntxt(String rsntxt) {
		this.rsntxt = rsntxt;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRc() {
		return rc;
	}
	public void setRc(String rc) {
		this.rc = rc;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getRsn() {
		return rsn;
	}
	public void setRsn(String rsn) {
		this.rsn = rsn;
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
