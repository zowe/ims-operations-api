package utils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import json.java.JSONArray;
import json.java.JSONObject;

/**
 * Set of utility methods to assist REST methods
 * @author jerryli
 *
 */
public class RestUtils {

	private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);


	/**
	 * This method processes the JSON response received from MC/OM. Returns the appropriate response to be 
	 * propagate back to client
	 * @param result
	 * @return
	 */
	public static Response processCommandOutput(JSONObject result) {
		if (result != null) {
			JSONObject message = (JSONObject) result.get("messages");
			JSONArray executeUserImsCommand = (JSONArray) message.get("executeImsCommand");
			JSONObject messageElement = (JSONObject) executeUserImsCommand.get(0);
			String status = (String) messageElement.get("status");

			if (status.equals("success")) {
				logger.debug("IMS Command Successful");
				return Response.ok(result).build();
			} else if (status.equals("warning")) {
				logger.debug("OM returned non zero return code: " + result.toString());
				return Response.status(Status.BAD_REQUEST).entity(result).build();
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(result).build();
		}
	}
}