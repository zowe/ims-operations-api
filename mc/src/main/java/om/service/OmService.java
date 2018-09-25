/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/

package om.service;

import om.services.Om;


/**
 * Interface provides the services available through {@link Om} operations. After instantiating
 * and {@link Om} one of these service types must be returned to perform operations on the various
 * IMS Resources.
 * @author IBM IMS
 *
 */
public interface OmService {



	/**
	 * Method provides access to various Command Line Service operations available. 
	 * @return {@link CommandService}
	 */
	public CommandService getCommandService();


}
