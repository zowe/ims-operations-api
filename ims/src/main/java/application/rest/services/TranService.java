/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotations.CheckHeader;
import application.rest.OMServlet;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import application.rest.responses.tran.query.QueryTransactionOutput;
import application.rest.responses.tran.start.StartTransactionOutput;
import application.rest.responses.tran.update.UpdateTransactionOutput;
import commands.create.tran.CreateTran;
import commands.delete.tran.DeleteTran;
import commands.query.tran.QueryTran;
import commands.query.tran.QueryTran.ConvOptions;
import commands.query.tran.QueryTran.FpOptions;
import commands.query.tran.QueryTran.RemoteOptions;
import commands.query.tran.QueryTran.RespOptions;
import commands.type2.Type2Command;
import commands.update.tran.UpdateTran;
import commands.update.tran.UpdateTran.ScopeOptions;
import exceptions.RestException;
import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.Hidden;
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
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;


/**
 * Restful interface for IMS commands pertaining to transaction resources
 * @author jerryli
 *
 */
@Stateless
@Path("/{plex}/transaction")
@Tag(name = "Transaction")
@CheckHeader
public class TranService {


	OMServlet omServlet = new OMServlet();

	private static final Logger logger = LoggerFactory.getLogger(TranService.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId= "querytran", summary = "Query information about IMS transactions across IMSplex using 'QUERY TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = QueryTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response query(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names, 

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema = @Schema(type = "integer")))
			@QueryParam("class") 
			List<Integer> clazz,

			@Parameter(schema = @Schema(allowableValues = {"LT", "LE", "GT", "GE", "EQ", "NE"}))
			@QueryParam("qcntcomp")
			String qcntcomp,

			@Parameter()
			@QueryParam("qcntval")
			Integer qcntval,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type = "string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,
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
			@QueryParam("attributes")
			String show,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,

			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,

			@Context 
			UriInfo uriInfo) {

		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setHostname(hostname);
		mcSpec.setPort(Integer.parseInt(port));
		mcSpec.setImsPlexName(plex);
		QueryTran tran = new QueryTran();

		try {

			if (names != null) {
				List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
				tran.getNAME().addAll(nameList);
			}

			if(clazz != null) {
				tran.getCLASS().addAll(clazz);
			}
			if (qcntcomp != null && qcntval != null) {
				QueryTran.QCNT qcnt = new QueryTran.QCNT();
				qcnt.setQCNTComp(QueryTran.QCNT.QcntComp.fromValue(qcntcomp));
				qcnt.setQCNTValue(qcntval);
				tran.setQCNT(qcnt);
			}
			ArrayList<QueryTran.StatusOptions> statusOptions = new ArrayList();
			if (status != null) {
				List<String> statusList = Arrays.asList(status.split("\\s*,\\s*"));
				for (String s : statusList) {
					statusOptions.add(QueryTran.StatusOptions.fromValue(s));
				}
				tran.getSTATUS().addAll(statusOptions);
			}
			if (conv != null) {
				tran.setCONV(ConvOptions.fromValue(conv));
			}
			if (fp != null) {
				tran.setFP(FpOptions.fromValue(fp));
			}
			if (remote != null) {
				tran.setREMOTE(RemoteOptions.fromValue(remote));
			}
			if (resp != null) {
				tran.setRESP(RespOptions.fromValue(resp));
			}

			ArrayList<QueryTran.ShowOptions> showOptions = new ArrayList();
			if (show != null) {
				List<String> showList = Arrays.asList(show.split("\\s*,\\s*"));
				for (String s : showList) {
					showOptions.add(QueryTran.ShowOptions.fromValue(s));
				}
				tran.getSHOW().addAll(showOptions);
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

			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);
			logger.debug("IMS Command Successfully Submitted. Check Return Code.");
			return Response.ok(result).build();
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} catch (IllegalArgumentException e) {
			RestException r = new RestException(e.getMessage());
			JSONObject rJSON = new JSONObject();
			rJSON.put("error", "Invalid Parameter Value, check command and log");
			rJSON.put("uri", uriInfo.getRequestUri().toString());
			r.setResponse(rJSON);
			logger.debug("Invalid Parameter Value " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(r.getResponse()).build();
		}
	}


	@Path("/start")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Hidden
	@Operation(operationId= "starttran", summary = "Returns data from a 'START TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = StartTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response start(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema=@Schema(type="string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema=@Schema(type="integer")))
			@QueryParam("class") 
			List<Integer> clazz,

			@Parameter(schema = @Schema(type = "boolean"))
			@QueryParam("affinity") 
			boolean affinity,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,

			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,

			@Context 
			UriInfo uriInfo) {

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);
			StringBuilder sb = new StringBuilder("CMD((STA TRAN");
			if (names != null) {
				if (names.equalsIgnoreCase("ALL")){
					sb.append(" ALL");
					if (clazz != null) {
						sb.append(" CLASS " + clazz);
					}
				}
				else {
					sb.append(" " + names);
					if (affinity) {
						sb.append(" AFFINITY");
					}
				}
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
			result = omServlet.executeImsCommand(sb.toString(), mcSpec);
			logger.debug("IMS Command Successfully Submitted. Check Return Code.");
			return Response.ok(result).build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} catch (IllegalArgumentException e) {
			RestException r = new RestException(e.getMessage());
			JSONObject rJSON = new JSONObject();
			rJSON.put("error", "Invalid Parameter Value, check command and log");
			rJSON.put("uri", uriInfo.getRequestUri().toString());
			r.setResponse(rJSON);
			logger.debug("Invalid Parameter Value " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(r.getResponse()).build();
		}


	}

	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId ="createtran", summary = "Create an IMS transaction code that associates an application program resource (PGM) to be scheduled for execution in an IMS message processing region using 'CREATE TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = CreateTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response create(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema=@Schema(type="string")))
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

			@Parameter(required = true)
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

			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,

			@Context 
			UriInfo uriInfo) {

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);

			CreateTran tran = new CreateTran();
			if (names != null) {
				List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
				tran.getNAME().addAll(nameList);
			}

			boolean isLike = false;
			CreateTran.LIKE like = new CreateTran.LIKE();
			if (desc != null) {
				like.setDESC(desc);
				isLike = true;
			}
			if (rsc != null) {
				like.setRSC(rsc);
				isLike = true;
			}
			if (isLike) {
				tran.setLIKE(like);
			}

			boolean isSet = false;
			CreateTran.SET set = new CreateTran.SET();
			if (aocmd != null) {
				set.setAOCMD(CreateTran.SET.AocmdOptions.fromValue(aocmd));
				isSet = true;
			}
			if (clazz != null) {
				set.setCLASS(clazz);
				isSet = true;
			}
			if (cmtmode != null) {
				set.setCMTMODE(CreateTran.SET.CmtmodeOptions.fromValue(cmtmode));
				isSet = true;
			}
			if (conv != null) {
				set.setCONV(CreateTran.SET.ConvOptions.fromValue(conv));
				isSet = true;
			}
			if (dclwa != null) {
				set.setDCLWA(CreateTran.SET.DclwaOptions.fromValue(dclwa));
				isSet = true;
			}
			if (dirroute != null) {
				set.setDIRROUTE(CreateTran.SET.DirrouteOptions.fromValue(dirroute));
				isSet = true;
			}
			if (editrtn != null) {
				set.setEDITRTN(editrtn);
				isSet = true;
			}
			if (edituc != null) {
				set.setEDITUC(CreateTran.SET.EditucOptions.fromValue(edituc));
				isSet = true;
			}
			if (exprtime != null) {
				set.setEXPRTIME(exprtime);
				isSet = true;
			}
			if (fp != null) {
				set.setFP(CreateTran.SET.FpOptions.fromValue(fp));
				isSet = true;
			}
			if (inq != null) {
				set.setINQ(CreateTran.SET.InqOptions.fromValue(inq));
				isSet = true;
			}
			if (lct != null) {
				set.setLCT(lct);
				isSet = true;
			}
			if (lpri != null) {
				set.setLPRI(lpri);
				isSet = true;
			}
			if (maxrgn != null) {
				set.setMAXRGN(maxrgn);
				isSet = true;
			}
			if (msgtype != null) {
				set.setMSGTYPE(CreateTran.SET.MsgtypeOptions.fromValue(msgtype));
				isSet = true;
			}
			if (msname != null) {
				set.setMSNAME(msname);
				isSet = true;
			}
			if (npri != null) {
				set.setNPRI(npri);
				isSet = true;
			}
			if (parlim != null) {
				set.setPARLIM(parlim);
				isSet = true;
			}
			if (pgm != null) {
				set.setPGM(pgm);
				isSet = true;
			}
			if (plct != null) {
				set.setPLCT(plcttime);
				isSet = true;
			}
			if (plcttime != null) {
				set.setPLCTTIME(plcttime);
				isSet = true;
			}
			if (recover != null) {
				set.setRECOVER(CreateTran.SET.RecoverOptions.fromValue(recover));
				isSet = true;
			}
			if (remote != null) {
				set.setREMOTE(CreateTran.SET.RemoteOptions.fromValue(remote));
				isSet = true;
			}
			if (resp != null) {
				set.setRESP(CreateTran.SET.RespOptions.fromValue(resp));
				isSet = true;
			}
			if (segno != null) {
				set.setSEGNO(segno);
				isSet = true;
			}
			if (segsz != null) {
				set.setSEGSZ(segsz);
				isSet = true;
			}
			if (serial != null) {
				set.setSERIAL(CreateTran.SET.SerialOptions.fromValue(serial));
				isSet = true;
			}
			if (sidl != null) {
				set.setSIDL(sidl);
				isSet = true;
			}
			if (sidr != null) {
				set.setSIDR(sidr);
				isSet = true;
			}
			if (spasz != null) {
				set.setSPASZ(spasz);
				isSet = true;
			}
			if (spatrunc != null) {
				set.setSPATRUNC(CreateTran.SET.SpatruncOptions.fromValue(spatrunc));
				isSet = true;
			}
			if (transtat != null) {
				set.setTRANSTAT(CreateTran.SET.TranstatOptions.fromValue(transtat));
				isSet = true;
			}
			if (wfi != null) {
				set.setWFI(CreateTran.SET.WfiOptions.fromValue(wfi));
				isSet = true;
			}
			if (isSet) {
				tran.setSET(set);
			}

			Type2Command type2Command = new Type2Command();
			type2Command.setCreateTran(tran);
			type2Command.setVerb(Type2Command.VerbOptions.CREATE); 
			type2Command.setResource(Type2Command.ResourceOptions.TRAN);

			if (imsmbr != null) {
				List<String> imsmbrList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
				type2Command.getRoute().addAll(imsmbrList);
				mcSpec.getDatastores().addAll(imsmbrList);
			}

			JSONObject result = new JSONObject();

			Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();

			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);

			logger.debug("IMS Command Successfully Submitted. Check Return Code.");
			return Response.ok(result).build();

		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} catch (IllegalArgumentException e) {
			RestException r = new RestException(e.getMessage());
			JSONObject rJSON = new JSONObject();
			rJSON.put("error", "Invalid Parameter Value, check command and log");
			rJSON.put("uri", uriInfo.getRequestUri().toString());
			r.setResponse(rJSON);
			logger.debug("Invalid Parameter Value " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(r.getResponse()).build();
		}


	}

	@Path("/")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId="deletetran", summary = "Delete IMS transactions using 'DELETE TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = DeleteTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response delete(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,  array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema=@Schema(type="string")))
			@QueryParam("route") 
			String imsmbr, 

			@Parameter(schema = @Schema(allowableValues = {"ALLRSP"}), description = "Indicates that the response lines are to be returned for all resources that are processed on the command. The default action is to return response lines only for the resources that resulted in an error. It is valid only with NAME(*). ALLRSP is ignored for other NAME values.")
			@QueryParam("option") 
			String option,

			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@Parameter(in = ParameterIn.HEADER, description = "IMS Connect port number", required = true) @HeaderParam("port") String port,

			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,

			@Context 
			UriInfo uriInfo) {

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);

			DeleteTran tran = new DeleteTran();
			if (names != null) {
				List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
				tran.getNAME().addAll(nameList);
			}

			if (option != null) {
				tran.setOPTION(DeleteTran.OptionOptions.fromValue(option));
			}

			Type2Command type2Command = new Type2Command();
			type2Command.setDeleteTran(tran);
			type2Command.setVerb(Type2Command.VerbOptions.DELETE); 
			type2Command.setResource(Type2Command.ResourceOptions.TRAN);

			if (imsmbr != null) {
				List<String> imsmbrList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
				type2Command.getRoute().addAll(imsmbrList);
				mcSpec.getDatastores().addAll(imsmbrList);
			}

			JSONObject result = new JSONObject();

			Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();

			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);

			logger.debug("IMS Command Successfully Submitted. Check Return Code.");
			return Response.ok(result).build();
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		} catch (IllegalArgumentException e) {
			RestException r = new RestException(e.getMessage());
			JSONObject rJSON = new JSONObject();
			rJSON.put("error", "Invalid Parameter Value, check command and log");
			rJSON.put("uri", uriInfo.getRequestUri().toString());
			r.setResponse(rJSON);
			logger.debug("Invalid Parameter Value " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(r.getResponse()).build();
		}


	}

	@Path("/")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId="updatetran", summary = "Update, start or stop IMS transaction resources using 'UPDATE TRAN' IMS command",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = UpdateTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response update(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(maxLength = 8)))
			@QueryParam("names") 
			String names,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema=@Schema(type="string")))
			@QueryParam("route") 
			String imsmbr,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type = "integer")))
			@QueryParam("class") 
			List<Integer> clazz, 

			@Parameter(schema = @Schema(allowableValues = {"AFFIN", "ALLRSP"}), description = "ALLRSP: Indicates that the response lines are to be returned for all resources that are processed on the command. The default action is to return response lines only for the resources that resulted in an error. It is valid only with NAME(*). ALLRSP is ignored for other NAME values."
					+ " AFFIN: AFFIN is valid with START(SCHD) or STOP(SCHD).\n" + 
					"When used with START(SCHD), OPTION(AFFIN) indicates that the transaction has local affinity to the IMS™ and that an inform request should be performed to register interest in the local affinity queue.")
			@QueryParam("option") 
			String option,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"ALL", "ACTIVE"})))
			@QueryParam("scope") 
			String scope,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"Q", "SCHD", "SUSPEND", "TRACE"})))
			@QueryParam("start") 
			String start,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"Q", "SCHD", "TRACE"})))
			@QueryParam("stop") 
			String stop,

			@Parameter(schema = @Schema(allowableValues = {"N", "CMD", "TRAN", "Y"}))
			@QueryParam("aocmd") 
			String aocmd,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "999"))
			@QueryParam("setClass")
			Integer setClazz,

			@Parameter(schema = @Schema(allowableValues = {"SNGL", "MULT"}))
			@QueryParam("cmtmode")
			String cmtmode,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}))
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"))
			@QueryParam("cpri")
			Integer cpri,

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

			@Parameter(schema = @Schema(allowableValues = {"ON", "OFF"}))
			@QueryParam("lock") 
			String lock,

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

			@Parameter(in = ParameterIn.PATH)
			@PathParam("plex") 
			String plex,

			@Context 
			UriInfo uriInfo
			) {

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);

			UpdateTran tran = new UpdateTran();
			if (names != null) {
				List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
				tran.getNAME().addAll(nameList);
			}

			ArrayList<UpdateTran.StartOptions> startOptions = new ArrayList();
			if (start != null) {
				List<String> startList = Arrays.asList(start.split("\\s*,\\s*"));
				for (String s : startList) {
					startOptions.add(UpdateTran.StartOptions.fromValue(s));
				}
				tran.getSTART().addAll(startOptions);
			}
			ArrayList<UpdateTran.StopOptions> stopOptions = new ArrayList();
			if (stop != null) {
				List<String> stopList = Arrays.asList(stop.split("\\s*,\\s*"));
				for (String s : stopList) {
					stopOptions.add(UpdateTran.StopOptions.fromValue(s));
				}
				tran.getSTOP().addAll(stopOptions);
			}

			if (clazz != null) {
				tran.getCLASS().addAll(clazz);
			}

			ArrayList<UpdateTran.OptionOptions> optionOptions = new ArrayList();
			if (option != null) {
				List<String> optionList = Arrays.asList(option.split("\\s*,\\s*"));
				for (String s : optionList) {
					optionOptions.add(UpdateTran.OptionOptions.fromValue(s));
				}
				tran.getOPTION().addAll(optionOptions);
			}

			if (scope != null) {
				tran.setSCOPE(ScopeOptions.fromValue(scope));
			}

			boolean isSet = false;
			UpdateTran.SET set = new UpdateTran.SET();
			if (aocmd != null) {
				set.setAOCMD(UpdateTran.SET.AocmdOptions.fromValue(aocmd));
				isSet = true;
			}
			if (clazz != null) {
				set.setCLASS(setClazz);
				isSet = true;
			}
			if (cmtmode != null) {
				set.setCMTMODE(UpdateTran.SET.CmtmodeOptions.fromValue(cmtmode));
				isSet = true;
			}
			if (conv != null) {
				set.setCONV(UpdateTran.SET.ConvOptions.fromValue(conv));
				isSet = true;
			}
			if (dclwa != null) {
				set.setDCLWA(UpdateTran.SET.DclwaOptions.fromValue(dclwa));
				isSet = true;
			}
			if (dirroute != null) {
				set.setDIRROUTE(UpdateTran.SET.DirrouteOptions.fromValue(dirroute));
				isSet = true;
			}
			if (editrtn != null) {
				set.setEDITRTN(editrtn);
				isSet = true;
			}
			if (edituc != null) {
				set.setEDITUC(UpdateTran.SET.EditucOptions.fromValue(edituc));
				isSet = true;
			}
			if (exprtime != null) {
				set.setEXPRTIME(exprtime);
				isSet = true;
			}
			if (fp != null) {
				set.setFP(UpdateTran.SET.FpOptions.fromValue(fp));
				isSet = true;
			}
			if (inq != null) {
				set.setINQ(UpdateTran.SET.InqOptions.fromValue(inq));
				isSet = true;
			}
			if (lct != null) {
				set.setLCT(lct);
				isSet = true;
			}
			if (lpri != null) {
				set.setLPRI(lpri);
				isSet = true;
			}
			if (lock != null) {
				set.setLOCK(UpdateTran.SET.LockOptions.fromValue(lock));
				isSet=true;
			}
			if (maxrgn != null) {
				set.setMAXRGN(maxrgn);
				isSet = true;
			}
			if (msgtype != null) {
				set.setMSGTYPE(UpdateTran.SET.MsgtypeOptions.fromValue(msgtype));
				isSet = true;
			}
			if (msname != null) {
				set.setMSNAME(msname);
				isSet = true;
			}
			if (npri != null) {
				set.setNPRI(npri);
				isSet = true;
			}
			if (parlim != null) {
				set.setPARLIM(parlim);
				isSet = true;
			}
			if (pgm != null) {
				set.setPGM(pgm);
				isSet = true;
			}
			if (plct != null) {
				set.setPLCT(plcttime);
				isSet = true;
			}
			if (plcttime != null) {
				set.setPLCTTIME(plcttime);
				isSet = true;
			}
			if (recover != null) {
				set.setRECOVER(UpdateTran.SET.RecoverOptions.fromValue(recover));
				isSet = true;
			}
			if (remote != null) {
				set.setREMOTE(UpdateTran.SET.RemoteOptions.fromValue(remote));
				isSet = true;
			}
			if (resp != null) {
				set.setRESP(UpdateTran.SET.RespOptions.fromValue(resp));
				isSet = true;
			}
			if (segno != null) {
				set.setSEGNO(segno);
				isSet = true;
			}
			if (segsz != null) {
				set.setSEGSZ(segsz);
				isSet = true;
			}
			if (serial != null) {
				set.setSERIAL(UpdateTran.SET.SerialOptions.fromValue(serial));
				isSet = true;
			}
			if (sidl != null) {
				set.setSIDL(sidl);
				isSet = true;
			}
			if (sidr != null) {
				set.setSIDR(sidr);
				isSet = true;
			}
			if (spasz != null) {
				set.setSPASZ(spasz);
				isSet = true;
			}
			if (spatrunc != null) {
				set.setSPATRUNC(UpdateTran.SET.SpatruncOptions.fromValue(spatrunc));
				isSet = true;
			}
			if (transtat != null) {
				set.setTRANSTAT(UpdateTran.SET.TranstatOptions.fromValue(transtat));
				isSet = true;
			}
			if (wfi != null) {
				set.setWFI(UpdateTran.SET.WfiOptions.fromValue(wfi));
				isSet = true;
			}
			if (isSet) {
				tran.setSET(set);
			}

			Type2Command type2Command = new Type2Command();
			type2Command.setUpdateTran(tran);
			type2Command.setVerb(Type2Command.VerbOptions.UPDATE); 
			type2Command.setResource(Type2Command.ResourceOptions.TRAN);

			if (imsmbr != null) {
				List<String> imsmbrList = Arrays.asList(imsmbr.split("\\s*,\\s*"));
				type2Command.getRoute().addAll(imsmbrList);
				mcSpec.getDatastores().addAll(imsmbrList);
			}

			JSONObject result = new JSONObject();

			Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();

			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
			result = omServlet.executeImsCommand(cmd, mcSpec);
			logger.debug("IMS Command Successfully Submitted. Check Return Code.");
			return Response.ok(result).build();
		} catch (OmCommandGenerationException e1) {
			logger.error("Unable to generate IMS command", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		} catch (RestException e) {
			logger.debug("OM returned non-zero return code: " + e.getResponse().toString());
			return Response.status(Status.BAD_REQUEST).entity(e.getResponse()).build();
		}  catch (IllegalArgumentException e) {
			RestException r = new RestException(e.getMessage());
			JSONObject rJSON = new JSONObject();
			rJSON.put("error", "Invalid Parameter Value, check command and log");
			rJSON.put("uri", uriInfo.getRequestUri().toString());
			r.setResponse(rJSON);
			logger.debug("Invalid Parameter Value " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(r.getResponse()).build();
		}


	}
}