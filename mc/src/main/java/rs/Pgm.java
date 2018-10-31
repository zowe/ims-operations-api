package rs;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotations.CheckHeader;
import commands.query.pgm.QueryPgm;
import commands.type2.Type2Command;
import icon.helpers.MCInteraction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import json.java.JSONArray;
import json.java.JSONObject;
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;
import zowe.mc.servlet.OMServlet;

@Stateless
@Path("/pgm")
@Api(tags = {"pgm"})
@CheckHeader
public class Pgm {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Pgm.class);

	@Path("/execute")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from Query PGM IMS command", httpMethod="GET", notes = "<br>This service submits a 'Query PGM' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
							@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
							@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response execute(@QueryParam("names") List<String> name, @QueryParam("show") List<String> show,
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		//TODO
		//Implement show options. Return http code 400 if invalid show option

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);

		QueryPgm pgm = new QueryPgm();
		pgm.getNAME().addAll(name);

		pgm.getSHOW().add(QueryPgm.ShowOptions.ALL);

		Type2Command type2Command = new Type2Command();
		type2Command.setQueryPgm(pgm);
		type2Command.setVerb(Type2Command.VerbOptions.QUERY); 
		type2Command.setResource(Type2Command.ResourceOptions.PGM);

		JSONObject result = new JSONObject();

		Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();
		try {
			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeUserImsCommand(cmd, mcSpec);
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} 

		JSONObject data = (JSONObject) result.get("commandExecutedGrid");

		//Parse the omMessageContext to determine what response to send back to client
		JSONObject message = (JSONObject) result.get("message");
		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
		JSONArray executeUserImsCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
		JSONObject messageTitle = (JSONObject) executeUserImsCommand.get(0);
		String status = (String) messageTitle.get("status");

		if (status.equals("success")) {
			logger.debug("IMS Command Successful");
			return Response.ok(result.get("commandExecutedGrid"), MediaType.APPLICATION_JSON).build();
		} else if (status.equals("warning")) {
			logger.debug("OM returned non zero return code: " + data.toString() + " " + (String) messageTitle.get("message"));
			return Response.status(Status.BAD_REQUEST).entity(data).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
