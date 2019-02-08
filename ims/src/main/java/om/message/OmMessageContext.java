/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import om.result.OmResult.COMMAND_TYPE;


/**
 * Class provides the mechanics to carry messages to the consumer of an OM request. The OmMessage provides 
 * the base messaging structure expected from OM and the connection factory.
 * @author ddimatos
 *
 */
public class OmMessageContext{
    
    public static final String OM_RETURN_CODE_SUCCESS = "00000000";
    
    private final String omCommandType       = "omCommandType";
    private final String omCommandExecuted   = "omCommandExecuted";
    private final String omMessageSummary    = "omMessageSummary";
    private final String omMessageTittle     = "omMessageTittle";
    private final String omReturnCode        = "omReturnCode";
    private final String omReasonCode        = "omReasonCode";
    private final String omReasonMessage     = "omReasonMessage";
    private final String omReasonText        = "omReasonText";
    private final String omCmderrMbr         = "omCmderrMbr";
    
    private final String omName              = "omName";
    private final String omVersion           = "omVersion";
    private final String omXmlVersion        = "omXmlVersion";
    private final String omStartTime         = "omStartTime";
    private final String omStopTime          = "omStopTime";
    private final String omSequence          = "omSequence";
    private final String omRequestToken1     = "omRequestToken1";
    private final String omRequestToken2     = "omRequestToken2";
    
    //OM Msg context map able to support other maps to be carried in the context
	private Map<String, Message> messages = null;
	private Message omExcutionMessage = null;
	
	//Each IMS can return an CmderrMbr
	private Collection<OmCommandErrorMbr> omCmderrMbrs = null; 
	
	public OmMessageContext(){
	    
		messages = new HashMap<String, Message>();
		//omCmderrMbrs = new ArrayList<OmCommandErrorMbr>(); //Let the user instantiate an instance
		
		omExcutionMessage = new Message();
	    omExcutionMessage.addMessage(this.omCommandType, "");
		omExcutionMessage.addMessage(this.omCommandExecuted, "");
		omExcutionMessage.addMessage(this.omMessageSummary, "");
		omExcutionMessage.addMessage(this.omMessageTittle, "");
		omExcutionMessage.addMessage(this.omReturnCode, OM_RETURN_CODE_SUCCESS); //"00000000");  //TODO: Do we really need this to be 000000
		omExcutionMessage.addMessage(this.omReasonCode, "");
		omExcutionMessage.addMessage(this.omReasonMessage, "");
		omExcutionMessage.addMessage(this.omReasonText, "");
		omExcutionMessage.addMessage(this.omCmderrMbr, omCmderrMbrs);
		
		omExcutionMessage.addMessage(this.omName, "");
		omExcutionMessage.addMessage(this.omVersion, "");
		omExcutionMessage.addMessage(this.omXmlVersion, "");
		omExcutionMessage.addMessage(this.omStartTime, "");
		omExcutionMessage.addMessage(this.omStopTime, "");
		omExcutionMessage.addMessage(this.omSequence, "");
		omExcutionMessage.addMessage(this.omRequestToken1, "");
		omExcutionMessage.addMessage(this.omRequestToken2, "");
		
		messages.put("omMessage", omExcutionMessage);
	}
	
	/**
	 * Add a custom {@link Message} to the {@link OmMessageContext} to be propagated
	 * @param key
	 * @param message
	 */
	public void addMessage(String key, Message message){
		this.messages.put(key, message);
	}
	
	/**
	 * Get a custom {@link Message} to the {@link OmMessageContext} to be propagated
	 * @param key
	 * @return
	 */
	public Message getMessage(String key){
		return this.messages.get(key);
	}
	
	/**
	 * <pre>
	 * Obtain a Map of a particular {@link Message} identified by a key in the {@link OmMessageContext}. 
	 * If a custom one has been added the key would be what was used when adding the Message, otherwise
	 * the defaulted Message key is "omMessage"
	 * <code>
	 *     Map<String, String> mapOmMessage = rtc.getOmMessageContext().getMessageMap("omMessage");
	 * </code>
	 * @param key
	 * @return
	 * </pre>
	 */
	public Map<String, Object> getMessageMap(String key){
	    return this.getMessage(key).getMessageMap();
	}
	
	/**
	 * Returns the OM command executed in this OM interaction
	 * @return
	 */
	public String getOmCommandExecuted() {
		return this.omExcutionMessage.getMessage(this.omCommandExecuted).toString();
	}
	
	/**
	 * Sets the OM command executed for this OM interaction
	 * @param omCommandExecuted
	 */
	public void setOmCommandExecuted(String omCommandExecuted) {
		this.omExcutionMessage.addMessage(this.omCommandExecuted,omCommandExecuted);
	}
	
	/**
	 * Returns the SQL command executed in this OM interaction
	 * @return
	 */
	public String getOmMessageSummary() {
	    //note: An idea is to check the error codes and generate the message here
	    return this.omExcutionMessage.getMessage(this.omMessageSummary).toString();
	}
	    
	/**
	 * Set the SQL command executed for this OM interaction
	 * @param omCacheCommandExecuted
	 */
	public void setOmMessageSummary(String omMessageSummary) {
	    this.omExcutionMessage.addMessage(this.omMessageSummary,omMessageSummary);
	}
	    
	/**
     * Returns the OM command executed in this OM interaction
     * @return
     */
    public String getOmMessageTittle() {
        return this.omExcutionMessage.getMessage(this.omMessageTittle).toString();
    }
    
    /**
     * Sets the OM command executed for this OM interaction
     * @param omCommandExecuted
     */
    public void setOmMessageTittle(String omMessageTittle) {
        this.omExcutionMessage.addMessage(this.omMessageTittle,omMessageTittle);
    }
    
	/**
	 * Command type represtendted by {@link COMMAND_TYPE}
	 * @return
	 */
	public String getOmCommandType() {
		return this.omExcutionMessage.getMessage(this.omCommandType).toString();
	}
	
	/**
	 * Set the type of command that is being executed {@link COMMAND_TYPE}
	 * @param omCommandType
	 */
	public void setOmCommandType(COMMAND_TYPE omCommandType) {
		this.omExcutionMessage.addMessage(this.omCommandType,omCommandType.name());
	}
	
	/**
	 * Commands routed to the Operations Manager (OM) can issue return and reason 
	 * codes to help you identify the source of a problem.
	 * @return
	 */
	public String getOmReturnCode() {
		return this.omExcutionMessage.getMessage(this.omReturnCode).toString();
	}
	
	/**
	 * Set the OM Return code
	 * @param omReturnCode
	 */
	public void setOmReturnCode(String omReturnCode) {
		this.omExcutionMessage.addMessage(this.omReturnCode,omReturnCode == null? "":omReturnCode);
	}
	
	/**
	 * Commands routed to the Operations Manager (OM) can issue return and reason 
     * codes to help you identify the source of a problem. 
	 * @return
	 */
	public String getOmReasonCode() {
		return this.omExcutionMessage.getMessage(this.omReasonCode).toString();
	}
	
	/**
	 * Set the OM reason code
	 * @param omReasonCode
	 */
	public void setOmReasonCode(String omReasonCode) {
		this.omExcutionMessage.addMessage(this.omReasonCode,omReasonCode == null? "":omReasonCode);
	}
	
	/**
	 * Get the OM Reason message for the given reason code
	 * @return
	 */
	public String getOmReasonMessage() {
		return this.omExcutionMessage.getMessage(this.omReasonMessage).toString();
	}
	
	/**
	 * Set the reason message
	 * @param omReasonMessage
	 */
	public void setOmReasonMessage(String omReasonMessage) {
		this.omExcutionMessage.addMessage(this.omReasonMessage,omReasonMessage == null? "":omReasonMessage);
	}
	
	/**
	 * Get the OM reason text for a given reason code
	 * @return
	 */
	public String getOmReasonText() {
		return this.omExcutionMessage.getMessage(this.omReasonText).toString();
	}
	
	/**
	 * set the OM Reason text
	 * @param omReasonText
	 */
	public void setOmReasonText(String omReasonText) {
		this.omExcutionMessage.addMessage(this.omReasonText,omReasonText == null? "":omReasonText);
	}
	
	/**
	 * Get the command error for this om interaction
	 * @return
	 */
	public Collection<OmCommandErrorMbr> getOmCommandErrorMbrs(){
	    //return this.omCmderrMbrs;
	    return ((Collection<OmCommandErrorMbr>)this.omExcutionMessage.getMessage(this.omCmderrMbr));
	}
	
	/**
	 * Set the command error for this om interaction
	 * @param commandErrorMbrs
	 */
	public void setOmCommandErrorMbrs(Collection<OmCommandErrorMbr> commandErrorMbrs){
	    this.omExcutionMessage.addMessage(this.omCmderrMbr, commandErrorMbrs);
	}

	/**
     * Gets the name of the OM instance that processed the type-2 command.
     */
    public String getOmName() {
        return this.omExcutionMessage.getMessage(this.omName).toString();
    }
    
    /**
     * Sets the name of the OM instance that processed the type-2 command.
     */
    public void setOmName(String omName) {
        this.omExcutionMessage.addMessage(this.omName,omName == null? "":omName);
    }
    
    /**
     * Gets the version number of the OM that processed the type-2 command.
     */
    public String getOmVersion() {
        return this.omExcutionMessage.getMessage(this.omVersion).toString();
    }
    
    /**
     * Sets the version number of the OM that processed the type-2 command.
     */
    public void setOmVersion(String omVersion) {
        this.omExcutionMessage.addMessage(this.omVersion,omVersion == null? "":omVersion);
    }
    
    /**
     * Gets the XML version number for the response message.
     */
    public String getOmXmlVersion() {
        return this.omExcutionMessage.getMessage(this.omXmlVersion).toString();
    }
    
    /**
     * Sets the XML version number for the response message.
     * @param omReasonText
     */
    public void setOmXmlVersion(String omXmlVersion) {
        this.omExcutionMessage.addMessage(this.omXmlVersion,omXmlVersion == null? "":omXmlVersion);
    }
    
    /**
     * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     */
    public String getOmStartTime() {
        return this.omExcutionMessage.getMessage(this.omStartTime).toString();
    }
    
    /**
     *  Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     * @param omReasonText
     */
    public void setOmStartTime(String omStartTime) {
        this.omExcutionMessage.addMessage(this.omStartTime,omStartTime == null? "":omStartTime);
    }
    
    /**
     * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     */
    public String getOmStopTime() {
        return this.omExcutionMessage.getMessage(this.omStopTime).toString();
    }
    
    /**
     * Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     */
    public void setOmStopTime(String omStopTime) {
        this.omExcutionMessage.addMessage(this.omStopTime,omStopTime == null? "":omStopTime);
    }
    
    /**
     * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     */
    public String getOmSequence() {
        return this.omExcutionMessage.getMessage(this.omSequence).toString();
    }
    
    /**
     * Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
     */
    public void setOmSequence(String omSequence) {
        this.omExcutionMessage.addMessage(this.omSequence,omSequence == null? "":omSequence);
    }
    
    /**
     * Gets the user-specified RQSTTKN1 value for the type-2 command.
     * Unprintable characters are converted to periods (.) in the output.
     */
    public String getOmRequestToken1() {
        return this.omExcutionMessage.getMessage(this.omRequestToken1).toString();
    }
    
    /**
     * Sets the user-specified RQSTTKN1 value for the type-2 command.
     * Unprintable characters are converted to periods (.) in the output.
     */
    public void setOmRequestToken1(String omRequestToken1) {
        this.omExcutionMessage.addMessage(this.omRequestToken1,omRequestToken1 == null? "":omRequestToken1);
    }
    
    /**
     * Gets the user-specified RQSTTKN2 value for the type-2 command.
     * Unprintable characters are converted to periods (.) in the output.
     */
    public String getOmRequestToken2() {
        return this.omExcutionMessage.getMessage(this.omRequestToken2).toString();
    }
    
    /**
     * Sets the user-specified RQSTTKN2 value for the type-2 command.
     * Unprintable characters are converted to periods (.) in the output.
     */
    public void setOmRequestToken2(String omRequestToken2) {
        this.omExcutionMessage.addMessage(this.omRequestToken2,omRequestToken2 == null? "":omRequestToken2);
    }
    
	@Override
	public String toString(){
	    StringBuilder result = new StringBuilder();
	    String NEW_LINE = System.getProperty("line.separator");
	        
	    String format = "| %1$-25s| %2$-50s|" + NEW_LINE;
	    String line = "+------------------------------------------------------------------------------+" + NEW_LINE;
	        
	    result.append(line);
	    result.append(String.format(format, "Object", this.getClass().getSimpleName() ));  
	    result.append(line);
	    result.append(String.format(format, "Variable Description", "Value")); 
	    
	    for(Map.Entry<String, Message> entry : messages.entrySet()){
	        result.append(entry.getValue().toString());
	    }
	       
	    //Add omCommandErrorMbrs too toString()
	    return result.toString();
	}
}
