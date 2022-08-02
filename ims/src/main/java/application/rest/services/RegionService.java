
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package application.rest.services;

import java.util.Arrays;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RolesAllowed({"ims-admin", "region-user", "put-user"})
	@Operation(operationId="stoprgn", summary = "Stop IMS message and application processing regions by using the '/STOP REGION' IMS command. For more information on each parameter, see the documentation for the '/STOP REGION' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response stop(

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, description = "Specifies the identifier of the region that you want to terminate. You can specify multiple identifiers to terminate multiple regions. If you specify this parameter, do not specify the job_name parameter.",
			array=@ArraySchema(schema = @Schema(type = "integer"), maxItems = 2))
			@QueryParam("reg_num") 
			String regNumber,

			@Parameter(description = "Specifies the name of the job for the region that you want to terminate. You can specify multiple job names to terminate multiple regions. If you specify this parameter, do not specify the reg_num parameter.", schema = @Schema(maxLength = 8))
			@QueryParam("job_name") 
			String jobName,

			@Parameter(description = "Specifies the transaction code of a program that you want to abnormally terminate.")
			@QueryParam("abdump") 
			String abdump,

			@Parameter(description = "Specifies the transaction code of a program that is in wait-for-input (WFI) mode and that you want to stop processing within the specified region.")
			@QueryParam("transaction") 
			String transaction,

			@Parameter(description="Specifies whether to abnormally terminate a region when a program that is running in the region cannot be terminated with the 'abdump' parameter.")
			@QueryParam("cancel") 
			boolean cancel,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, description = "Specifies the ID of the IMS system in the IMSplex that the API call is routed to.", array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
		
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user ID", required = false) @HeaderParam("user_id") String username,
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user password", required = false) @HeaderParam("password") String password,

			@Parameter(in = ParameterIn.PATH, description = "Specifies the IMSplex to which you are directing the API call.")
			@PathParam("plex") 
			String plex
			) {


		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		
		if (username != null && password != null) {
			mcSpec.setRacfUsername(username);
			mcSpec.setRacfPassword(password);
			mcSpec.setRacfEnabled(true);
		}
		
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
	@RolesAllowed({"ims-admin", "region-user", "put-user"})
	@Operation(operationId="startrgn", summary = "Start IMS message and application processing regions by using the '/START REGION' IMS command. For more information on each parameter, see the documentation for the '/START REGION' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response start(

			@Parameter(required = true, description ="Specifies the name of the IMS PROCLIB member that contains the JCL used to start the region. If no member name is specified, the default member name is used.")
			@QueryParam("member_name") 
			String memName,

			@Parameter(description = "Specifies the job name that overrides the JOB statement value specified in the JCL of the default or specified member.")
			@QueryParam("job_name") 
			String jobName,

			@Parameter(description="Specifies whether IMS overrides the symbolic IMSID parameter in the JCL of the default or specified member. If you specify a value for the job_name parameter, the value of this parameter is 'true' by default.")
			@QueryParam("local") 
			boolean local,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, description = "Specifies the ID of the IMS system in the IMSplex that the API call is routed to.", array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
		
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user ID", required = false) @HeaderParam("user_id") String username,
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user password", required = false) @HeaderParam("password") String password,

			@Parameter(in = ParameterIn.PATH, description = "Specifies the IMSplex to which you are directing the API call.")
			@PathParam("plex") 
			String plex
			) {


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
	@RolesAllowed({"ims-admin", "region-user", "get-user"})
	@Operation(operationId="disrgn", summary = "Display region information associated with an IMS™ system by using the '/DISPLAY ACT' IMS command. The region is scheduled to an application program and the IMS resources are assigned. For more information on each parameter, see the documentation for the '/DISPLAY ACT' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response display(
			@Parameter()
			@QueryParam("dc") 
			boolean dc,
			
			@Parameter(description = "Displays the active regions.")
			@QueryParam("region") 
			boolean region,
			
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, description = "Specifies the ID of the IMS system in the IMSplex that the API call is routed to.", array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
		
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user ID", required = false) @HeaderParam("user_id") String username,
			@Parameter(in = ParameterIn.HEADER, description = "The RACF user password", required = false) @HeaderParam("password") String password,

			@Parameter(in = ParameterIn.PATH, description = "Specifies the IMSplex to which you are directing the API call.")
			@PathParam("plex") 
			String plex,
			
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


		try {			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}

		logger.debug("IMS Command Successfully Submitted. Check Return Code.");
		return Response.ok(result).build();

	}
		
		
}