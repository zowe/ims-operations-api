/**
 *  Copyright IBM Corporation 2018, 2019
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
