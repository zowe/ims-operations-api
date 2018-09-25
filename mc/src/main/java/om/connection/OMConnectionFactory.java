/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/

package om.connection;


/**
 * Interface provides a connection factory for use by {@link OMConnectionManager} to 
 * get connection types and connection. 
 *
 */
public interface OMConnectionFactory {
    
    /**
     * Sets the connection type that identifies the OMConnectionFactory. Connection types
     * identify the OMConnection that is registered with the OMConnectionManager.
     * @return Connection type identified 
     */
    public void setConnectionType(String type);
    
	
}
