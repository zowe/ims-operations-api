/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/

package om.common.service;

import om.common.version.Version;
import om.common.exception.OmConnectionException;
import om.common.exception.OmDatastoreException;
import om.common.exception.OmException;
import om.common.result.OmResultSet;

public interface CommandService {

    
    /**
     * Executes a Type1 or Type2 OM command and returns a Resultset.
     * @param command
     * @return
     * @throws OmException
     * @throws OmConnectionException 
     * @throws OmDatastoreException 
     */
    public OmResultSet executeImsCommand(String callingMethodName, String command) throws OmException, OmConnectionException, OmDatastoreException;

    /**
     * Executes a Type1 or Type2 IMS Command
     * @param callingMethodName - Key use to identify objects managed by the OM connection instance
     * @param command - Command to be executed
     * @param version - Version of resource we are executing the command against. The default behavior for the overloaded methods
     * that don't require a version will use the latest version of member type IMS, it could be you don't want to use the latest
     * version or member type IMS , so passing Version in is used instead.
     * @return
     * @throws OmException
     * @throws OmConnectionException
     * @throws OmDatastoreException
     */
	public OmResultSet executeImsCommand(String callingMethodName, String command,Version version) throws OmException, OmConnectionException, OmDatastoreException;

}
