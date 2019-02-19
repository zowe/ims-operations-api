/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.services;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotations.CheckHeader;
import application.rest.OMServlet;
import exceptions.RestException;
import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import json.java.JSONObject;

@Stateless
@Path("/{plex}/region")
@Tag(name = "Region")
@CheckHeader
public class RegionService {

	private static final Logger logger = LoggerFactory.getLogger(RegionService.class);


	OMServlet omServlet = new OMServlet();

	@Path("/stop")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId="stoprgn", summary = "Stop IMS message and application processing regions using 'START/STOP REGION' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response stop(

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, description = "Region Number Identifier. Can specify a Region Number or a Job Name, but NOT BOTH.",
			array=@ArraySchema(schema = @Schema(type = "integer"), maxItems = 2))
			@QueryParam("reg_num") 
			String regNumber,

			@Parameter(description = "Can specify a Region Number or a Job Name, but NOT BOTH.", schema = @Schema(maxLength = 8))
			@QueryParam("job_name") 
			String jobName,

			@Parameter(description = "Specify a transaction to abnormally terminate")
			@QueryParam("abdump") 
			String abdump,

			@Parameter(description = "Specify a program in WFI mode to stop its message processing within the specified region")
			@QueryParam("transaction") 
			String transaction,

			@Parameter()
			@QueryParam("cancel") 
			boolean cancel,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			
			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,
			
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type="string")))
			@QueryParam("route") 
			String imsmbr) {


		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((STOP REGION ");
		if (regNumber != null) {
			String[] regNumbers = regNumber.split("\\s*,\\s*");
			if (regNumbers.length > 1) {
				sb.append(regNumbers[0]).append("-").append(regNumbers[1]).append(" ");
			} else {
				sb.append(regNumber).append(" ");
			}
			if (abdump != null) {
				sb.append("ABDUMP " + abdump + " ");
			}
			if (transaction != null) {
				sb.append("TRANSACTION " + transaction + " ");
			}
			if (cancel) {
				sb.append("CANCEL");
			}
		}
		if (jobName != null) {
			sb.append("JOBNAME ").append(jobName + " ");
			if (abdump != null) {
				sb.append("ABDUMP " + abdump + " ");
			}
			if (transaction != null) {
				sb.append("TRANSACTION " + transaction + " ");
			}
			if (cancel) {
				sb.append("CANCEL");
			}
		} 
		sb.append(")");
		sb.append(" OPTION=AOPOUTPUT");
		sb.append(")");

		JSONObject result = new JSONObject();
		
		if (imsmbr != null) {
			sb.append(" ROUTE(");
			List<String> routeList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
			for (String s : routeList) {
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}


		try {
			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}

		logger.debug("IMS Command Successfully Submitted. Check Return Code.");
		return Response.ok(result).build();

	}

	@Path("/start")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId="startrgn", summary = "Start IMS message and application processing regions using 'START/STOP REGION' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response start(

			@Parameter(description = "Region Number Identifier", required = true)
			@QueryParam("member_name") 
			String memName,

			@Parameter(description = "")
			@QueryParam("job_name") 
			String jobName,

			@Parameter()
			@QueryParam("local") 
			boolean local,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			
			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,
			
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type="string")))
			@QueryParam("route") 
			String imsmbr) {


		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((START REGION ");
		if (memName != null) {
			sb.append(memName + " ");
			if (jobName != null) {
				sb.append("JOBNAME ").append(jobName + " ");
			}
		} 
		if (local) {
			sb.append("LOCAL");

		}

		sb.append(")");
		sb.append(" OPTION=AOPOUTPUT");
		sb.append(")");

		JSONObject result = new JSONObject();
		
		if (imsmbr != null) {
			sb.append(" ROUTE(");
			List<String> routeList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
			for (String s : routeList) {
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}


		try {
			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}

		logger.debug("IMS Command Successfully Submitted. Check Return Code.");
		return Response.ok(result).build();

	}
	
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId="disrgn", summary = "Display region and DC information associated with an IMS™ system. The region is scheduled to an application program and the IMS resources are assigned.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response display(
			@Parameter()
			@QueryParam("dc") 
			boolean dc,
			
			@Parameter(description = "")
			@QueryParam("region") 
			boolean region,
			
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			
			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,
			
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type="string")))
			@QueryParam("route") 
			String imsmbr,
			
			@Context 
			UriInfo uriInfo) {
		
		
		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((DIS ACT REGION");
		

		sb.append(")");
		sb.append(" OPTION=AOPOUTPUT");
		sb.append(")");

		JSONObject result = new JSONObject();
		
		if (imsmbr != null) {
			sb.append(" ROUTE(");
			List<String> routeList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
			for (String s : routeList) {
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}


		try {
			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}

		logger.debug("IMS Command Successfully Submitted. Check Return Code.");
		return Response.ok(result).build();

	}
		
		
}