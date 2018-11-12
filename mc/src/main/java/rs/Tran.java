package rs;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotations.CheckHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import json.java.JSONObject;
import zowe.mc.servlet.OMServlet;

@Stateless
@Path("/tran")
@Api(tags = {"Transaction"})
@CheckHeader
public class Tran {
	
	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Tran.class);
	
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from QUERY TRAN IMS command", httpMethod="PUT", notes = "<br>This service submits a 'Query Tran' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
			@ApiResponse(code = 400, message = "Error connecting to IMS"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response query(@QueryParam("names") List<String> name, @QueryParam("show") List<String> show,
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {
		
		
		
		return Response.ok().build();
		
	}
	
	
	@Path("/start")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from START TRAN IMS command", httpMethod="GET", notes = "<br>This service submits a 'Start TRAN' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
							@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
							@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response start(@QueryParam("names") List<String> name,
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}
	
	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from CREATE TRAN IMS command", httpMethod="POST", notes = "<br>This service submits a 'Create TRAN' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
							@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
							@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response create(@QueryParam("names") List<String> name, @QueryParam("set") List<String> set, @QueryParam("pgm") String pgm,
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}
	

}
