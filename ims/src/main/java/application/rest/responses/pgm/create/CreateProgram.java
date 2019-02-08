/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.create;

import java.lang.reflect.Field;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO from a Create PGM command that represents output for one program")
public class CreateProgram {
	
	@Schema(description = "Completion Code")
	String cc;
	@Schema(description = "Completion code text that briefly explains the meaning of the non-zero completion code.")
	String cctxt;
	@Schema(description = "IMSplex member that built the output line.")
	String mbr;
	@Schema(description = "Program name.")
	String pgm;
	
	
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getCctxt() {
		return cctxt;
	}
	public void setCctxt(String cctxt) {
		this.cctxt = cctxt;
	}
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getPgm() {
		return pgm;
	}
	public void setPgm(String pgm) {
		this.pgm = pgm;
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
