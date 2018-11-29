package rs.services;

import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import annotations.CheckHeader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Stateless
@Path("/region")
@Tag(name = "Region")
@CheckHeader
public class Region {



	@Path("/")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Return data from 'STOP REGION' IMS command",
			responses = { @ApiResponse(content = @Content(mediaType="application/json")),
					@ApiResponse(responseCode = "200", description = "Successful Request"),
					@ApiResponse(responseCode = "400", description = "Request Error"),
					@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response stop(

			@Parameter(description = "Region Number Identifier")
			@QueryParam("regNum") 
			Integer regNumber,

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("jobnames") 
			String names,

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


		return Response.ok().build();
	}





}