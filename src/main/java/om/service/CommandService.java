/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.service;

import om.exception.OmConnectionException;
import om.exception.OmException;
import om.result.OmResultSet;
import om.version.Version;

public interface CommandService {

    
    /**
     * Executes a Type1 or Type2 OM command and returns a Resultset.
     * @param command
     * @return
     * @throws OmException
     * @throws OmConnectionException 
     * @throws OmDatastoreException 
     */
    public OmResultSet executeImsCommand(String callingMethodName, String command) throws OmException, OmConnectionException;

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
	public OmResultSet executeImsCommand(String callingMethodName, String command,Version version) throws OmException, OmConnectionException;

}
