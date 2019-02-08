/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.tran.delete;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO from a Delete TRAN command that represents output for one transaction")
public class DeleteTransaction {
	
	@Schema(description = "Completion Code")
	String cc;
	@Schema(description = "Completion code text that briefly explains the meaning of the non-zero completion code.")
	String cctxt;
	@Schema(description = "Conversation id of conversation associated with transaction that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
	String convid;
	@Schema(description = "Error text that provides diagnostic information. Error text can be returned for a nonzero completion code and further explains the completion code.")
	String errt;
	@Schema(description = "IMSplex member that built the output line.")
	String mbr;
	@Schema(description = "APPC LU name associated with the transaction conversation that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
	String lu;
	@Schema(description = "Node name of static node associated with transaction conversation that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
	String node;
	@Schema(description = "OTMA tmember name associated with transaction conversation that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
	String tmem;
	@Schema(description = "OTMA tpipe name associated with the transaction conversation that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
	String tpip;
	@Schema(description = "Transaction name.")
	String tran;
	@Schema(description = "User name of dynamic user associated with the transaction conversation that caused the delete to fail with a completion code of C'1A'. This information may be used to exit the conversation, before attempting the delete again.")
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
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getLu() {
		return lu;
	}
	public void setLu(String lu) {
		this.lu = lu;
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
	
	

}
