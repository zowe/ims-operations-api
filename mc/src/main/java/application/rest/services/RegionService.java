package application.rest.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import annotations.CheckHeader;
import application.rest.OMServlet;
import exceptions.RestException;
import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import json.java.JSONObject;

@Stateless
@Path("/region")
@Tag(name = "Region")
@CheckHeader
public class RegionService {

	private static final Logger logger = LoggerFactory.getLogger(RegionService.class);
	
	@Autowired
	@EJB
	OMServlet omServlet;

	@Path("/stop")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Return data from 'STOP REGION' IMS command",
			responses = { @ApiResponse(content = @Content(mediaType="application/json")),
					@ApiResponse(responseCode = "200", description = "Successful Request"),
					@ApiResponse(responseCode = "400", description = "Request Error"),
					@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response stop(

			@Parameter(style = ParameterStyle.FORM, description = "Region Number Identifier",
					array=@ArraySchema(schema = @Schema(type = "integer")))
			@QueryParam("regNum") 
			String regNumber,

			@Parameter(schema = @Schema(maxLength = 8))
			@QueryParam("jobname") 
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
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {
		
		
		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((STOP REGION ");
		if (jobName != null) {
			sb.append(jobName + " ");
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
		if (regNumber != null) {
			String[] regNumbers = regNumber.split("\\s*,\\s*");
			for (String n : regNumbers) {
				sb.append(n + " ");
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
		
		sb.append(")");
		sb.append(" OPTION=AOPOUTPUT");
		sb.append(")");
		
		JSONObject result = new JSONObject();
		

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