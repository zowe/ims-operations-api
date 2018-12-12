package application.rest;

import javax.ejb.Local;
import javax.ejb.Remote;

import exceptions.RestException;
import icon.helpers.MCInteraction;
import json.java.JSONObject;

@Local
public interface ServletInterface {
	
	public JSONObject executeImsCommand(String command, MCInteraction mcSpec) throws RestException; 

}
