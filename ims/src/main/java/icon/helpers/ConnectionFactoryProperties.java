/**
 *  Copyright IBM Corporation 2018, 2019
 */

package icon.helpers;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import com.ibm.ims.connect.ApiProperties;
import com.ibm.ims.connect.ConnectionFactory;


/**
 * Class is used to configure the IMS Connect API Connection Factory. Defaults are
 * in place according to the {@link ConnectionFactory} and {@link ApiProperties}.
 * 
 * @author ddimatos
 *
 */
public class ConnectionFactoryProperties implements ApiProperties {
	public static final String PRODUCT_PREFIX = "IQE";
	private static final char[] CHARSET = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
											'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
											'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
											'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	private final static String DEFAULT_SSL_KEYSTORE_NAME = System.getProperty("jre.home") + "lib" + System.getProperty("path.separator") + "security" + System.getProperty("path.separator") +"cacerts";
	private final static String DEFAULT_SSL_TRUSTSTORE_NAME = DEFAULT_SSL_KEYSTORE_NAME;
	
	private String hostName 					= DEFAULT_HOSTNAME;
	private int portNumber 					= DEFAULT_PORTNUMBER;
	private byte socketType 					= DEFAULT_SOCKET_TYPE;
	private String clientId 					= DEFAULT_CLIENTID;
	private byte sslEncryptionType 			= SSL_ENCRYPTIONTYPE_STRONG; //DEFAULT_SSL_ENCRYPTIONTYPE;
	private InputStream sslKeystoreInputStream 	= DEFAULT_SSL_KEYSTORE_INPUT_STREAM;
	private URL sslKeystoreUrl 					= DEFAULT_SSL_KEYSTORE_URL;
	private String sslKeystoreName 				= DEFAULT_SSL_KEYSTORE_NAME;
	private String sslKeystorePassword			= DEFAULT_SSL_KEYSTORE_PASSWORD;
	private InputStream sslTruststoreInputStream = DEFAULT_SSL_TRUSTSTORE_INPUT_STREAM;
	private URL sslTruststoreUrl 				= DEFAULT_SSL_TRUSTSTORE_URL;
	private String sslTruststoreName 			= DEFAULT_SSL_TRUSTSTORE_NAME;
	private String sslTruststorePassword		= DEFAULT_SSL_TRUSTSTORE_PASSWORD;
	private boolean useSslConnection 			= DEFAULT_USE_SSL_CONNECTION;
	private int interactionTimeout 			= TIMEOUT_5_MINUTES;
	private int socketConnectTimeout 			= TIMEOUT_2_MINUTES; //DEFAULT_SOCKET_CONNECT_TIMEOUT;
	
	private byte[] keystoreBytes               =  new byte[] {};
	private byte[] truststoreBytes             =  new byte[] {};
	
	/**
	 * Configures the attributes with the required interaction properties.
	 * @param hostName
	 * @param portNumber
	 * @param useSslConnection
	 */
	public ConnectionFactoryProperties(String hostName, int portNumber,boolean useSslConnection ) {
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.useSslConnection = useSslConnection;
		this.clientId = PRODUCT_PREFIX.concat(getRandomString(5, CHARSET));
		
	}
	
//	@Deprecated
//	public ConnectionFactoryProperties getNewInstance(){
//	    //What should we be doing here? Originally it was minimal to prevent others credentials from 
//	    //being abused/accessed, now its just the same user? If we return "this" we also return bad values? 
//		return new ConnectionFactoryProperties(this.hostName,this.portNumber,this.useSslConnection);
//	}
	
	private ConnectionFactoryProperties(String hostName, int portNumber,boolean useSslConnection,InputStream sslKeystoreInputStream, String sslKeystorePassword, InputStream sslTruststoreInputStream, String sslTruststorePassword ) {
	    this.hostName = hostName;
	    this.portNumber = portNumber;
	    this.useSslConnection = useSslConnection;
	    this.clientId = PRODUCT_PREFIX.concat(getRandomString(5, CHARSET));
	    this.sslKeystoreInputStream = sslKeystoreInputStream;
	    this.sslKeystorePassword = sslKeystorePassword;
	    this.sslTruststoreInputStream = sslTruststoreInputStream;
	    this.sslTruststorePassword = sslTruststorePassword;
	}
	   
	public ConnectionFactoryProperties getNewInstance(){
	    ConnectionFactoryProperties connectionFactoryProperties =  new ConnectionFactoryProperties(this.hostName,this.portNumber,this.useSslConnection,this.sslKeystoreInputStream, this.sslKeystorePassword, this.sslTruststoreInputStream,this.sslTruststorePassword);
	    connectionFactoryProperties.setKeystoreBytes(this.keystoreBytes);
	    connectionFactoryProperties.setTruststoreBytes(this.truststoreBytes);
	    return connectionFactoryProperties;
	}
	
	
	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	}
	
	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	/**
	 * @return the socketType
	 */
	public byte getSocketType() {
		return socketType;
	}
	
	/**
	 * @param socketType the socketType to set
	 */
	public void setSocketType(byte socketType) {
		this.socketType = socketType;
	}
	
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return this.clientId;
	}
	
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	/**
	 * @return the sslEncryptionType
	 */
	public byte getSslEncryptionType() {
		return sslEncryptionType;
	}
	/**
	 * @param sslEncryptionType the sslEncryptionType to set
	 */
	public void setSslEncryptionType(byte sslEncryptionType) {
		this.sslEncryptionType = sslEncryptionType;
	}
	
	/**
	 * @return the sslKeystoreInputStream
	 */
	public InputStream getSslKeystoreInputStream() {
		return sslKeystoreInputStream;
	}
	
	/**
	 * @param sslKeystoreInputStream the sslKeystoreInputStream to set
	 */
	public void setSslKeystoreInputStream(InputStream sslKeystoreInputStream) {
		this.sslKeystoreInputStream = sslKeystoreInputStream;
	}
	
	/**
	 * @return the sslKeystoreUrl
	 */
	public URL getSslKeystoreUrl() {
		return sslKeystoreUrl;
	}
	
	/**
	 * @param sslKeystoreUrl the sslKeystoreUrl to set
	 */
	public void setSslKeystoreUrl(URL sslKeystoreUrl) {
		this.sslKeystoreUrl = sslKeystoreUrl;
	}
	
	/**
	 * @return the sslKeystoreName
	 */
	public String getSslKeystoreName() {
		return sslKeystoreName;
	}
	
	/**
	 * @param sslKeystoreName the sslKeystoreName to set
	 */
	public void setSslKeystoreName(String sslKeystoreName) {
		this.sslKeystoreName = sslKeystoreName;
	}
	
	/**
	 * @return the sslKeystorePassword
	 */
	public String getSslKeystorePassword() {
		return sslKeystorePassword;
	}
	
	/**
	 * @param sslKeystorePassword the sslKeystorePassword to set
	 */
	public void setSslKeystorePassword(String sslKeystorePassword) {
		this.sslKeystorePassword = sslKeystorePassword;
	}
	
	/**
	 * @return the sslTruststoreInputStream
	 */
	public InputStream getSslTruststoreInputStream() {
		return sslTruststoreInputStream;
	}
	
	/**
	 * @param sslTruststoreInputStream the sslTruststoreInputStream to set
	 */
	public void setSslTruststoreInputStream(InputStream sslTruststoreInputStream) {
		this.sslTruststoreInputStream = sslTruststoreInputStream;
	}
	
	/**
	 * @return the sslTruststoreUrl
	 */
	public URL getSslTruststoreUrl() {
		return sslTruststoreUrl;
	}
	
	/**
	 * @param sslTruststoreUrl the sslTruststoreUrl to set
	 */
	public void setSslTruststoreUrl(URL sslTruststoreUrl) {
		this.sslTruststoreUrl = sslTruststoreUrl;
	}
	
	/**
	 * @return the sslTruststoreName
	 */
	public String getSslTruststoreName() {
		return sslTruststoreName;
	}
	
	/**
	 * @param sslTruststoreName the sslTruststoreName to set
	 */
	public void setSslTruststoreName(String sslTruststoreName) {
		this.sslTruststoreName = sslTruststoreName;
	}
	
	/**
	 * @return the sslTruststorePassword
	 */
	public String getSslTruststorePassword() {
		return sslTruststorePassword;
	}
	
	/**
	 * @param sslTruststorePassword the sslTruststorePassword to set
	 */
	public void setSslTruststorePassword(String sslTruststorePassword) {
		this.sslTruststorePassword = sslTruststorePassword;
	}
	
	/**
	 * @return the useSslConnection
	 */
	public boolean isUseSslConnection() {
		return useSslConnection;
	}
	
	/**
	 * @param useSslConnection the useSslConnection to set
	 */
	public void setUseSslConnection(boolean useSslConnection) {
		this.useSslConnection = useSslConnection;
	}
	
	/**
	 * @return the interactionTimeout
	 */
	public int getInteractionTimeout() {
		return interactionTimeout;
	}
	
	/**
	 * @param interactionTimeout the interactionTimeout to set
	 */
	public void setInteractionTimeout(int interactionTimeout) {
		this.interactionTimeout = interactionTimeout;
	}
	
	/**
	 * @return the socketConnectTimeout
	 */
	public int getSocketConnectTimeout() {
		return socketConnectTimeout;
	}
	
    
    public byte[] getKeystoreBytes(){
        return this.keystoreBytes;
    }
    
    public byte[] getTruststoreBytes(){
        return this.truststoreBytes;
    }
    
    public void setKeystoreBytes(byte[] keystoreBytes){
        this.keystoreBytes = keystoreBytes;
    }
    
    public void setTruststoreBytes(byte[] truststoreBytes){
        this.truststoreBytes = truststoreBytes;
    }
    
//	/**
//	 * @param socketConnectTimeout the socketConnectTimeout to set
//	 */
//	public void setSocketConnectTimeout(int socketConnectTimeout) {
//		this.socketConnectTimeout = socketConnectTimeout;
//	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
	    String NEW_LINE = System.getProperty("line.separator");
	    String format = "| %1$-30s| %2$-30s| %3$-15s|" + NEW_LINE;
	    String line = "+--------------------------------------------------------------------------------+" + NEW_LINE;

	    result.append(line);
	    result.append(String.format(format, "ConnectionFactoryProperties", "Value", "Measurement")); 
	    result.append(line);
		
	    if(useSslConnection){
	    	result.append(String.format(format, "Host Name", 		getHostName()							,"String")); 
	 	    result.append(String.format(format, "Port Number", 		Integer.toString(getPortNumber())		,"Integer")); 
	 	    result.append(String.format(format, "Socket Type",		Byte.toString(getSocketType())			,"Byte")); 
	 	    result.append(String.format(format, "Client ID", 		getClientId()							,"String")); 
	 	    result.append(String.format(format, "Encryption Type", 	Byte.toString(getSslEncryptionType())	,"Byte")); 
	 	    //result.append(String.format(format, "Key Store URL", 	getSslKeystoreUrl().toString()			,"URL")); 
	 	    result.append(String.format(format, "Key Store Input Stream", getSslKeystoreInputStream() == null? "NULL":"IMPORTED","Bytes")); 
	 	    //result.append(String.format(format, "Trust Store URL",  getSslTruststoreUrl().toString()		,"URL")); 
	 	    result.append(String.format(format, "Trust Store Input Stream", getSslTruststoreInputStream() == null? "NULL":"IMPORTED","Bytes")); 
	 	    result.append(String.format(format, "SSL Enabled", 		Boolean.toString(useSslConnection)		,"Boolean")); 
	 	    result.append(String.format(format, "Interaction Time out", 	Integer.toString(interactionTimeout),"Integer")); 
	 	    result.append(String.format(format, "Socket Connect Timeout", 	Integer.toString(socketConnectTimeout),"Integer")); 
	 	    // Keep Disabled ==> result.append(String.format(format, "Keystore Pass",    getSslTruststorePassword(),"Integer")); 
	 	    // Keep Disabled ==> result.append(String.format(format, "Keystore Pass",    getSslKeystorePassword(),"Integer")); 
	    }else{
	    	result.append(String.format(format, "Host Name", 		getHostName()							,"String")); 
	 	    result.append(String.format(format, "Port Number", 		Integer.toString(getPortNumber())		,"Integer")); 
	 	    result.append(String.format(format, "Socket Type",		Byte.toString(getSocketType())			,"Byte")); 
	 	    result.append(String.format(format, "Client ID", 		getClientId()							,"String")); 
	 	    result.append(String.format(format, "Interaction Time out", 	Integer.toString(interactionTimeout),"Integer")); 
	 	    result.append(String.format(format, "Socket Connect Timeout", 	Integer.toString(socketConnectTimeout),"Integer")); 
	    }
	    result.append(line); 
	    
	    return result.toString();
	}
	
//	public static String byteArray2Hex(byte[] bytes) {
//	    StringBuffer sb = new StringBuffer(bytes.length * 2);
//	    for(final byte b : bytes) {
//	        sb.append(hex[(b & 0xF0) >> 4]);
//	        sb.append(hex[b & 0x0F]);
//	    }
//	    return sb.toString();
//	}
	
	public String getRandomString(int length, char[] characterSet) {
	    StringBuilder sb = new StringBuilder();

	    for (int i = 0; i < length; i++) {
	    	//Don't trust the randomness here but it seems to be ok, so going to step it up a bit.
	        //int index = new Random().nextInt(characterSet.length);
	    	 int index = new Random(Double.doubleToLongBits(Math.random())+System.nanoTime()).nextInt(characterSet.length);
	        sb.append(characterSet[index]);
	    }
	    return sb.toString();
	}
}
