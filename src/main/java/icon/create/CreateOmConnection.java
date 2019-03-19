/**
 *  Copyright IBM Corporation 2018, 2019
 */
package icon.create;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.ims.connect.ApiProperties;

import icon.helpers.ConnectionFactoryProperties;
import icon.helpers.TmInteractionProperties;
import om.connection.IconOmConnection;
import om.connection.OMConnection;
import om.exception.OmConnectionException;
import om.services.Om;

public final class CreateOmConnection {
	final static Logger logger = LoggerFactory.getLogger(CreateOmConnection.class);
	
	private int sysplexId = -1;
	private String hostName;
	private int port; 
	private String imsplex;
	private Boolean isSsl 				= false;
	private byte[] keystore			= null;
	private String keystorePassword		= null;
	private byte[] truststore			= null;
	private String truststorePassword 	= null;
	private String racfUserId;
	private String racfPassword;
	private final String racfGroupName	= "";		//Note Group name is not supported and the underlying implementation will set it to an empty string.
	private boolean isRacf 			= false;	//Var used to help manage the use of racf
	
	@SuppressWarnings("unused")
	private CreateOmConnection() {/* Keep me private */}
	
	/**
	 * <p>
	 * Minimum configuration to create a IMS Connect connection to OM. This is a minimum configuration
	 * which does not use SSL or RACF. For RACF or SSL, configure the required by using methods: 
	 * {@link #enableRACF(String, String)} and/or {@link #enableSSl(byte[], String, byte[], String)}
	 * 
	 * @param hostName
	 * @param port
	 * @param imsplex
	 * </p>
	 */
	public CreateOmConnection(int sysplexId, String hostName, int port, String imsplex) {
		this.hostName = hostName;
		this.port = port;
		this.imsplex = imsplex;
		this.sysplexId = sysplexId;
	}
	
	/**
	 * Enalbe SSL (AT-TLS) support for this IMS Connection
	 * @param keystore
	 * @param keystorePassword
	 * @param truststore
	 * @param truststorePassword
	 */
	public void enableSSl(byte[] keystore, String keystorePassword, byte[] truststore, String truststorePassword){
		this.isSsl = true;
		this.keystore = keystore;
		this.keystorePassword = keystorePassword;
		this.truststore = truststore;
		this.truststorePassword = truststorePassword;
	}
	
	/**
	 * Enable RACF for this connection with IMS Connect
	 * @param racfUserId
	 * @param racfPassword
	 */
	public void enableRACF(String racfUserId,String racfPassword){
		this.isRacf = true;
		this.racfUserId 	= racfUserId;
		this.racfPassword 	= racfPassword;
	}
	
	/**
	 * <pre>
	 * Returns a new instance of a IMS Connection that can communicate with OM. Connection returned will have
	 * been tested that it is connected to IMS Connect. Connection should be tested by the caller if OM is 
	 * Active and Ready. This connection is used in combination with {@link Om}, testing of OM is Active
	 * and Ready can use the method {@link Om#noOpCommand()}. If SSL or RACF is configured for this connection
	 * and either are incorrect because of the values or the host configuration an {@link OmConnectionException} 
	 * will be returned with a message and additional information.
	 * @return an {@link IconOmConnection} that can be used to communicate with OM
	 * @throws OmConnectionException
	 * </pre>
	 */
    public OMConnection getIconOmConnection() throws OmConnectionException{
        IconOmConnection iconOmConnection = null;
        
        // Create the properties needed for a connection from user input
        ConnectionFactoryProperties connectionFactoryProperties = new ConnectionFactoryProperties(this.hostName, this.port, this.isSsl);
        
        //Connect APi follows this order: sslkeystorestream -> sslkeystoreurl -> sslkeystoreName
        if(this.isSsl){
            connectionFactoryProperties.setKeystoreBytes(this.keystore);
            connectionFactoryProperties.setTruststoreBytes(this.truststore);
            
            InputStream keystoreInputStream = new ByteArrayInputStream(this.keystore);
            InputStream truststoreInputStream = new ByteArrayInputStream(this.truststore);
            
            connectionFactoryProperties.setSslEncryptionType(ApiProperties.SSL_ENCRYPTIONTYPE_STRONG);
            connectionFactoryProperties.setSslKeystoreInputStream(keystoreInputStream);
            connectionFactoryProperties.setSslKeystorePassword(this.keystorePassword);
            connectionFactoryProperties.setSslTruststoreInputStream(truststoreInputStream);
            connectionFactoryProperties.setSslTruststorePassword(this.truststorePassword);
        }
        
        TmInteractionProperties tmInteractionProperties = null;
        
        if(this.isRacf){
        	tmInteractionProperties = new TmInteractionProperties(this.imsplex ,this.racfUserId, this.racfGroupName, this.racfPassword);
        }else{
        	tmInteractionProperties = new TmInteractionProperties(this.imsplex ,null, this.racfGroupName, null);
        }
        
        //Create an OmConnection to establish the connection and be placed in the cacheConnection
        iconOmConnection = new IconOmConnection(connectionFactoryProperties, tmInteractionProperties);
        iconOmConnection.createConnection();
        
        if(!iconOmConnection.isConnected()){
            throw new OmConnectionException("Unable to connect to IMS Connect");
        }
        
        if(logger.isDebugEnabled()){
            logger.debug(iconOmConnection.toString());
        }
        
        iconOmConnection.setEnvironment(this.sysplexId);
        iconOmConnection.setImsplex(this.imsplex);
        return iconOmConnection;
    }
}
