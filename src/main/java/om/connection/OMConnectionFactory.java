/**
 *  Copyright IBM Corporation 2018, 2019
 */

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
