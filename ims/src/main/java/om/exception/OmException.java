/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.exception;


/**
 * IMS OM exception. This exception should be used to propagate an error that needs to be thrown. Keeping mind
 * that OM could send back non-zero return codes for commands that execute successfully but some of the resources
 * did not complete the command successfully, thus indicating the CC and CCTXT should be investigated for each member. 
 * @author ddimatos
 *
 */
public class OmException extends Exception{
    private String errorNumber =""; // = "HWS0000E";
	private String omCommandExecuted = "";
    private String omReturnCode = "";
    private String omReasonCode = "";
    private String omReasonMessage = "";
    private String omReasonText = "";
    
//    Leave this for now till its decided this support is not needed in the exception
//    private String omName              = "";
//    private String omVersion           = "";
//    private String omXmlVersion        = "";
//    private String omStartTime         = "";
//    private String omStopTime          = "";
//    private String omSequence          = "";
//    private String omRequestToken1     = "";
//    private String omRequestToken2     = "";
    
	private static final long serialVersionUID = 8718268572577704527L;

	public OmException(String message){
		super(message);
	}
	
	public OmException(String message,Throwable cause){
		super(message,cause);
	}

	public OmException(String message,String errorNumber){
		super(message);
		this.errorNumber = errorNumber;
	}
	
	public OmException(Throwable cause,String errorNumber){
		super(cause);
		this.errorNumber = errorNumber;
	}
	
	public OmException(Throwable cause){
		super(cause);
	}
	
    public void setErrorNumber(String errorNumber){
        this.errorNumber = errorNumber;
    }
    
    public String getErrorNumber(){
        return this.errorNumber;
    }
    
    public String getOmCommandExecuted() {
		return omCommandExecuted;
	}

	public void setOmCommandExecuted(String omCommandExecuted) {
		this.omCommandExecuted = omCommandExecuted;
	}

	public String getOmReturnCode() {
		return omReturnCode;
	}

	public void setOmReturnCode(String omReturnCode) {
		this.omReturnCode = omReturnCode;
	}

	public String getOmReasonCode() {
		return omReasonCode;
	}

	public void setOmReasonCode(String omReasonCode) {
		this.omReasonCode = omReasonCode;
	}

	public String getOmReasonMessage() {
		return omReasonMessage;
	}

	public void setOmReasonMessage(String omReasonMessage) {
		this.omReasonMessage = omReasonMessage;
	}

	public String getOmReasonText() {
		return omReasonText;
	}

	public void setOmReasonText(String omReasonText) {
		this.omReasonText = omReasonText;
	}
	
	
//	Leave this for now till its decided this support is not needed in the exception
//	
//	/**
//    * Gets the name of the OM instance that processed the type-2 command.
//    */
//   public String getOmName() {
//       return this.omName;
//   }
//   
//   /**
//    * Sets the name of the OM instance that processed the type-2 command.
//    */
//   public void setOmName(String omName) {
//       this.omName = omName;
//   }
//   
//   /**
//    * Gets the version number of the OM that processed the type-2 command.
//    */
//   public String getOmVersion() {
//       return this.omVersion;
//   }
//   
//   /**
//    * Sets the version number of the OM that processed the type-2 command.
//    */
//   public void setOmVersion(String omVersion) {
//       this.omVersion = omVersion;
//   }
//   
//   /**
//    * Gets the XML version number for the response message.
//    */
//   public String getOmXmlVersion() {
//       return this.omXmlVersion;
//   }
//   
//   /**
//    * Sets the XML version number for the response message.
//    * @param omReasonText
//    */
//   public void setOmXmlVersion(String omXmlVersion) {
//       this.omXmlVersion = omXmlVersion;
//   }
//   
//   /**
//    * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    */
//   public String getOmStartTime() {
//       return this.omStartTime;
//   }
//   
//   /**
//    *  Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    * @param omReasonText
//    */
//   public void setOmStartTime(String omStartTime) {
//       this.omStartTime = omStartTime;
//   }
//   
//   /**
//    * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    */
//   public String getOmStopTime() {
//       return this.omStopTime;
//   }
//   
//   /**
//    * Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    */
//   public void setOmStopTime(String omStopTime) {
//       this.omStopTime = omStopTime;
//   }
//   
//   /**
//    * Gets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    */
//   public String getOmSequence() {
//       return this.omSequence;
//   }
//   
//   /**
//    * Sets the time that OM started processing the command. The timestamp is given in the following format: yyyy.ddd hh:mm:ss.th
//    */
//   public void setOmSequence(String omSequence) {
//       this.omSequence = omSequence;
//   }
//   
//   /**
//    * Gets the user-specified RQSTTKN1 value for the type-2 command.
//    * Unprintable characters are converted to periods (.) in the output.
//    */
//   public String getOmRequestToken1() {
//       return this.omRequestToken1;
//   }
//   
//   /**
//    * Sets the user-specified RQSTTKN1 value for the type-2 command.
//    * Unprintable characters are converted to periods (.) in the output.
//    */
//   public void setOmRequestToken1(String omRequestToken1) {
//       this.omRequestToken1 = omRequestToken1;
//   }
//   
//   /**
//    * Gets the user-specified RQSTTKN2 value for the type-2 command.
//    * Unprintable characters are converted to periods (.) in the output.
//    */
//   public String getOmRequestToken2() {
//       return this.omRequestToken2;
//   }
//   
//   /**
//    * Sets the user-specified RQSTTKN2 value for the type-2 command.
//    * Unprintable characters are converted to periods (.) in the output.
//    */
//   public void setOmRequestToken2(String omRequestToken2) {
//       this.omRequestToken2 = omRequestToken2;
//   }
}
