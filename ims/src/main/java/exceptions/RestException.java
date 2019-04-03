
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package exceptions;

import json.java.JSONObject;

/**
 * Wrapper for exceptions thrown from servlet to rest layer
 * @author jerryli
 *
 */
public class RestException extends Exception {
	
	private static final long serialVersionUID = 5400767546840379034L;
	private JSONObject response;
	
	public RestException(String message){
		super(message);
	}
	
	public RestException(String message, JSONObject response){
		super(message);
		this.response = response;
	}
	
	public RestException(String message,Throwable cause){
		super(message,cause);
	}
	

	public RestException(Throwable cause){
		super(cause);
	}
	
	
	public void setResponse(JSONObject response) {
		this.response = response;
	}
	
	public JSONObject getResponse() {
		return this.response;
	}

}
