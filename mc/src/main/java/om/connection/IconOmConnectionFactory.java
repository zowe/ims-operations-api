package om.connection;
/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.ibm.ims.connect.ApiProperties;

import icon.helpers.ConnectionFactoryProperties;
import icon.helpers.IconOmConnectionConstants;
import icon.helpers.MCInteraction;
import icon.helpers.TmInteractionProperties;
import om.exception.OmConnectionException;
import om.exception.OmDatastoreException;
import om.exception.OmException;
import om.message.IQEO;
import om.services.Om;

/**
 * Class will provide access to a number of IconOmConnection(s) stored in a cache based on the connection key (IMSPlex)
 * @author ddimatos
 */
public class IconOmConnectionFactory implements OMConnectionFactory {
	private final static Logger logger = LoggerFactory.getLogger(IconOmConnectionFactory.class); //Logger
	private String myConnectionType = "";                                                        //Our EA OM ICON FACTORY Type (name) "FACTORY_IMS_CONNECT"
	private long threadId = 0;																	 //Help identify the thread
	private String sessionId = "";																 //Hash code for the given object instance
    
    /**
	 * Factory instantiation for access to IMS Connect Factories
	 */
	public IconOmConnectionFactory() {
		this.sessionId = UUID.randomUUID().toString();//Integer.toHexString(System.identityHashCode(this));
		this.threadId = Thread.currentThread().getId();
	}
	
	public void setConnectionType(String type) {
	   this.myConnectionType = type;
	}
	   
	public String getConnectionType() {
		return this.myConnectionType;
	}


 
   
	
	/**
	 * Create an ICON Connection with the underlying IMS Connect API implementation that is connected.
	 * Returns null if the connection created is not connected to a IMS Connect Port
	 * @param icon
	 * @param environmentId
	 * @param imsplexName
	 * @return
	 * @throws OmConnectionException
	 */
	public IconOmConnection createIconOmConnectionFromData(MCInteraction mcSpec) throws OmConnectionException {
        //ICON Configuration Properties
        ConnectionFactoryProperties connectionFactoryProperties = new ConnectionFactoryProperties(mcSpec.getHostname(), Integer.valueOf(mcSpec.getPort()), Boolean.valueOf(mcSpec.isUsesSsl()));
        
        if(mcSpec.isUsesSsl()){
            //Set the bytes in the connectonFactoryProperties for use when this connection must be cloned
            connectionFactoryProperties.setKeystoreBytes(mcSpec.getKeystore());
            connectionFactoryProperties.setTruststoreBytes(mcSpec.getTruststore());
            
            //Connection Factory Properties
            connectionFactoryProperties.setSslKeystoreName(null);
            connectionFactoryProperties.setSslKeystorePassword(mcSpec.getKeystorePass());
            connectionFactoryProperties.setSslTruststoreName(null);
            connectionFactoryProperties.setSslTruststorePassword(mcSpec.getTruststorePass());
            connectionFactoryProperties.setSslEncryptionType(ApiProperties.SSL_ENCRYPTIONTYPE_STRONG);
            InputStream keystoreInputStream = new ByteArrayInputStream(mcSpec.getKeystore());
            InputStream truststoreInputStream = new ByteArrayInputStream(mcSpec.getTruststore());
            connectionFactoryProperties.setSslKeystoreInputStream(keystoreInputStream);
            connectionFactoryProperties.setSslTruststoreInputStream(truststoreInputStream);
        }
            
        //ICON TMinteraction properties
        TmInteractionProperties tmInteractionProperties = null;
        tmInteractionProperties = new TmInteractionProperties(mcSpec.getDatastoreName());
            
        //Create an OmConnection to establish the connection and be placed in the cacheConnection
        IconOmConnection iconOmConnection = new IconOmConnection(connectionFactoryProperties, tmInteractionProperties);
        iconOmConnection.setConnectionType(this.getConnectionType());
        iconOmConnection.createConnection();
        
        
        if(iconOmConnection.isConnected()){
            return iconOmConnection;
        }
            
        return null;
	}
	
	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
	    String NEW_LINE = System.getProperty("line.separator");
	    String format = "| %1$-25s| %2$-20s|" + NEW_LINE;
	    String line = "+--------------------------------------------------------------------------------+" + NEW_LINE;
	    
	    result.append(line);
	    result.append(String.format(format, "Object", this.getClass().getSimpleName() ));  
	    result.append(line);
	    result.append(String.format(format, "Thread ID: ", this.threadId)); 
	    result.append(String.format(format, "Object Hash ID: ",this.sessionId)); 
	    result.append(line); 
	    
	    return result.toString();
	}
}
