package rs.services;

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
import commands.query.pgm.QueryPgm.ShowOptions;
import commands.query.pgm.QueryPgm.StatusOptions;
import commands.type2.Type2Command;
import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import json.java.JSONObject;
import om.exception.OmCommandGenerationException;
import rs.responses.QueryProgramResponse;
import utils.Type2CommandSerializable;
import zowe.mc.exceptions.RestException;
import zowe.mc.servlet.OMServlet;

/**
 * Restful interface for IMS commands pertaining to program resources
 * @author jerryli
 *
 */
@OpenAPIDefinition(
		info = @Info(
				title = "Management Console for Zowe",
				version = "1.0.0",
				description = "Management Console for Zowe allows users to use RESTFul APIs to submit IMS commmands"),
		tags = {@Tag(name="Program"), @Tag(name="Region")},
		servers = {@Server(url = "http://localhost:9080/mc/")}
		)
@Stateless
@Path("/pgm")
@Tag(name = "Program")
@CheckHeader
public class Pgm {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Pgm.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns data from a 'QUERY PGM' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",  
						content = @Content(schema = @Schema(implementation = QueryProgramResponse.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response query(

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)),
					description = "Specifies the 1-8 character name of the program. Wildcards can be specified in the name. The name is a repeatable parameter. The default is NAME(*) which returns all program resources.")
			@QueryParam("names") 
			String names, 

			@Parameter(style = ParameterStyle.FORM, 
			array=@ArraySchema(schema = 
			@Schema(maxLength = 8, allowableValues = {"ALL", "BMPTYP", "DEFN", "DEFNTYPE", "DOPT", "FP", "GLOBAL", 
					"IMSID", "GPSB", "LANG", "LOCAL", "MODEL", "RESIDENT", "SCHDTYPE", "STATUS", 
					"TIMESTAMP", "TRANSTAT", "EXPORTNEEDED", "DB", "RTC", "TRAN", "WORK"})), 
					description = "Specifies the program output fields to be returned. The program name is always returned, along with the name of the IMS™ that created the output, the region type, and the completion code.")
			@QueryParam("show") String show, 

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = 
			@Schema(maxLength = 8, allowableValues = {"DB-NOTAVL", "IOPREV", "LOCK", "NOTINIT", "STOSCHD", "TRACE"})),
					description = "Selects programs for display that possess at least one of the specified program status. This selection allows for additional filtering by program status. The program status is returned as output, even if the SHOW(STATUS) was not specified.")
			@QueryParam("status") 
			String status,

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

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
	}

	@Path("/start")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns data from a 'START PGM' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response start(
			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)),
					description = "Specifies the 1-8 character name of the program. Wildcards can be specified in the name. The name is a repeatable parameter. The default is NAME(*) which returns all program resources.")
			@QueryParam("names") 
			String names, 


			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		StringBuilder sb = new StringBuilder("CMD((STA PGM");
		if (names != null) {
			sb.append(" " + names);
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
	@Operation(summary = "Returns data from a 'CREATE PGM' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response create(
			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)),
				description = "Specifies the 1-8 character name of the program. Wildcards can be specified in the name. The name is a repeatable parameter. The default is NAME(*) which returns all program resources.")
			@QueryParam("names") 
			String names, 


			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(type="string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(description = "Specifies the name of the descriptor to use as a model to define this resource.")
			@QueryParam("desc")
			String desc,

			@Parameter(description = "Specifies the name of the resource to use as a model to define this resource.")
			@QueryParam("rsc")
			String rsc,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), 
				description = "BMP type option. Specifies whether the program executes in a BMP type region or not. A BMP type region can be a BMP region or a JBP region. PSBs scheduled by DB2® stored procedures, by programs running under WebSphere® Application Server, and by other users of the ODBA interface may be defined with BMPTYPE Y or N.")
			@QueryParam("bmptype") 
			String bmptype,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description = "Specifies the dynamic option.")
			@QueryParam("dopt") 
			String dopt,

			@Parameter(schema = @Schema(allowableValues = {"N", "E"}), description = "Specifies the Fast Path option.")
			@QueryParam("fp") 
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description = "Specifies the generated PSB option.")
			@QueryParam("gpsb") 
			String gpsb,

			@Parameter(schema = @Schema(allowableValues = {"ASSEM", "COBOL", "JAVA", "PASCAL", "PLI"}), 
					description = "Specifies the language interface of the program for a GPSB, or defines a DOPT(Y) program as using the Java™ language.\n" + 
							"In order to define a DOPT program using the Java language, the program must be defined with DOPT(Y) and LANG(JAVA). DOPT PSBs are not loaded at IMS restart, they are loaded every time the program is scheduled. When the program is scheduled for the first time, IMS does not know the language until after the program is scheduled in a region and the PSB is loaded. Unless LANG(JAVA) is defined for the DOPT(Y) program, the program is incorrectly scheduled in a non-Java region.")
			@QueryParam("lang") 
			String lang,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), 
						description = "Specifies the resident option. The RESIDENT(N) option takes effect right away. The RESIDENT(Y) option takes effect at the next restart, unless an error is encountered such as no PSB in ACBLIB for the program, or if the program was created as RESIDENT(Y) after the checkpoint from which this IMS is performing emergency restart")
			@QueryParam("resident") 
			String resident,

			@Parameter(schema = @Schema(allowableValues = {"PARALLEL", "SERIAL"}),
						description = "Specifies whether this program can be scheduled into more than one message region or batch message region simultaneously.")
			@QueryParam("schdtype") 
			String schdtype,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}),
						description = "Specifies whether transaction level statistics should be logged. The value specified has meaning only if the program is a JBP or a non-message driven BMP. If Y is specified, transaction level statistics are written to the log in a X'56FA' log record.")
			@QueryParam("transtat") 
			String transtat,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);

		CreatePgm pgm = new CreatePgm();
		if (names != null) {
			List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
			pgm.getNAME().addAll(nameList);
		}

		CreatePgm.SET set = new CreatePgm.SET();
		if (bmptype != null) {
			set.setBMPTYPE(CreatePgm.SET.BmptypeOptions.fromValue(bmptype));
		}
		if (dopt != null) {
			set.setDOPT(CreatePgm.SET.DoptOptions.fromValue(dopt));
		}
		if (fp != null) {
			set.setFP(CreatePgm.SET.FpOptions.fromValue(fp));
		}
		if (gpsb != null) {
			set.setGPSB(CreatePgm.SET.GpsbOptions.fromValue(gpsb));
		}
		if (lang != null) {
			set.setLANG(CreatePgm.SET.LangOptions.fromValue(lang));
		}
		if (resident != null) {
			set.setRESIDENT(CreatePgm.SET.ResidentOptions.fromValue(resident));
		}
		if (schdtype != null) {
			set.setSCHDTYPE(CreatePgm.SET.SchdtypeOptions.fromValue(schdtype));
		} 
		if (transtat != null) {
			set.setTRANSTAT(CreatePgm.SET.TranstatOptions.fromValue(transtat));
		}

		pgm.setSET(set);

		Type2Command type2Command = new Type2Command();
		type2Command.setCreatePgm(pgm);
		type2Command.setVerb(Type2Command.VerbOptions.CREATE); 
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

	}





}