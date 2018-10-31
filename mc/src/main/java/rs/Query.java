package rs;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commands.query.pgm.QueryPgm;
import commands.type2.Type2Command;
import icon.helpers.MCInteraction;
import json.java.JSONObject;
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;
import zowe.mc.servlet.OMServlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Stateless
@Path("/Query")
@Api(tags = {"Query"})
public class Query {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Query.class);

	@Path("/PGM")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from Query PGM IMS command", httpMethod="GET", notes = "<br>This service submits a 'Query PGM' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200,response = JSONObject.class, message = "Successful operation"),@ApiResponse(code = 400, message = "Bad Request", response = JSONObject.class)})
	public Response execute(@QueryParam("names") List<String> name,
			@HeaderParam("hostname") String hostname,
			@HeaderParam("port") String port,
			@HeaderParam("plex") String plex) {

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
			logger.error("Unable to submit IMS command", e1);
			return Response.status(Status.BAD_REQUEST).entity("Unable to submit IMS command: " 
					+ e1.getMessage()).build();
		} catch (Exception e) {
			logger.error("Exception", e);
			Response.serverError();
			e.printStackTrace();

		}

		return Response.ok(result, MediaType.APPLICATION_JSON).build();

	}

}
