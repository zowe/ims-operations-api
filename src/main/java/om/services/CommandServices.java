/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import om.exception.OmConnectionException;
import om.exception.OmException;
import om.result.OmResultSet;
import om.service.CommandService;
import om.version.Version;


/**
 * <pre>
 * Class provides methods to interact with OM through executing OM Type1 or Type2 commands. 
 * Return types are generalized for all interactions as a Collection of {@link OmResultSet} objects. 
 * 
 * It should be noted that the Collection only manages the response for the particular 
 * resource and that OM Messages including Reason Codes, Reason Text, etc are accessible 
 * from the {@link Om} instance managing the interaction. 
 * 
 * See  <code>{@link Om#getOmMessageContexts()} </code> on how to access OM Messages.
 * </pre>
 * 
 */
public final class CommandServices extends ServicesHelper implements CommandService {

	private static final Logger logger = LoggerFactory.getLogger(CommandServices.class);

	//This must remain protected else the Om instance will not be able to aggregate all the Om Messages
	protected CommandServices(Om om) {
		super(om);

	}




	public OmResultSet executeImsCommand(String callingMethodName, String command) throws OmException,OmConnectionException {
		if (logger.isDebugEnabled()) logger.debug(">> executeImsCommand("+ callingMethodName + "," +command+ ")");

		//Local omresultSet otherwise we risk data corruption of the service is reused. 
		OmResultSet omResultSet = null;

		try{
			//Since no version was passed use the latest version
			//this.version = this.resourceVersion.get("latestResourceVersion");
			this.version = new Version("15.1.0");
			
			Service service = new Service(this.om, this.version);
			omResultSet = service.executeCommand(callingMethodName,command);
			return omResultSet;

		}finally{
			if (logger.isDebugEnabled()) logger.debug("<< executeImsCommand("+ callingMethodName + "," +command+ ")");

		}
	}

	public OmResultSet executeImsCommand(String callingMethodName, String command, Version version) throws OmException,OmConnectionException {
		if (logger.isDebugEnabled()) logger.debug(">> executeImsCommand("+ callingMethodName + "," +command+ version +")");

		//Local omresultSet otherwise we risk data corruption of the service is reused. 
		OmResultSet omResultSet = null;

		try{
			Service service = new Service(this.om, this.resourceVersion.get("latestResourceVersion"));
			omResultSet = service.executeCommand(callingMethodName,command);
			return omResultSet;

		}finally{
			if (logger.isDebugEnabled()) logger.debug("<< executeImsCommand("+ callingMethodName + "," +command+  version +")");

		}
	}
}
