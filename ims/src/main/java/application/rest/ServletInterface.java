/**
 *  Copyright IBM Corporation 2018, 2019
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
