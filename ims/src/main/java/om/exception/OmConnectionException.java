/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.exception;

/**
 * Exception to be used to describe a connection exception when attempting to communicate with OM. 
 * For example, if your implementation of the OM connection factory failed to communicate with an
 * end-point, you would want to propagate a connection error. 
 * @author ddimatos
 *
 */
public class OmConnectionException extends Exception{

	private static final long serialVersionUID = 6052258832946049756L;
	private String errorNumber = ""; 
	private String connectionType = ""; 
	private int connectionReturnCode = -1; 
	private int connectionReasonCode = -1; 
	private int environment = -1;
	private String imsplex = ""; 
	
	
	public OmConnectionException(String message){
		super(message);
	}
	
	public OmConnectionException(String message,Throwable cause){
		super(message,cause);
	}

	public OmConnectionException(String message,String errorNumber){
		super(message);
		this.errorNumber = errorNumber;
	}
	
	public OmConnectionException(Throwable cause,String errorNumber){
		super(cause);
		this.errorNumber = errorNumber;
	}
	
	public OmConnectionException(Throwable cause){
		super(cause);
	}
	
    public void setErrorNumber(String errorNumber){
        this.errorNumber = errorNumber;
    }
    
    public String getErrorNumber(){
        return this.errorNumber;
    }

	public String getConnectionType() {
		return this.connectionType;
	}

	public void setConnectionType(String connectionType) {
	    this.connectionType  = (connectionType == null  ? "" : connectionType);
	}

	public int getConnectionReturnCode() {
		return connectionReturnCode;
	}

	public void setConnectionReturnCode(int connectionReturnCode) {
		this.connectionReturnCode = connectionReturnCode;
	}

	public int getConnectionReasonCode() {
		return connectionReasonCode;
	}

	public void setConnectionReasonCode(int connectionReasonCode) {
		this.connectionReasonCode = connectionReasonCode;
	}

	public int getEnvironmentId() {
		return environment;
	}

	public void setEnvironmentId(int environment) {
		this.environment = environment;
	}

	public String getImsplexName() {
		return imsplex;
	}

	public void setImsplexName(String imsplex) {
		this.imsplex = imsplex;
	}
}
