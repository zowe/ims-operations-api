/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.tran.update;

import java.lang.reflect.Field;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO from a Update TRAN command that represents output for one transaction")
public class UpdateTransaction {
	
	@Schema(description = "Completion code. The completion code indicates whether IMS was able to process the command for the specified resource. The completion code is always returned. ")
	String cc;
	@Schema(description = "Completion code text that briefly explains the meaning of the nonzero completion code.")
	String cctxt;
	@Schema(description = "Conversation ID of conversation associated with transaction that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String convid;
	@Schema(description = "Error text with diagnostic information. Error text can be returned for a nonzero completion code and contains information that further explains the completion code.")
	String errt;
	@Schema(description = "Indicates that the response line is for the global update.")
	String gbl;
	@Schema(description = "APPC LU name associated with the transaction conversation that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String lu;
	@Schema(description = "The IMSplex member that built the output line. The IMS identifier of the IMS for which the transaction information is displayed. The IMS identifier is always returned.")
	String mbr;
	@Schema(description = "Node name of static node associated with the transaction conversation that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String node;
	@Schema(description = "OTMA tmember name associated with the transaction conversation that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String tmem;
	@Schema(description = "OTMA tpipe name associated with the transaction conversation that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String tpip;
	@Schema(description = "The transaction name. The transaction name is always displayed.")
	String tran;
	@Schema(description = "User name of dynamic user associated with the transaction conversation that caused the update to fail with a completion code of C'1A'. This information can be used to exit the conversation, before attempting the update again.")
	String user;
	
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
	public String getConvid() {
		return convid;
	}
	public void setConvid(String convid) {
		this.convid = convid;
	}
	public String getErrt() {
		return errt;
	}
	public void setErrt(String errt) {
		this.errt = errt;
	}
	public String getGbl() {
		return gbl;
	}
	public void setGbl(String gbl) {
		this.gbl = gbl;
	}
	public String getLu() {
		return lu;
	}
	public void setLu(String lu) {
		this.lu = lu;
	}
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getTmem() {
		return tmem;
	}
	public void setTmem(String tmem) {
		this.tmem = tmem;
	}
	public String getTpip() {
		return tpip;
	}
	public void setTpip(String tpip) {
		this.tpip = tpip;
	}
	public String getTran() {
		return tran;
	}
	public void setTran(String tran) {
		this.tran = tran;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
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
