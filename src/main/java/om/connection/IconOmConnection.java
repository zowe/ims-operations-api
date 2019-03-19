/**
 *  Copyright IBM Corporation 2018, 2019
 */


package om.connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.ims.connect.ImsConnectApiException;
import com.ibm.ims.connect.ImsConnectCommunicationException;
import com.ibm.ims.connect.ImsConnectExecutionException;
import com.ibm.ims.connect.OutputMessage;

import icon.helpers.ConnectionFactoryProperties;
import icon.helpers.ImsConnectConnection;
import icon.helpers.TmInteractionProperties;
import om.exception.OmConnectionException;
import om.exception.OmException;
import om.message.IQEO;

/**
 * Class provides a connenction to IMS Connect extending interface OMConnection. Provides members
 * to execute commands and keep track of connection statistics. 
 * @author ddimatos
 *
 */
public final class IconOmConnection extends OMConnection{
    final static Logger logger = LoggerFactory.getLogger(IconOmConnection.class);
    
	//Connection status variables
	private boolean 	isSessionInUse	 	= false;	//Signals if the session is in use
	private boolean 	isErrorInConnection = false;	//Signals if an error has occurred in the underlying connection
	private long 		sessionCreationTime	= 0; 		//Time connection was created
	private long 		sessionAccessed 	= 0; 		//Last request, sessionRequestTime
	private long 		sessionReturned		= 0; 		//Last response sessionResponseTime
	private String 		sessionId			= "";		//Objects hash ID used later to identify this particular instance
	private boolean 	isSessionTemp		= false;	//For short lived temparary connections this flag is set so the executor can disconnect it
	  
	//Global ICON connection used for execution of an OM command
	private ImsConnectConnection imsConnectConnection = null;
	  
	//keep the properties local so when a connection times out we can recreate them
	private ConnectionFactoryProperties connectionFactoryProperties = null;
	private TmInteractionProperties tmInteractionProperties = null;
	  
	/**
	 * Instantiate a connection with connection factory properties 
	 * @param connectionFactoryProperties
	 * @param tmInteractionProperties
	 */
	public IconOmConnection(ConnectionFactoryProperties connectionFactoryProperties, TmInteractionProperties tmInteractionProperties) {
	  this.connectionFactoryProperties = connectionFactoryProperties;
	  this.tmInteractionProperties = tmInteractionProperties;
	  sessionId = Integer.toHexString(System.identityHashCode(this));
	}
	  
	@Override
	public InputStream execute(String command) throws OmException, OmConnectionException{
	    if(getCallingMethodName().equals("noOpCommand")){
	        return execute(command,true,true);
	    }
	    return execute(command,false,false);
	}
	
	//Don't want this in the interface, its meant for use by E4A code only code only.
	private InputStream execute(String command,boolean overideSSessionState, boolean overideDisconnect) throws OmException, OmConnectionException{
	    
		OutputMessage outputMessage = null;
		
		//UPDATE: OMConnectionManager.getConnection(...) now returns connections with session in use (true) therefor
		//this case here would be hit every time creating a new connection and ophaning others that can not be closed.
		//if(isSessionInUse){
		    //This occurs only if there is a concurrency issue. The OMConnectionManager.getConnection(...) in OM does not
		    //handle concurrency therefore it is left up to the implementor. Instead of throwing an exception we can
		    //recreate the ICON connection with its original connection properties including honoring RACF. 
		 //   this.connectionFactoryProperties =connectionFactoryProperties.getNewInstance();
		 //   createConnection();
		//}
		
		setSessionInUse(true);	//Connection is in use //TODO: We don't really need this setter, its set by get connection
		
		setSessionAccessed(System.nanoTime());	//Connection was accessed for a request
		  
		try {
		    //Long startTime = System.nanoTime();
			outputMessage = imsConnectConnection.execute(command);
	        //System.out.println("Interaction Time with IMS for command [" + command + "] = " + ((double)(System.nanoTime()-startTime)/ 1000000000.0));
			
			if(logger.isDebugEnabled()){
			    logger.debug("XML Payload returned from OM: " + outputMessage.getDataAsString());
			}
			
		}catch (ImsConnectCommunicationException e) {  //Also appears as ImsConnectCommunicationException type exception
			setErrorInConnection(true);
			setSessionInUse(false);
			
			//How come there is no disconnect here only for temp? 
			
			if(isSessionTemp){
				this.disconnect();
			}
			
			OmConnectionException omConnectionException = new OmConnectionException(e, e.getErrorNumber());
			omConnectionException.setConnectionType(this.getConnectionType());
			omConnectionException.setEnvironmentId(getEnvironment());
			omConnectionException.setImsplexName(getImsplex());
			throw omConnectionException;
			
		} catch (ImsConnectApiException e) { 
			setErrorInConnection(true);
			setSessionInUse(false);
			
			if(isSessionTemp){
				this.disconnect();
			}
			
			OmConnectionException omConnectionException = new OmConnectionException(e, e.getErrorNumber());
			omConnectionException.setConnectionType(this.getConnectionType());
			omConnectionException.setEnvironmentId(getEnvironment());
			omConnectionException.setImsplexName(getImsplex());
			throw omConnectionException;
			
		} catch (ImsConnectExecutionException e){  //RACF error ("HWS0043E")
			setErrorInConnection(true);
			setSessionInUse(false);
			
			if(isSessionTemp){
				this.disconnect();
			}
			
			OmConnectionException omConnectionException = new OmConnectionException(e, e.getErrorNumber());
			omConnectionException.setConnectionReasonCode(e.getReasonCode());
			omConnectionException.setConnectionReturnCode(e.getReturnCode());
			omConnectionException.setConnectionType(this.getConnectionType());
			omConnectionException.setEnvironmentId(getEnvironment());
			omConnectionException.setImsplexName(getImsplex());
			
			throw omConnectionException;

		} catch (Exception e) { //cause	  (id=2691)	
			
			setErrorInConnection(true);
			setSessionInUse(false);
			if(isSessionTemp){
				this.disconnect();
			}
			OmException omException = new OmException(e);
			omException.setOmCommandExecuted(command);
			throw omException;
		} catch (OutOfMemoryError e ){
			
		    setErrorInConnection(true);
            setSessionInUse(false);
            if(isSessionTemp){
                this.disconnect();
            }
            OmException omException = new OmException("Out of Memory Error");
            omException.setOmCommandExecuted(command);
            throw omException;
		}
		  
		// Access the Connect API's response and convert it to an InputStream to meet the contractual requirement
		InputStream is = new ByteArrayInputStream(outputMessage.getDataAsArrayOfByteArrays()[0]);
         
		setSessionReturned(System.nanoTime());	//Connection was returned
		
		//if overideSSessionState is set to true, then don't flag the connection as not in use
//		if(!overideSSessionState){
//		    setSessionInUse(false);	  //Flag connection is free to use
//		}
		
		if(isSessionTemp && !overideDisconnect){
			this.disconnect();
				
			if(logger.isDebugEnabled()){
				logger.debug("Detected a temporary connection; connection will be disconnected");
			}
		}

		return is;
	}
	
	/**
	 * Create a new ImsConnectConnection to use with executing OM commands through the IMS Connect API
	 * @throws OmConnectionException 
	 */
	public void createConnection() throws OmConnectionException{
		try {
			  imsConnectConnection = new ImsConnectConnection(this.connectionFactoryProperties, this.tmInteractionProperties);
		} catch (SocketException e) {
			setErrorInConnection(true);
			String strMsg = "( " + this.connectionFactoryProperties.getHostName() + ", " + this.connectionFactoryProperties.getPortNumber() + " ) " ;
			OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0001E.msg(new Object[] {strMsg + e.getLocalizedMessage()}));
			omConnectionException.setImsplexName(tmInteractionProperties.getImsDatastoreName());
			throw omConnectionException;
		} catch(ImsConnectCommunicationException e) {
			  setErrorInConnection(true);
			  String strMsg = "( " + this.connectionFactoryProperties.getHostName() + ", " + this.connectionFactoryProperties.getPortNumber() + " ) " ;
			  OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0006E.msg(new Object[] {strMsg + e.getLocalizedMessage()}));
			  omConnectionException.setErrorNumber(e.getErrorNumber());
			  omConnectionException.setImsplexName(tmInteractionProperties.getImsDatastoreName());
			  throw omConnectionException;
		} catch (ImsConnectApiException e) {
			  setErrorInConnection(true);
			  String strMsg = "( " + this.connectionFactoryProperties.getHostName() + ", " + this.connectionFactoryProperties.getPortNumber() + " ) " ;
			  OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0002E.msg(new Object[] {strMsg + e.getLocalizedMessage()}));
			  omConnectionException.setErrorNumber(e.getErrorNumber());
			  omConnectionException.setImsplexName(tmInteractionProperties.getImsDatastoreName());
			  throw omConnectionException;
		} catch (Exception e ){
		    throw new OmConnectionException(e);
		}

		//Reset the session information
		setSessionCreationTime();
		setSessionInUse(false);
		setErrorInConnection(false);
		setSessionAccessed(0);
		setSessionReturned(0);
	 }
	  
	/**
	 * Method returns if the connetion session is in use
	 * @return true if in use otherwise false
	 */
	public boolean isSessionInUse() {
		return this.isSessionInUse;
	}

	/**
	 * Set if connneciton is in use
	 * @param isSessionInUse
	 */
	public void setSessionInUse(boolean isSessionInUse) {
		this.isSessionInUse = isSessionInUse;
	}

	@Override
	public void releaseConnection(){
		this.setSessionInUse(false);
	}
	
	/**
	 * Method returns time the session was created (instantiated) 
	 * @return Current value of the most precise available system timer in nanoseconds
	 */
	public long getSessionCreationTime() {
		return this.sessionCreationTime;
	}

	
	private void setSessionCreationTime() {
		this.sessionCreationTime = System.nanoTime(); //sessionCreationTime;
	}

	
	/**
	 * Method returns the time the session has been alive (current time - creation time)
	 * @return Lifetime of session in nanoseconds
	 */
	public long getSessionLifetime() {
		return System.nanoTime() - this.sessionCreationTime;
	}

	/**
	 * Method returns when the session was last accessed as in the last command request
	 * @return Last accessed system nanotime
	 */
	public long getSessionAccessed() {
		return this.sessionAccessed;
	}

	private void setSessionAccessed(long sessionAccessed) {
		this.sessionAccessed = sessionAccessed;
	}

	/**
	 * Method returns when the session last executed a request
	 * @return Last response in system nanotime
	 */
	public long getSessionReturned() {
		return this.sessionReturned;
	}

	private void setSessionReturned(long sessionReturned) {
		this.sessionReturned = sessionReturned;
	}

	/**
   * Indicates if there is a connected socket associated with this connection.  
   * This method only indicates that the socket is connected to the TCP/IP network at 
   * the API (client application) end.  It does not give an indication if the partner end socket 
   * connection to IMS Connect is still connected.  In most cases, you will only know if a socket has 
   * been disconnected at the IMS Connect end if you get a connection failure during the receive 
   * call performed by the API during the <code>execute</code> method.
   * @return <code>true</code> if this connection is connected, otherwise <code>false</code>.
	 */
	public boolean isConnected(){
		return this.imsConnectConnection.isConnected();
	}
	
	/**
	 * Method gets if an error has occurred in the underlying connection
	 * @return True if there is an error otherwise false
	 */
	public boolean isErrorInConnection() {
		return isErrorInConnection;
	}

	/**
	 * Method sets if an error has occurred in the underlying connection
	 * @return True if there is an error otherwise false
	 */
	public void setErrorInConnection(boolean isErrorInConnection) {
		this.isErrorInConnection = isErrorInConnection;
	}

	/**
	 * Method sets if the session is temporary in that its not stored in the cache
	 * and meant for one time use to assist with connectivity overload
	 * @param isSessionTemp
	 */
	public void setSessionTemp(boolean isSessionTemp){
		this.isSessionTemp = isSessionTemp;
	}
	
	/**
	 * Method returns if the session is temporary in that its not stored in the cache
	 * and meant for one time use to assist with connectivity overload. Generally this 
	 * flag is used by the executor to know if it should disconnect the connection.
	 * @param isSessionTemp
	 */
	public boolean getSessionTemp(){
		return this.isSessionTemp;
	}
	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
	    String NEW_LINE = System.getProperty("line.separator");
	    String format = "| %1$-30s| %2$-30s| %3$-15s|" + NEW_LINE;
	    String line = "+--------------------------------------------------------------------------------+" + NEW_LINE;
	    result.append(line);
	    result.append(String.format(format, "Connection Session Statistics", "Time", "Measurement")); 
	    result.append(line);
		
	    result.append(String.format(format, "Session Id", getSessionId(),"Integer")); 
	    result.append(String.format(format, "Session Creation Time", getSessionCreationTime(),"Nano Time")); 
	    result.append(String.format(format, "Session In Use", isSessionInUse(),"Boolean")); 
	    result.append(String.format(format, "Session Accessed Time", getSessionAccessed(),"Nano Time")); 
	    result.append(String.format(format, "Session Returned Time", getSessionReturned(),"Nano Time")); 
	    result.append(String.format(format, "Session Life Time", (getSessionLifetime() / 1000000000.0 ),"Seconds")); 
	    result.append(String.format(format, "Session Execution Time", (getSessionReturned() - getSessionAccessed() ) / 1000000000.0,"Seconds")); 
	    result.append(String.format(format, "Session Errored", isErrorInConnection(),"Boolean")); 
	    result.append(String.format(format, "Session Temporary", getSessionTemp(),"Boolean")); 
	    result.append(line); 
	    result.append(connectionFactoryProperties);
	    result.append(tmInteractionProperties);

	    return result.toString();
	}
	
	/**
	 * Method will return a new instance of the connection using the current instance connection and tmInteration properties
	 * Basically this will make a new connection that is identical to the properties for the current connection. This is best
	 * used when a connection has an error or a temporary connection is needed to complete a request 
	 * @return New connection identical to the current properties for this connection
	 * @throws OmConnectionException 
	 */
	public IconOmConnection getNewInstance() throws OmConnectionException{
		IconOmConnection tempOmConnection = null;
		try{
		    ConnectionFactoryProperties tempConnectionFactoryProperties = connectionFactoryProperties.getNewInstance();
		    tempOmConnection = new IconOmConnection(tempConnectionFactoryProperties, tmInteractionProperties);
		    tempOmConnection.createConnection();
		}catch(OmConnectionException e){
			e.setEnvironmentId(getEnvironment());
			e.setImsplexName(getImsplex());
			e.setConnectionType(this.getConnectionType());
			throw e;
		}
		return tempOmConnection;
	}
	
	/**
	 * Disconnects and terminates a connection instance.
	 * @throws Exception
	 */
	public void disconnect() throws OmConnectionException {
    	if(logger.isDebugEnabled()){
    		logger.debug(">> disconnect()" );
    	}
		try {
			this.imsConnectConnection.disconnect();

        	if(logger.isDebugEnabled()){
        		logger.debug("Disconnected IconOmConnection:");
        		logger.debug(this.imsConnectConnection.toString());
        	}
		} catch (ImsConnectApiException e) {
			OmConnectionException omConnectionException = new OmConnectionException(e, e.getErrorNumber());
			omConnectionException.setConnectionType(this.getConnectionType());
			omConnectionException.setEnvironmentId(getEnvironment());
			omConnectionException.setImsplexName(getImsplex());
			throw omConnectionException;
		}
    	if(logger.isDebugEnabled()){
    		logger.debug("<< disconnect()" );
    	}
	}
	
	/**
	 * Ping will try to make various verifications to see if the endpoint and port are up. It will resolve the adddress then
	 * check if the address resolved is reachable while waiting up to 2 sec * number of tries. Then it will make
	 * up to numOfTries to make a socket connection. Sockets are from defined ports in the connnection.
	 * On success the socket is closed and returns true.
	 * @param numOfTries - int, how many times to amtempt to connect
	 * @return- boolean, true if a socket is accessable, otherwise false.
	 * 
	 */
	public boolean ping(int numOfTries){
		//2 second sleep times
		Long sleepTime = new Long(2000);
		
		//How long we wait to see if the address is reachable
		int waitTime = Integer.valueOf(sleepTime.toString());
		int waitTimeIsReachable = waitTime*numOfTries;
		
		Socket socket = null;
		InetAddress address = null;
		String hostAddress = null;
		
		//Default: We will try this 30 times (1 min).
		if(numOfTries < 0){
			numOfTries=30;
		}
		
	    //Resolve the local host address, should map to the /etc/hosts file
	    try {
	    	hostAddress = InetAddress.getByName(connectionFactoryProperties.getHostName()).getHostAddress();
			address = InetAddress.getByName(hostAddress);
		} catch (UnknownHostException e) {
			return false;
		}
	        
	    //Check to be certain the address is reachable, wait up till waitTimeIsReachable up to seconds before returning false and exiting
		try {
			if (!address.isReachable(waitTimeIsReachable)){
			    return false;
			}
		} catch (IOException e) {
			return false;
		}
	            
		// If Address is reachable, see if their is a port still open.
		for(int i = 0; i < numOfTries; i++){
	       	try {
	       		//Print this message every 5 tries (10 seconds)
	       		if((i%5) == 0){
	   	        	System.out.println(" Checking on server status.");
	   	        }
	       		
	       		try{
	       		//First wait sleeptime seconds to give the server a chance to reset
	       			Thread.sleep(sleepTime);
	       		}catch(InterruptedException e){} // Do nothing not worth handling
	   	        
	   	        //Try to create a socket connection, exceptions are ignored 
	   	        socket = new Socket(address,connectionFactoryProperties.getPortNumber());
	   			socket.close();
	   			return true;
			} catch (Exception e) {
				continue;
			}
		}
		
	  return false;
	}
	
	/**
	 * Returns the id for this OmConnection instance. It returns 
	 * the same hash code for the given object that can be used to track
	 * Instances of this object. Each instance will have a unique hash
	 * associated to it. 
	 * @return String, hash code for this object instance
	 */
	public String getSessionId(){
		return this.sessionId;
	}

	/**
	 * Set the IMS Connect connection RACF Password
	 * @param password
	 */
	public void setRacfPassword(char[] password){
		this.imsConnectConnection.setRacfPassword(password);
	}
	
	/**
	 * Set the IMS Connect connection RACF User
	 * @param racfUser
	 */
	public void setRacfUser(String racfUser){
		imsConnectConnection.setRacfUser(racfUser);
	}
	
	/**
	 * Set the IMS Connect connection RACF Group
	 * @param racfGroup
	 */
	public void setRacfGroup(String racfGroup){
		imsConnectConnection.setRacfGroup(racfGroup);
	}
	
	/**
	 * Get the IMS Connect connection RACF Host Name
	 * @return
	 */
	public String getHostName(){
		return this.connectionFactoryProperties.getHostName();
	}
	
	public int getPort(){
		return this.connectionFactoryProperties.getPortNumber();
	}

	
	//TODO: move this out somplace, its duplicate code with the threadHelper
    public String getCallingMethodName() {
        return getCurrentMethodNameFromThreadStack(1);
    }

    /**
     * 
     * @param stackLevel
     * @return
     */
    private String getCurrentMethodNameFromThreadStack(int stackPosition) {
        /*
         * Position:
         *  0 will dump the threads
         *  1 will get the stacktrace
         *  2 will get this current method "getCurrentMethodNameFromThread"
         *  3 will get the method calling this method
         *  4 will get method calling the calling method
         */
        int pos = 4;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[pos + stackPosition];

       // String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();

        //return className + "." + methodName;
        return methodName;
    }
}

