package rs;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotations.CheckHeader;
import commands.create.pgm.CreatePgm;
import commands.query.pgm.QueryPgm;
import commands.query.pgm.QueryPgm.Show2Options;
import commands.query.pgm.QueryPgm.Show3Options;
import commands.query.pgm.QueryPgm.ShowOptions;
import commands.query.pgm.QueryPgm.StatusOptions;
import commands.type2.Type2Command;
import icon.helpers.MCInteraction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import json.java.JSONObject;
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;
import zowe.mc.exceptions.RestException;
import zowe.mc.servlet.OMServlet;

/**
 * Restful interface for IMS commands pertaining to program resources
 * @author jerryli
 *
 */
@Stateless
@Path("/pgm")
@Api(tags = {"Program"})
@CheckHeader
public class Pgm {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Pgm.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from Query PGM IMS command", httpMethod="GET", notes = "<br>This service submits a 'Query PGM' IMS command and returns the output.", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response query(
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("names") String names, 
			
			@ApiParam(allowMultiple = true, collectionFormat = "csv", allowableValues = "ALL, BMPTYPE, DEFN, DEFNTYPE, DOPT, FP, GLOBAL, IMSID, GPSB, LANG, LOCAL, MODEL, RESIDENT, SCHDTYPE, STATUS, TIMESTAMP, TRANSTAT")
			@QueryParam("show") String show, 
			
			@ApiParam(allowMultiple = false, allowableValues = "EXPORTNEEDED")
			@QueryParam("show2") 
			String show2,
			
			@ApiParam(allowMultiple = false, allowableValues = "DB, RTC, TRAN, WORK")
			@QueryParam("show3") 
			String show3,
			
			@ApiParam(allowMultiple = true, allowableValues = "DB_NOTAVL, IOPREV, LOCK, NOTINIT, STOSCHD, TRACE")
			@QueryParam("status") 
			String status,
			
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("route") 
			String imsmbr, 
			
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		QueryPgm pgm = new QueryPgm();

		if (names != null) {
			List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
			pgm.getNAME().addAll(nameList);
		}

		ArrayList<ShowOptions> showOptions = new ArrayList();
		if (show != null) {
			List<String> showList = Arrays.asList(show.split("\\s*,\\s*"));
			for (String s : showList) {
				showOptions.add(ShowOptions.fromValue(s));
			}
			pgm.getSHOW().addAll(showOptions);
		}
		if (show2 != null) {
			pgm.setSHOW2(Show2Options.fromValue(show2));
		}
		if (show3 != null) {
			pgm.setSHOW3(Show3Options.fromValue(show3));
		}
		
		ArrayList<StatusOptions> statusOptions = new ArrayList();
		if (status != null) {
			List<String> statusList = Arrays.asList(status.split("\\s*,\\s*"));
			for (String s : statusList) {
				statusOptions.add(StatusOptions.fromValue(s));
			}
			pgm.getSTATUS().addAll(statusOptions);
		}
		

		Type2Command type2Command = new Type2Command();
		type2Command.setQueryPgm(pgm);
		type2Command.setVerb(Type2Command.VerbOptions.QUERY); 
		type2Command.setResource(Type2Command.ResourceOptions.PGM);

		if (imsmbr != null) {
			List<String> imsmbrList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
			type2Command.getRoute().addAll(imsmbrList);
			mcSpec.getDatastores().addAll(imsmbrList);
		}

		JSONObject result = new JSONObject();

		Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();
		try {
			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} 

		logger.debug("IMS Command Successful");
		return Response.ok(result).build();
		//return RestUtils.processCommandOutput(result);
	}

	@Path("/start")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from START PGM IMS command", httpMethod="PUT", notes = "<br>This service submits a 'Start PGM' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response start(
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("name") 
			String name,
			
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("route") 
			String imsmbr,
			
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((STA PGM");
		if (name != null) {
			sb.append(" " + name);
		} else {
			sb.append(" ALL");
		}
		sb.append(")");
		sb.append(" OPTION=AOPOUTPUT");
		sb.append(")");
		
		if (imsmbr != null) {
			List<String> imsmbrList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
			mcSpec.getDatastores().addAll(imsmbrList);
			sb.append("ROUTE(");
			for (String s : imsmbrList) {
				sb.append(s).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}

		JSONObject result = new JSONObject();

		try {
			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}

		logger.debug("IMS Command Successful");
		return Response.ok(result).build();

	}


	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from CREATE PGM IMS command", httpMethod="POST", notes = "<br>This service submits a 'Create PGM' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Om returned non zero return code"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response create(@QueryParam("names") List<String> name, @QueryParam("route") List<String> imsmbr, @QueryParam("set") List<String> set,
			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);

		CreatePgm pgm = new CreatePgm();
		pgm.getNAME().addAll(name);

		Type2Command type2Command = new Type2Command();
		type2Command.setCreatePgm(pgm);
		type2Command.setVerb(Type2Command.VerbOptions.CREATE); 
		type2Command.setResource(Type2Command.ResourceOptions.PGM);
		type2Command.getRoute().addAll(imsmbr);

		mcSpec.getDatastores().addAll(imsmbr);

		JSONObject result = new JSONObject();

		Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();
		try {
			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} 

		logger.debug("IMS Command Successful");
		return Response.ok(result).build();

	}





}
