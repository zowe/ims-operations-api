
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
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
