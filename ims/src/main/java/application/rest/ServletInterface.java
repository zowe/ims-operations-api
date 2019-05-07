
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package application.rest;

import javax.ejb.Local;

import exceptions.RestException;
import icon.helpers.MCInteraction;
import json.java.JSONObject;

@Local
public interface ServletInterface {
	
	public JSONObject executeImsCommand(String command, MCInteraction mcSpec) throws RestException; 

}
