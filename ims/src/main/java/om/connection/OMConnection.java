/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.connection;

import java.io.InputStream;

import om.exception.OmConnectionException;
import om.exception.OmException;
import om.services.Om;

/**
 * This abstract class is used to initialize an {@link Om} interaction for executing om services or 
 * IMS commands (Type1/Type2).
 */
public abstract class OMConnection {

	/**
	 * Default connection type is set to IMSCON (IMS Connect). This connection type is used by the OM layer 
	 * when recording errors that occur related to a connection. The error recorded also takes into account
	 * the connection type that has an error to identify the extender who is responsible for the connection.
	 * 
	 * If you are extending via an extension point, this will be overridden by the setConnectionType() and 
	 * it will use the Extension Point Type value.
	 */
	private String connectionType = "IMSCON";
	
	private int environment = -1;
	
	private String imsplex = "";
	
	/**
	 * <pre>
	 * Method will execute an IMS Type1 or Type2 command and return the input stream as a response. 
	 * The input stream response returned must the valid XML response sent by OM. When sending a Type1
	 * command, there is no need to prefix the command with a forward slash. Routing is supported and
	 * should be appended as part of the command. 
	 * 
	 * For example a command sent should look like:
	 * CMD(QUERY IMSPLEX NAME(*) SHOW(ALL)) ROUTE(*)
	 * 
	 * @param command String type 1 or type 2  command.
	 * @return input stream response from OM as a valid XML
	 * @throws OmException
	 * @throws OmConnectionException 
	 * </pre>
	 */
	public abstract InputStream execute(String command) throws OmException,OmConnectionException; 
	
	// *****************************************************************************************
	// * Valid XML Payload for command: "CMD(QUERY IMSPLEX NAME(*) SHOW(ALL))"
	// * "<?xml version=\"1.0\"?><!DOCTYPE imsout SYSTEM \"imsout.dtd\"><imsout><ctl><omname>OM1OM   </omname><omvsn>1.5.0</omvsn><xmlvsn>20  </xmlvsn><statime>2012.356 18:16:10.374274</statime><stotime>2012.356 18:16:10.374620</stotime><staseq>CAA71204B8482449</staseq><stoseq>CAA71204B85DCD14</stoseq><rqsttkn1>16439672........</rqsttkn1><rc>00000000</rc><rsn>00000000</rsn></ctl><cmd><userid>RACFUID </userid><verb>QRY </verb><kwd>IMSPLEX         </kwd><input>QUERY IMSPLEX NAME(*) SHOW(ALL) </input></cmd><cmdrsphdr><hdr slbl=\"IMSPLX\" llbl=\"IMSplex\" scope=\"LCL\" sort=\"A\" key=\"1\" scroll=\"NO\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"MBR\" llbl=\"MbrName\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"CC\" llbl=\"CC\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"4\" dtype=\"INT\" align=\"right\" /><hdr slbl=\"IMSMBR\" llbl=\"Member\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"NO\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"JOB\" llbl=\"JobName\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"TYP\" llbl=\"Type\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"5\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"STYP\" llbl=\"Subtype\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"VER\" llbl=\"Version\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"OS\" llbl=\"OSName\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"8\" dtype=\"CHAR\" align=\"left\" /><hdr slbl=\"STT\" llbl=\"Status\" scope=\"LCL\" sort=\"N\" key=\"0\" scroll=\"YES\" len=\"*\" dtype=\"CHAR\" align=\"left\" /></cmdrsphdr><cmdrspdata><rsp>IMSPLX(CSLPLEX1) MBR(OM1OM) IMSMBR(HWS1) CC(   0) JOB(HWS1) TYP(IMSCON) STYP() VER(12.1.0) OS(CSDMEC20) STT(ACTIVE)</rsp><rsp>IMSPLX(CSLPLEX1) MBR(OM1OM) IMSMBR(OM1OM) CC(   0) JOB(OM1) TYP(OM) STYP() VER(1.5.0) OS(CSDMEC20) STT(READY,ACTIVE)</rsp><rsp>IMSPLX(CSLPLEX1) MBR(OM1OM) IMSMBR(RM1RM) CC(   0) JOB(RM1) TYP(RM) STYP(SNGLRM) VER(1.5.0) OS(CSDMEC20) STT(READY,ACTIVE)</rsp><rsp>IMSPLX(CSLPLEX1) MBR(OM1OM) IMSMBR(IMS1) CC(   0) JOB(IMS1) TYP(IMS) STYP(DBDC) VER(12.1.0) OS(CSDMEC20) STT(READY,ACTIVE)</rsp><rsp>IMSPLX(CSLPLEX1) MBR(OM1OM) IMSMBR(SCI1SC) CC(   0) JOB(SCI1) TYP(SCI) STYP() VER(1.5.0) OS(CSDMEC20) STT(READY,ACTIVE)</rsp></cmdrspdata></imsout>";
	// *****************************************************************************************
	
	/**
	 * Return the environment ID used to identify the system. This is used by the OM code
	 * to assist with retrieving values from the Derby cache for uniqueness. 
	 * @return ID for the environment
	 */
	public int getEnvironment(){
		return this.environment;
	}
	
	/**
	 * Return the IMSPlex Name used to identify the IMSPLex within an Environment. There can be 
	 * more than one IMSPLex. The name is also used to assist with retrieving values from the
	 * Derby Cache.
	 * @return Name of the IMSPlex
	 */
	public String getImsplex(){
		return this.imsplex;
	}
	
	/**
	 * Set the environment ID used to identify the system. This is used by the OM code
	 * to assist with retrieving values from the Derby cache for uniqueness.
	 * @return ID for the environment
	 */
	public void setEnvironment(int environment){
		this.environment = environment;
	}
	
	/**
	 * Set the IMSPlex Name used to identify the IMSPLex within an Environment. There can be 
	 * more than one IMSPLex. The name is also used to assist with retrieving values from the
	 * Derby Cache.
	 * @return Name of the IMSPlex
	 */
	public void setImsplex(String imsplex){
		this.imsplex = imsplex;
	}
	
	
	/**
	 * Default connection type is set to IMSCON (IMS Connect). This connection type is used by the OM layer 
	 * when recording errors that occur related to a connection. The error recorded also takes into account
	 * the connection type that has an error to identify the extender who is responsible for the connection.
	 * 
	 * If you are extending via an extension point, this will be overridden by the setConnectionType() and 
	 * it will use the Extension Point Type value.
	 * @return Connection Type name
	 */
	public String getConnectionType() {
		return this.connectionType;
	}

	/**
	 * Set the connection type. The default connection type is set to IMSCON (IMS Connect). This connection type is used by the OM layer 
	 * when recording errors that occur related to a connection. The error recorded also takes into account
	 * the connection type that has an error to identify the extender who is responsible for the connection.
	 * 
	 * If you are extending via an extension point, this will be overridden by the setConnectionType() and 
	 * it will use the Extension Point Type value.
	 * @return Connection Type name
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 * <pre>
	 * Method if implemented will be managed by the instance of {@link Om} and should be called on exit or
	 * completed use of the instance. 
	 * Example:
	 * Om om = new Om(myOmconnection);
	 * 
	 * if(om != null) {
	 *    om.releaseConnection();
	 * }
	 * </pre>
	 */
	public abstract void releaseConnection() ;	
}
