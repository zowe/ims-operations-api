package rs.services;

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
import commands.query.tran.QueryTran;
import commands.type2.Type2Command;
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
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;
import zowe.mc.exceptions.RestException;
import zowe.mc.servlet.OMServlet;


/**
 * Restful interface for IMS commands pertaining to transaction resources
 * @author jerryli
 *
 */
@Stateless
@Path("/tran")
@Tag(name = "Transaction")
@CheckHeader
public class Tran {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Tran.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns data from a 'QUERY TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response query(
			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names, 

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(type = "integer")))
			@QueryParam("class") 
			List<Integer> clazz,

			@Parameter(style = ParameterStyle.FORM, 
				array=@ArraySchema(schema = @Schema(allowableValues = {"LT", "LE", "GT", "GE", "EQ", "NE"})))
			@QueryParam("qcntcomp")
			String qcntcomp,

			@Parameter()
			@QueryParam("qcntval")
			Integer qcntval,

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(style = ParameterStyle.FORM, 
					array=@ArraySchema(schema = @Schema(allowableValues = 
					{"AFFIN", "BAL", "CONV", "CPIC", "DYN", "IOPREV", "LCK", "NOTINIT", "QERR", "QSTP", "SUSPEND", 
							"STOQ", "STOSCHD", "TRACE", "USTO"})))
			@QueryParam("status")
			String status,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(allowableValues = {"E", "N", "P"}))
			@QueryParam("fp")
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("remote")
			String remote,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("resp")
			String resp,

			@Parameter(style = ParameterStyle.FORM, 
					array=@ArraySchema(schema = @Schema(allowableValues = 
					{"AFFIN", "ALL", "AOCMD", "CLASS", "CMTMODE", "CONV", "CPRI", "DCLWA", "DEFN", "DEFNTYPE", "DIRROUTE", 
							"EDITRTN", "EDITUC", "EMHBSZ", "EXPRTIME", "FP", "GLOBAL", "IMSID", "INQ", "LCT", "LOCAL", 
							"LPRI", "MAXRGN", "MODEL", "MSGTYPE", "MSNAME", "NPRI", "PARLIM", "PGM", "PLCT", "PLCTTIME",
							"PSB", "QCNT", "RECOVER", "REMOTE", "RESP", "RGC", "SEGNO", "SEGSZ", "SERIAL", "SPASZ", "SPATRUNC", 
							"STATUS", "TIMESTAMP", "TRANSTAT", "WFI", "WORK", "EXPORTNEEDED"})))
			@QueryParam("show")
			String show,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		QueryTran tran = new QueryTran();

		if (names != null) {
			List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
			tran.getNAME().addAll(nameList);
		}


		if (qcntcomp != null && qcntval != null) {
			QueryTran.QCNT qcnt = new QueryTran.QCNT();
			qcnt.setQCNTComp(QueryTran.QCNT.QcntComp.fromValue(qcntcomp));
			qcnt.setQCNTValue(qcntval);
			tran.setQCNT(qcnt);
		}
		if(clazz != null) {
			tran.getCLASS().addAll(clazz);
		}

		Type2Command type2Command = new Type2Command();
		type2Command.setQueryTran(tran);
		type2Command.setVerb(Type2Command.VerbOptions.QUERY); 
		type2Command.setResource(Type2Command.ResourceOptions.TRAN);

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
	@Operation(summary = "Returns data from a 'START TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response start(
			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			List<String> name,

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema=@Schema(type="string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema=@Schema(type="integer")))
			@QueryParam("class") 
			List<Integer> clazz,

			@Parameter(schema = @Schema(type = "boolean"))
			@QueryParam("affinity") 
			boolean affinity,

			@Parameter(schema = @Schema(type = "boolean"))
			@QueryParam("all") 
			boolean all,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

		return Response.ok().build();
	}

	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns data from a 'CREATE TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request"),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response create(
			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			List<String> name,

			@Parameter(style = ParameterStyle.FORM, array=@ArraySchema(schema=@Schema(type="string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter()
			@QueryParam("desc")
			String desc,

			@Parameter()
			@QueryParam("rsc")
			String rsc,

			@Parameter(schema = @Schema(allowableValues = {"N", "CMD", "TRAN", "Y", "-1"}))
			@QueryParam("aocmd") 
			String aocmd,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "999"))
			@QueryParam("class")
			Integer clazz,

			@Parameter(schema = @Schema(allowableValues = {"SNGL", "MULT"}))
			@QueryParam("cmtmode")
			String cmtmode,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("dclwa")
			String dclwa,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("dirroute")
			String dirroute,

			@Parameter()
			@QueryParam("editrtn")
			String editrtn,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("edituc")
			String edituc,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "30720"))
			@QueryParam("emhbsz")
			Integer emhbsz,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"))
			@QueryParam("exprtime")
			Integer exprtime,

			@Parameter(schema = @Schema(allowableValues = {"E", "N", "P"}))
			@QueryParam("fp")
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("inq")
			String inq,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "65535"))
			@QueryParam("lct")
			Integer lct,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"))
			@QueryParam("lpri")
			Integer lpri,

			@Parameter(schema = @Schema(type = "integer", minimum = "0"))
			@QueryParam("maxrgn")
			Integer maxrgn,

			@Parameter(schema = @Schema(allowableValues = {"MULTSEG", "SNGLSEG"}))
			@QueryParam("msgtype")
			String msgtype,

			@Parameter()
			@QueryParam("msname")
			String msname,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"))
			@QueryParam("npri")
			Integer npri,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"))
			@QueryParam("parlim")
			Integer parlim,

			@Parameter()
			@QueryParam("pgm")
			String pgm,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"))
			@QueryParam("plct")
			Integer plct,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "6553500"))
			@QueryParam("plcttime")
			Integer plcttime,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("recover")
			String recover,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("remote")
			String remote,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("resp")
			String resp,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"))
			@QueryParam("segno")
			Integer segno,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"))
			@QueryParam("segsz")
			Integer segsz,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("serial")
			String serial,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"))
			@QueryParam("sidl")
			Integer sidl,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"))
			@QueryParam("sidr")
			Integer sidr,

			@Parameter(schema = @Schema(type = "integer", minimum = "16", maximum = "32767"))
			@QueryParam("spasz")
			Integer spasz,

			@Parameter(schema = @Schema(allowableValues = {"S", "R"}))
			@QueryParam("spatrunc")
			String spatrunc,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("transtat")
			String transtat,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("wfi")
			String wfi,


			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}


}