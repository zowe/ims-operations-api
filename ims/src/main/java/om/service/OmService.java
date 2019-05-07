
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

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
