/**
 *  Copyright IBM Corporation 2018, 2019
 */
package icon.helpers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.ims.connect.ApiProperties;
import com.ibm.ims.connect.Connection;
import com.ibm.ims.connect.ConnectionFactory;
import com.ibm.ims.connect.ImsConnectApiException;
import com.ibm.ims.connect.InputMessage;
import com.ibm.ims.connect.OutputMessage;
import com.ibm.ims.connect.TmInteraction;

//import utils.TraceUtil;


/**
 * Common connection instance to help set up and connect to IMS Connect with and without SSL. At the common
 * level this connection class provides members to return a TYPE-2 TM Interaction for use with Type-2 command
 * execution.
 * @author ddimatos
 *
 */
public class ImsConnectConnection implements ApiProperties{  //Note: This might be better suited to be an abstract class

	final static Logger logger = LoggerFactory.getLogger(ImsConnectConnection.class);

	/** Factory for use with communicating with IMS Connect */
	private ConnectionFactory connectionFactory = null;
	
	/** Private Connection to communicate with IMS */
	private Connection connection = null;
	
	/** Private TmInteraction properties to be used by this instance */
	private TmInteractionProperties tmInteractionProperties = null;
	
	private ConnectionFactoryProperties connectionFactoryProperties = null;
	
	/**
	 * Initialize the IMS Connection with the properties arguments. 
	 */
	public ImsConnectConnection(ConnectionFactoryProperties connectionFactoryProperties,TmInteractionProperties tmInteractionProperties) throws ImsConnectApiException, SocketException{
		java.util.logging.Logger.getLogger("com.ibm.ims.connect");
		
		//try {
        //    java.util.logging.Logger loggerz;
        //    ApiLoggingConfiguration apiLoggingConfig = new ApiLoggingConfiguration();
        //    loggerz = apiLoggingConfig.configureApiLogging("/home/ddimatos/APILogsType2CommandTestTrace.log", ApiProperties.TRACE_LEVEL_INTERNAL);
        //} catch (ImsConnectApiException e) {
        //    e.printStackTrace();
        //}
		this.connectionFactoryProperties = connectionFactoryProperties;
		this.tmInteractionProperties = tmInteractionProperties;
		// FOR TYPE 1 ======> this.tmInteractionProperties.setInteractionTimeout(ApiProperties.INTERACTION_TYPE_DESC_SENDRECV);
		//Set up (load) the connection factory with the connectionFactoryProperties
		loadConnectionFactoryAttributesFromObject(connectionFactoryProperties);
		
		//Set the global connection
		this.connection = connectionFactory.getConnection();
		
		//Connect the connection (opens socket)
		this.connection.connect();	
	}

	/**
	 * Method will create a new connection factory and load it with the configured connectionFactoryAttributes
	 * @param connectionFactoryAttributes
	 * @return
	 * @throws ImsConnectApiException
	 */
	private void loadConnectionFactoryAttributesFromObject(ConnectionFactoryProperties connectionFactoryProperties) throws ImsConnectApiException{
		//Set up the connection factory with the connectionFactoryAttributes
		connectionFactory = new ConnectionFactory();
		
		//Create new inputstream because they can only be used once from a prior connection , don't want to chance it. 
		InputStream keystoreInputStream = new ByteArrayInputStream(connectionFactoryProperties.getKeystoreBytes());
        InputStream trustStoreInputStream = new ByteArrayInputStream(connectionFactoryProperties.getTruststoreBytes());
        
		//Load all the connection factory properties
		connectionFactory.setHostName(connectionFactoryProperties.getHostName());                 
		connectionFactory.setPortNumber(connectionFactoryProperties.getPortNumber()); 
		connectionFactory.setSocketType(connectionFactoryProperties.getSocketType()); /** Default is persistent **/
		connectionFactory.setClientId(connectionFactoryProperties.getClientId());
		connectionFactory.setSslEncryptionType(connectionFactoryProperties.getSslEncryptionType());
		connectionFactory.setSslKeystoreInputStream(keystoreInputStream);
		connectionFactory.setSslKeystoreUrl(connectionFactoryProperties.getSslKeystoreUrl());
		connectionFactory.setSslKeystoreName(connectionFactoryProperties.getSslKeystoreName());
		connectionFactory.setSslKeystorePassword(connectionFactoryProperties.getSslKeystorePassword());
		connectionFactory.setSslTruststoreInputStream(trustStoreInputStream);
		connectionFactory.setSslTruststoreUrl(connectionFactoryProperties.getSslTruststoreUrl());
		connectionFactory.setSslTruststoreName(connectionFactoryProperties.getSslTruststoreName());
		connectionFactory.setSslTruststorePassword(connectionFactoryProperties.getSslTruststorePassword());
		connectionFactory.setUseSslConnection(connectionFactoryProperties.isUseSslConnection());
		connectionFactory.setInteractionTimeout(connectionFactoryProperties.getInteractionTimeout());
		connectionFactory.setSocketConnectTimeout(connectionFactoryProperties.getSocketConnectTimeout());
	}
	
	/**
	 * Method executes a command and returns a {@link OutputMessage} containing the payload and return connection return codes
	 * @param command represented by a String
	 * @return Populated {@link OutputMessage}
	 * @throws ImsConnectApiException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public OutputMessage execute(String command) throws ImsConnectApiException, UnsupportedEncodingException, Exception{
		
		logger.debug("Execute Command {}", command);
		//interaction properties for use with communicating with IMS and execution of a Type-2 Command
		TmInteraction tmInteraction = null;
		
		// Contains the data sent to IMS Connect
		InputMessage inputMessage = null; 
		
		//if its a type2 do , else do.. Note: We need to figure out how to hadnle the datastoreName , for type 1 its the IMS , 
		//for type2 its IMSPlex, we don't have the IMS info from the user so lets see if we can do the type2 path
		//if(command.startsWith("/")){
		//	this.tmInteractionProperties.setImsDatastoreName("IMS1");
		//	this.tmInteractionProperties.setImsConnectUserMessageExitIdentifier(IMS_CONNECT_USER_MESSAGE_EXIT_IDENTIFIER_FOR_HWSSMPL1);
		//	this.tmInteractionProperties.setInteractionTypeDescription(INTERACTION_TYPE_DESC_SENDRECV);
		//	this.tmInteractionProperties.setTrancode("");
		//}else{
			this.tmInteractionProperties.setInteractionTypeDescription(INTERACTION_TYPE_DESC_TYPE2_COMMAND);
		//}
		
		tmInteraction = connection.createInteraction(this.tmInteractionProperties);
		
		// get InputMessage instance from myTMInteraction
		inputMessage = tmInteraction.getInputMessage();
        
		//Populate InputMessage object with input byte array
        byte[][] inData2DByteArray = new byte[1][];
        inData2DByteArray[0] = new String(command).getBytes(tmInteraction.getImsConnectCodepage());
        //TraceUtil.dumpBytesInHex(inData2DByteArray[0]);
        inputMessage.setInputMessageData(inData2DByteArray);
        //defect 52031 SQH avoid duplicate client Id error
        tmInteraction.setCancelClientId(false);
        tmInteraction.setGenerateClientIdWhenDuplicate(true);
        tmInteraction.execute();
        //connection.disconnect(); <-- Going to keep them alive for now since I have the Cache started.
		
		OutputMessage outputMessage = tmInteraction.getOutputMessage();
		
		//System.out.println("MSG RESPONSE PAYLOAD IS: " + outputMessage.getDataAsString());
		return outputMessage;
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
		return this.connection.isConnected();
	}
	
	/**
	 * Disconnects and terminates a connection instance.
	 * @throws ImsConnectApiException
	 */
	public void disconnect() throws ImsConnectApiException{
		this.connection.disconnect();
	}
	
	/**
	 * Set the RACF User for this connection instance
	 * @param racfUser
	 */
	public void setRacfUser(String racfUser){
		this.tmInteractionProperties.setRacfUserId(racfUser);
	}
	
	/**
	 * Set the RACF Password for this connection instance
	 * @param racfPassword
	 */
	public void setRacfPassword(char[] racfPassword){
		this.tmInteractionProperties.setRacfPassword(String.valueOf(racfPassword));
	}
	
	/**
	 * Set the RACF Group for this instance
	 * @param racfGroup
	 */
	public void setRacfGroup(String racfGroup){
		this.tmInteractionProperties.setRacfGroupName(racfGroup);
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append(this.connectionFactoryProperties.toString());
		result.append(this.tmInteractionProperties.toString());
		return result.toString();
	}
}
