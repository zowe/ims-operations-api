package rs;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import annotations.CheckHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import json.java.JSONObject;

@Stateless
@Path("/")
@Api(tags = {"Region"})
@CheckHeader
public class Region {



	@Path("/stop")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from STOP REGION IMS command", httpMethod="PUT", notes = "<br>This service submits a 'Stop Region' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful Operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Request Error"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response stop(

			@ApiParam(allowMultiple = false)
			@QueryParam("regNum") 
			Integer regNumber,

			@ApiParam(allowMultiple = false)
			@QueryParam("jobnames") 
			List<String> name,

			@ApiParam(allowMultiple = false, value = "abnormal termination of specified transaction")
			@QueryParam("abdump") 
			String abdump,

			@ApiParam(allowMultiple = false, value = "stops a message processing program in WFI mode from processing within the specified region")
			@QueryParam("transaction") 
			String transaction,

			@ApiParam(allowMultiple = false)
			@QueryParam("cancel") 
			boolean cancel,

			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}





}