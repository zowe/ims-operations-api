package com.ibm.ims.ea.om.connection.icon;
/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/


import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.im.ac.user.UserInfoBinding;
import com.ibm.ims.ea.base.cache.ConcurrentCache;
import com.ibm.ims.ea.om.common.exception.OmConnectionException;


/**
 * Class represents a cache populated with {@link IconOmConnection} objects that serve as a connection to answer a command request.
 * This cache is using a the java concurrency map.
 * @author ddimatos
 */
public class IconOmConnectionCache extends ConcurrentCache<IconOmConnection> implements Serializable,UserInfoBinding{
    private final static Logger logger = LoggerFactory.getLogger(IconOmConnectionCache.class);
    
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -5279530104913519161L;

	//private static IconOmConnectionCache cache = null;
	//private IconOmConnectionCache cache = null;
	//private IconOmConnectionCache() { /** Keep me private **/ }

    @Override
    public void valueUnbound() {
    	if(logger.isDebugEnabled()){
    		logger.debug(">> valueUnbound" );
    	}
    	
        if(this != null){
            for (Map.Entry<String, IconOmConnection> entry : this.entrySet()){
                try {
                    entry.getValue().disconnect();
                	
                    if(logger.isDebugEnabled()){
                		logger.debug("Unbinding IconOmConnection: disconnected");
                		logger.debug(entry.getValue().toString());
                	}
                	
                    if(entry.getValue().isSessionInUse() == true){
                    	if(logger.isDebugEnabled()){
                    		logger.debug("Unbinding IconOmConnection:  Warning, session still in use");
                    		logger.debug(entry.getValue().toString());
                    	}
                    }
                    
                    if(this.removeCacheEntry(entry.getKey()) == false){
                    	if(logger.isDebugEnabled()){
                    		logger.debug("Unbinding IconOmConnection: Warning, unable to remove connection from cache");
                    		logger.debug(entry.getValue().toString());
                    	}
                    }
                	
                } catch (OmConnectionException e) {
                    logger.debug("Unabled to disconnect connection\n" + entry.getValue());
                }
            }
        }
        
    	if(logger.isDebugEnabled()){
    		logger.debug("<< valueUnbound" );
    	}
    }
	
	/**
	 * Returns instance of the cache. 
	 * @return Cache instance if not null, otherwise a new instance is created.
	 */
//	public static synchronized IconOmConnectionCache getInstance(){
//		if(cache == null){
//			cache = new IconOmConnectionCache();
//		}
//		return cache;
//	}
	
	
}
