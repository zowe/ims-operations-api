/**
 *  Copyright IBM Corporation 2018, 2019
 */
package icon.helpers;

import com.ibm.ims.connect.TmInteractionAttributes;

public class TmInteractionProperties extends TmInteractionAttributes {
	
	private static final String DEFUALT_IMS_CONNECT_CODEPAGE = "CP037";
	public final static String BLANK_USERID = "        ";

	public TmInteractionProperties(String imsDatastoreName) {
		setImsDatastoreName(imsDatastoreName);
		setImsConnectCodepage(DEFUALT_IMS_CONNECT_CODEPAGE);
		setImsConnectTimeout(TIMEOUT_2_MINUTES);
		setInteractionTimeout(TIMEOUT_5_MINUTES);
		setInteractionTypeDescription(INTERACTION_TYPE_DESC_TYPE2_COMMAND);
		
	    setRacfUserId(BLANK_USERID);
	    setRacfPassword(BLANK_USERID);
	    setRacfGroupName("");
	        
	}

	public TmInteractionProperties(String imsDatastoreName, String racfUserId, String racfGroupName, String racfPassword) {
		setImsDatastoreName(imsDatastoreName);
		setImsConnectCodepage(DEFUALT_IMS_CONNECT_CODEPAGE);
		setImsConnectTimeout(TIMEOUT_2_MINUTES);
		setInteractionTimeout(TIMEOUT_5_MINUTES);
		setInteractionTypeDescription(INTERACTION_TYPE_DESC_TYPE2_COMMAND);
		
		if(racfUserId != null ){
			setRacfUserId(racfUserId);
		}else{
			setRacfUserId(BLANK_USERID);
		}
		
		if(racfPassword != null){
			setRacfPassword(racfPassword);
		}else{
			setRacfPassword(DEFAULT_RACF_PASSWORD);
		}
		
		setRacfGroupName("");
		//TODO: Need to set this to blank, using the default messes up
//		if(racfGroupName != null){
//			setRacfGroupName(racfGroupName);
//		}else{
//			setRacfGroupName(DEFAULT_RACF_GROUP_NAME);
//		}
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
	    String NEW_LINE = System.getProperty("line.separator");
	    String format = "| %1$-30s| %2$-30s| %3$-15s|" + NEW_LINE;
	    String line = "+--------------------------------------------------------------------------------+" + NEW_LINE;
	    result.append(line);
        result.append(String.format(format, "Object", this.getClass().getSimpleName(),"" ));  
	    result.append(line);
	    result.append(String.format(format, "TmInteractionProperties", "Value", "Measurement")); 
	    result.append(line);
		
	    if(getInteractionTypeDescription() != null) result.append(String.format(format, "Interaction Type", getInteractionTypeDescription()	,"String"));
	    if(getImsDatastoreName() != null) 	result.append(String.format(format, "Data Store Name", 			getImsDatastoreName()			,"String")); 
	    if(getImsConnectCodepage() != null) result.append(String.format(format, "Connection Codepage", 		getImsConnectCodepage()			,"String")); 
	    if(getRacfUserId() != null) 		result.append(String.format(format, "RACF User ID",				getRacfUserId()					,"String")); 
	    //if(getRacfPassword() != null)        result.append(String.format(format, "RACF User Password",            getRacfPassword()         ,"String")); 
	    if(getRacfGroupName() != null) 		result.append(String.format(format, "RACF Group Name", 			getRacfGroupName()				,"String")); 
	    									result.append(String.format(format, "IMS Connection Timeout",	Integer.toString(getImsConnectTimeout())	,"Integer")); 
	    									result.append(String.format(format, "Interaction Timeout",  	Integer.toString(getInteractionTimeout())	,"Integer")); 
	    result.append(line); 
	    
	    return result.toString();
	}
}
