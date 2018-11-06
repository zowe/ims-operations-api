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
		JSONObject data = (JSONObject) result.get("commandExecutedGrid");

		if (data != null) {
		//Parse the omMessageContext to determine what response to send back to client
		JSONObject message = (JSONObject) result.get("message");
		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
		JSONArray executeUserImsCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
		JSONObject messageElement = (JSONObject) executeUserImsCommand.get(0);
		String status = (String) messageElement.get("status");

		if (status.equals("success")) {
			logger.debug("IMS Command Successful");
			return Response.ok(result.get("commandExecutedGrid"), MediaType.APPLICATION_JSON).build();
		} else if (status.equals("warning")) {
			logger.debug("OM returned non zero return code: " + data.toString() + " " + (String) messageElement.get("message"));
			return Response.status(Status.BAD_REQUEST).entity(messageElement).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(result.get("message")).build();
		}
	}
}
