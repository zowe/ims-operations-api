package com.ibm.ims.ea.om.connection.icon;
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

import com.ibm.im.ac.datastore.DatastoreException;
import com.ibm.imsbase.datastore.ImsconnectBean;
import com.ibm.imsbase.datastore.ImsconnectStore;
import com.ibm.im.ac.datastore.RootDatastore;
import com.ibm.im.ac.datastore.SysplexBean;
import com.ibm.im.ac.services.binding.UserBinding;
import com.ibm.im.ac.user.UserInfo;
import com.ibm.im.ac.user.credentials.Credentials;
import com.ibm.im.ac.user.credentials.CredentialsException;
import com.ibm.im.ac.user.credentials.CredentialsManager;
import com.ibm.im.ac.user.credentials.UserPassCallback;
import com.ibm.im.ac.user.credentials.UserPassCredentials;
import com.ibm.ims.connect.ApiProperties;
import com.ibm.ims.ea.base.om.common.messages.IQEO;
import com.ibm.ims.ea.om.common.connection.factory.OMConnection;
import com.ibm.ims.ea.om.connection.factory.OMConnectionFactory;
import com.ibm.ims.ea.om.common.exception.OmConnectionException;
import com.ibm.ims.ea.om.common.exception.OmDatastoreException;
import com.ibm.ims.ea.om.common.exception.OmException;
import com.ibm.ims.ea.om.common.services.Om;
import com.ibm.ims.ea.om.connection.icon.helpers.ConnectionFactoryProperties;
import com.ibm.ims.ea.om.connection.icon.helpers.IconOmConnectionConstants;
import com.ibm.ims.ea.om.connection.icon.helpers.TmInteractionProperties;
import com.ibm.imsbase.datastore.ImsbaseDatastore;

/**
 * Class will provide access to a number of IconOmConnection(s) stored in a cache based on the connection key (IMSPlex)
 * @author ddimatos
 */
public class IconOmConnectionFactory implements OMConnectionFactory {
	private final static Logger logger = LoggerFactory.getLogger(IconOmConnectionFactory.class); //Logger
	private String myConnectionType = "";                                                        //Our EA OM ICON FACTORY Type (name) "FACTORY_IMS_CONNECT"
	private long threadId = 0;																	 //Help identify the thread
	private String sessionId = "";																 //Hash code for the given object instance
    private IconOmConnectionCache imsConnectionCache = null;
    
    /**
	 * Factory instantiation for access to IMS Connect Factories
	 */
	public IconOmConnectionFactory() {
		this.sessionId = UUID.randomUUID().toString();//Integer.toHexString(System.identityHashCode(this));
		this.threadId = Thread.currentThread().getId();
	}
	
	@Override
	public void setConnectionType(String type) {
	   this.myConnectionType = type;
	}
	   
	@Override
	public String getConnectionType() {
		return this.myConnectionType;
	}

	@SuppressWarnings("unused")
    private UserPassCredentials getUserInfo(HttpSession session, String address) throws OmException{
		// Pull the userInfo out of the session
		UserInfo userInfo = UserBinding.getUserInfo(session);
		
		// Lets check the users credentials and see if they have RACF configured for this connection and retun if so.
		CredentialsManager credentialsManager = userInfo.getCredentialsManager();
		UserPassCredentials userPass 	= null;
		Credentials credentials 		= null;
		
		try {
			credentials = credentialsManager.getCredentialsFor(address);
		} catch (CredentialsException e) {
			throw new OmException(e);
		}
			
			if (credentials != null) {
				if(logger.isDebugEnabled()) logger.debug("Saved Credentials were found for {}", address);
				
				// Make sure we have UserPassCredentials...we don't actually support anything else right now.
				if (credentials instanceof UserPassCredentials == false) throw new OmException("Invalid Credentials Type: " + credentials.getClass());
				
				userPass = (UserPassCredentials)credentials;
			}
			return userPass;
	}

 
 @Override  //NOTE: No need to be synchronized, each user has a pool, synch will lock other users.
 public OMConnection getConnection(int environment, String imsplex, UserInfo userInfo) throws OmConnectionException {
     UserPassCredentials userPassCredentials = null;
     IconOmConnection iconOmConnection = null;
     String connectionKey = environment +","+imsplex.trim(); //Set the connection key for class access
     Om om = null;
     
     if(logger.isDebugEnabled()){
    	 logger.debug(">> getConnection("+ environment +","+imsplex.trim()+")");
     }
     
     //Access the users connection pool instance
     this.imsConnectionCache = (IconOmConnectionCache)userInfo.getBinding(IconOmConnectionConstants.IMS_EA_OM_CONNECTION_POOL_BINDING);
   
     //If the pool is null, its the first time, create it and attach it to the user binding
     if(this.imsConnectionCache == null){
         this.imsConnectionCache = new IconOmConnectionCache();
         userInfo.bindObject(IconOmConnectionConstants.IMS_EA_OM_CONNECTION_POOL_BINDING, this.imsConnectionCache);
     }
     
     //Pull a connection from the cache that can serve the request
     iconOmConnection = this.imsConnectionCache.getCacheEntry(connectionKey);
     
     if(iconOmConnection != null){ //Connection was found in the cache
         
         if(userInfo != null){
             userPassCredentials = this.getUserPassCredentials(userInfo, iconOmConnection.getHostName());
         }
         
         //Because E4A allows a user to disconnect an ICON port it can leave an iconOmConnection in the cache in a disconnect state
         if(iconOmConnection.isConnected()){ 
             
             //Connections can be put in error mode if an exception occurs while communicating with ICON or OM
             if(!iconOmConnection.isErrorInConnection()){
                 
                 //Connection in Pool could be in use
                 if(!iconOmConnection.isSessionInUse()){
                     
                     iconOmConnection.setSessionInUse(true);
                     
                     //If there are no credentials could be a non-racf connection or user did not save credentials
                     if(userPassCredentials != null){
                         iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                         iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                         iconOmConnection.setRacfGroup("");
                     }
                     
                     if(logger.isDebugEnabled()){
                         logger.debug("Retrieved connection from cache \n" + this.toString() + this.imsConnectionCache.getCacheEntry(connectionKey));
                     }
                     
                 }else{  //connection is in use so create a temporary connection to be non-blocking and flag it as temperary to be disposed of afterwards
                         iconOmConnection = this.imsConnectionCache.getCacheEntry(connectionKey).getNewInstance();
                         iconOmConnection.setSessionInUse(true);
                         iconOmConnection.setSessionTemp(true);
                         
                         //If there are no credentials could be a non-racf connection or user did not save credentials
                         if(userPassCredentials != null){
                             iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                             iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                             iconOmConnection.setRacfGroup("");
                         }
                         
                         if(logger.isDebugEnabled()){
                             logger.debug("A temporary connection has been created "+this.toString());
                         }
                 }
             }else{  //Error in connection from prior use, try to recreate the same connection 
                 IconOmConnection erroredIconOmConnection = this.imsConnectionCache.getCacheEntry(connectionKey);
                 this.imsConnectionCache.removeCacheEntry(connectionKey);
                 erroredIconOmConnection.disconnect();
                     
                 iconOmConnection = null;
                 iconOmConnection = erroredIconOmConnection.getNewInstance();
                 
                 //It could be that the connection being cloned has been disconnected so call createIconOmConnection to find a new good connection
                 if(!iconOmConnection.isConnected()){
                     iconOmConnection = createIconOmConnection(environment, imsplex.trim());
                 }
                     
                 if(iconOmConnection != null){
                     iconOmConnection.setSessionInUse(true);
                         
                     //If there are no credentials could be a non-racf connection or user did not save credentials
                     if(userPassCredentials != null){
                         iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                         iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                         iconOmConnection.setRacfGroup("");
                     }
                     
                     //Add the reset connection into the cache
                     this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                         
                     if(logger.isDebugEnabled()){
                         logger.debug("A connection was cloned to correct an errored connection " + this.toString());
                     }
                         
                 }else{
                     OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0002E.msg(new Object[] {"Unable to crete a connection, it could be that the IMS Connect ports are closed."}));
                     omConnectionException.setImsplexName(imsplex);
                     omConnectionException.setEnvironmentId(environment);
                     throw omConnectionException;
                 }
             }
         }else{ //A connection exists in the cache but is disconnected, could mean the IMS Connect port was disconnected
             this.imsConnectionCache.removeCacheEntry(connectionKey);
             
             //Create a new iconOmConnection
             iconOmConnection = createIconOmConnection(environment, imsplex.trim());
             
             if(iconOmConnection != null){
                 iconOmConnection.setSessionInUse(true);
                 
                 //If there are no credentials could be a non-racf connection or user did not save credentials
                 if(userPassCredentials != null){
                     iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                     iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                     iconOmConnection.setRacfGroup("");
                 }
                 
                 //Add the reset connection into the cache
                 this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                     
                 if(logger.isDebugEnabled()){
                     logger.debug("A new connection has been created to replace a disconnected connection and cached " + this.toString());
                 }
             }else{ //Connection is null, must be the host is non-existent, throw OmConnectionException
                 OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0001E.msg(new Object[] {"Connection request returned null"}));
                 omConnectionException.setConnectionType(this.myConnectionType);
                 omConnectionException.setImsplexName(imsplex);
                 omConnectionException.setEnvironmentId(environment);
                 throw omConnectionException;
             }
         }

     }else{ //Connection is not in the cache lets try to create it and then return it
             iconOmConnection = createIconOmConnection(environment, imsplex.trim());
             
             if(iconOmConnection != null){
                 
                 iconOmConnection.setSessionInUse(true);
                 
                 if(userInfo != null){
                     userPassCredentials = this.getUserPassCredentials(userInfo, iconOmConnection.getHostName());
                 }

                 //If there are no credentials could be a non-racf connection or user did not save credentials
                 if(userPassCredentials != null){
                     iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                     iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                     iconOmConnection.setRacfGroup("");
                 }
                 
                 //Add the reset connection into the cache
                 this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                     
                 if(logger.isDebugEnabled()){
                     logger.debug("A new connection has been created and cached " + this.toString());
                 }
                 
             }else{ //Connection is null, must be the host is non-existent, throw OmConnectionException
            	 OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0001E.msg(new Object[] {"Connection request returned null"}));
            	 omConnectionException.setConnectionType(this.myConnectionType);
            	 omConnectionException.setImsplexName(imsplex);
            	 omConnectionException.setEnvironmentId(environment);
            	 throw omConnectionException;
             }
     }
     
     //Connection has been created at this point, try to test if OM is ready and if RACF credentials are needed
     try{
         om = new Om(iconOmConnection);
         
         //Test the connection with a no-op so that ICON can evaluate if RACF is required
         om.noOpCommand();
         
     } catch (OmConnectionException e) {
         
         try {
             if(e.getErrorNumber().contains("HWS0043E")){ //HWS0043E RACF AUTH ERROR
                 //Mark connection error, we need to create new connections on error because ICON disconnects the socket.
                 iconOmConnection.setErrorInConnection(true);
                 iconOmConnection.setSessionInUse(false);
                 iconOmConnection.disconnect();

                 //Remove the invalid connection from the pool so that a clone is not attempted from a connection that is errored and disconnected
                 this.imsConnectionCache.removeCacheEntry(connectionKey);
                 
                 // Prompt user for credentials and create our callback which needs the session so we can track it back to which push servlet to call out to
                 UserPassCallback userPassCallback = userInfo.getUserPassCallback();
                 
                 //Get the sysplex shortname we are going to send to the UI
                 SysplexBean sysplexBean = RootDatastore.getInstance().getSysplexStore().getSysplexByID(environment);
                 String environmentName = sysplexBean.getShortName();
                 
                 //userPass = callback.getUserPassCredentials(null, environmentName, iconOmConnection.getHostName(), iconOmConnection.getPort(), null);
                 UserPassCredentials userPass = userPassCallback.getUserPassCredentials(null, environmentName, iconOmConnection.getHostName(), iconOmConnection.getPort(), null);
                 
                 iconOmConnection = createIconOmConnection(environment, imsplex.trim());
                 
                 if(userPass != null){
                     iconOmConnection.setRacfPassword(userPass.getPassword());
                     iconOmConnection.setRacfUser(userPass.getUsername());
                     iconOmConnection.setRacfGroup("");
                 }

                 //Add the connection to the pool with credentials, this is safe because each user has their own pool and it minimizes credentials prompts
                 this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                 
                 try {
                     om = new Om(iconOmConnection);
                     
                     //Test the connection with a no-op so that ICON can evaluate if RACF is required
                     om.noOpCommand();
                     
                     //Set the new iconOmConnection in use to true
                     iconOmConnection.setSessionInUse(true);
                 } catch (OmConnectionException e1) {
                    //Note: Giving the user a second chance to enter RACF credentials.
                    if(e1.getErrorNumber().contains("HWS0043E")){ //HWS0043E RACF AUTH ERROR
                        //Mark connection error, we need to create new connections on error because ICON disconnects the socket.
                        iconOmConnection.setErrorInConnection(true);
                        iconOmConnection.setSessionInUse(false);
                        iconOmConnection.disconnect();
                        
                        //Remove the invalid connection from the pool so that a clone is not attempted from a connection that is errored and disconnected
                        this.imsConnectionCache.removeCacheEntry(connectionKey);
                        
                        userPass = userPassCallback.getUserPassCredentials(null, environmentName, iconOmConnection.getHostName(), iconOmConnection.getPort(), null);
                        iconOmConnection = createIconOmConnection(environment, imsplex.trim());
                        
                        if(userPass != null){
                            iconOmConnection.setRacfPassword(userPass.getPassword());
                            iconOmConnection.setRacfUser(userPass.getUsername());
                            iconOmConnection.setRacfGroup("");
                        }

                        //Add the connection to the pool with credentials, this is safe because each user has their own pool and it minimizes credentials prompts
                        this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                        
                        om = new Om(iconOmConnection);
                        
                        //Test the connection with a no-op so that ICON can evaluate if RACF is required
                        om.noOpCommand();
                        
                        //Set the new iconOmConnection in use to true
                        iconOmConnection.setSessionInUse(true);
                    }
                }
                 
             //A connection was put back in the pool that was disconnected by user and bypassed our other checks but is being caught now and connection recreated
             }else{ 
                 
                 this.imsConnectionCache.removeCacheEntry(connectionKey);
                 
                 //Create a new iconOmConnection
                 iconOmConnection = createIconOmConnection(environment, imsplex.trim());
                 
                 if(iconOmConnection != null){
                     iconOmConnection.setSessionInUse(true);
                     
                     //If there are no credentials could be a non-racf connection or user did not save credentials
                     if(userPassCredentials != null){
                         iconOmConnection.setRacfPassword(userPassCredentials.getPassword());
                         iconOmConnection.setRacfUser(userPassCredentials.getUsername());
                         iconOmConnection.setRacfGroup("");
                     }
                     
                     //Add the reset connection into the cache
                     this.imsConnectionCache.putCacheEntry(connectionKey, iconOmConnection);
                         
                     om = new Om(iconOmConnection);
                     
                     //Test the connection with a no-op so that ICON can evaluate if RACF is required
                     om.noOpCommand();
                     
                     //Set the new iconOmConnection in use to true
                     iconOmConnection.setSessionInUse(true);
                     
                     if(logger.isDebugEnabled()){
                         logger.debug("A new connection has been created to replace a disconnected connection and cached " + this.toString());
                     }
                 }else{ //Connection is null, must be the host is non-existent, throw OmConnectionException
                     OmConnectionException omConnectionException = new OmConnectionException(IQEO.IQEO0001E.msg(new Object[] {"Connection request returned null"}));
                     omConnectionException.setConnectionType(this.myConnectionType);
                     omConnectionException.setImsplexName(imsplex);
                     omConnectionException.setEnvironmentId(environment);
                     throw omConnectionException;
                 }
             }
         } catch (OmException ome){
        	 OmConnectionException omConnectionException = new OmConnectionException(ome.getMessage());
        	 omConnectionException.setImsplexName(imsplex);
        	 omConnectionException.setEnvironmentId(environment);
        	 throw omConnectionException;
         } catch (CredentialsException ex) {
        	 OmConnectionException omConnectionException = new OmConnectionException(ex);
        	 omConnectionException.setImsplexName(imsplex);
        	 omConnectionException.setEnvironmentId(environment);
        	 throw omConnectionException;
         } catch (DatastoreException ex) {
        	 OmConnectionException omConnectionException = new OmConnectionException(ex);
        	 omConnectionException.setImsplexName(imsplex);
        	 omConnectionException.setEnvironmentId(environment);
        	 throw omConnectionException;
         }
     }catch (OmException ome){
    	 OmConnectionException omConnectionException = new OmConnectionException(ome.getMessage());
    	 omConnectionException.setImsplexName(imsplex);
    	 omConnectionException.setEnvironmentId(environment);
    	 throw omConnectionException;
     } finally{
         if(logger.isDebugEnabled()){
        	 logger.debug("<< getConnection("+ environment +","+imsplex.trim()+")");
             logger.debug(iconOmConnection.toString());
         }
     }
     
     //Set the env and imsplex 
     iconOmConnection.setEnvironment(environment);
     iconOmConnection.setImsplex(imsplex);
     
     return iconOmConnection;
 }
    
 /**
  * Method returns a users credentials needed for a connection such as RACF and are obtained from the UserInfo
  * and the component (address). For an omConnection the, the address is used to map a users credentials.
  * @throws OmException
  */
 private UserPassCredentials getUserPassCredentials(UserInfo userInfo, String address) throws OmConnectionException {
     // UserPassCallback userPassCallback = userInfo.getUserPassCallback();
     // Lets check the users credentials and see if they have RACF configured for this connection and retun if so.
     CredentialsManager credentialsManager = userInfo.getCredentialsManager();
     UserPassCredentials userPassCredentials = null;
     Credentials credentials = null;

     try {
         credentials = credentialsManager.getCredentialsFor(address);
     } catch (CredentialsException e) {
         throw new OmConnectionException(e);
     }

     if (credentials != null) {
         if (logger.isDebugEnabled())
             logger.debug("Saved Credentials were found for {}", address);

         // Make sure we have UserPassCredentials...we don't actually support anything else right now.
         if (credentials instanceof UserPassCredentials == false) throw new OmConnectionException("Invalid Credentials Type: " + credentials.getClass());

         userPassCredentials = (UserPassCredentials) credentials;
     }
     return userPassCredentials;
 }
 
    /**
     * Create a connection for OM and return it for use. Method will try to find a connection 
     * that can satisfy the request by searching our connectivity data in Derby. It will test 
     * each one before runturning a connection that is connected else it will return null if none
     * are connected.
     * @throws OmDatastoreException
     * @throws OmConnectionException 
     */
	private IconOmConnection createIconOmConnection(int environmentId, String imsplexName) throws OmConnectionException  {
		ImsconnectStore imsconnectStore   = null;
		Collection<ImsconnectBean> icons  = null;
		try{
		    imsconnectStore = ImsbaseDatastore.getInstance().getImsconnectStore();
		    icons = imsconnectStore.getImsconnectBy(environmentId, imsplexName);
		}catch (DatastoreException e){
            OmConnectionException omConnectionException = new OmConnectionException(e.getMessage());
            omConnectionException.setEnvironmentId(environmentId);
            omConnectionException.setImsplexName(imsplexName);
            throw omConnectionException;
		}
		
		IconOmConnection iconOmConnection = null;
		
		//Look over all SSL ICON's configured in the setup, if one connects return it
		for(ImsconnectBean imsconnectBean: icons){
			if(imsconnectBean.isUsesSsl()){
			    //Before returning a connection ensure its alive and ping it else keep looking
                try {
                    iconOmConnection = createIconOmConnectionFromData(imsconnectBean,environmentId,imsplexName);
                } catch (OmConnectionException e) {
                   //Do nothing, if we encounter a connectivity exception continue trying the other icons
                }
			    
			    if(iconOmConnection != null){
			        return iconOmConnection;
			    }
			}
		}
		
		//No SSL ICON was configured or may not have connected, look over non-ssl ICON and try to return a connection
	    for(ImsconnectBean imsconnectBean: icons){
	        if(!imsconnectBean.isUsesSsl()){
	              //Before returning a connection ensure its alive and ping it else keep looking
                try {
                    iconOmConnection = createIconOmConnectionFromData(imsconnectBean,environmentId,imsplexName);
                } catch (OmConnectionException e) {
                  //Do nothing, if we encounter a connectivity exception continue trying the other icons
                }
	               
	            if(iconOmConnection != null){
	                return iconOmConnection;
	            }
	        }
	    }

	    //Return null if we were not able to create a connection and add it to the cache
		return null; 
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
	private IconOmConnection createIconOmConnectionFromData(ImsconnectBean icon, int environmentId, String imsplexName) throws OmConnectionException {
        //ICON Configuration Properties
        ConnectionFactoryProperties connectionFactoryProperties = new ConnectionFactoryProperties(icon.getHostname(), Integer.valueOf(icon.getPort()), Boolean.valueOf(icon.isUsesSsl()));
        
        if(icon.isUsesSsl()){
            //Set the bytes in the connectonFactoryProperties for use when this connection must be cloned
            connectionFactoryProperties.setKeystoreBytes(icon.getKeystore());
            connectionFactoryProperties.setTruststoreBytes(icon.getTruststore());
            
            //Connection Factory Properties
            connectionFactoryProperties.setSslKeystoreName(null);
            connectionFactoryProperties.setSslKeystorePassword(icon.getKeystorePass());
            connectionFactoryProperties.setSslTruststoreName(null);
            connectionFactoryProperties.setSslTruststorePassword(icon.getTruststorePass());
            connectionFactoryProperties.setSslEncryptionType(ApiProperties.SSL_ENCRYPTIONTYPE_STRONG);
            InputStream keystoreInputStream = new ByteArrayInputStream(icon.getKeystore());
            InputStream truststoreInputStream = new ByteArrayInputStream(icon.getTruststore());
            connectionFactoryProperties.setSslKeystoreInputStream(keystoreInputStream);
            connectionFactoryProperties.setSslTruststoreInputStream(truststoreInputStream);
        }
            
        //ICON TMinteraction properties
        TmInteractionProperties tmInteractionProperties = null;
        tmInteractionProperties = new TmInteractionProperties(icon.getDatastoreName());
            
        //Create an OmConnection to establish the connection and be placed in the cacheConnection
        IconOmConnection iconOmConnection = new IconOmConnection(connectionFactoryProperties, tmInteractionProperties);
        iconOmConnection.setConnectionType(this.getConnectionType());
        iconOmConnection.createConnection();
        
        //Print out the cache to the log
        if(logger.isDebugEnabled()){
            this.imsConnectionCache.printCacheEntrysAll();
        }
        
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
