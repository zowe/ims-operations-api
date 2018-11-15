package rs;

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
 * Restful interface for IMS commands pertaining to transaction resources
 * @author jerryli
 *
 */
@Stateless
@Path("/tran")
@Api(tags = {"Transaction"})
@CheckHeader
public class Tran {

	@EJB
	OMServlet omServlet;

	private static final Logger logger = LoggerFactory.getLogger(Tran.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from QUERY TRAN IMS command", httpMethod="GET", notes = "<br>This service submits a 'Query Tran' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful Operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Request Error"),
			@ApiResponse(code = 400, message = "Error connecting to IMS"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response query(
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("names") 
			String names, 

			@ApiParam(allowMultiple = true)
			@QueryParam("class") 
			List<Integer> clazz,

			@ApiParam(allowMultiple = false, allowableValues = "LT, LE, GT, GE, EQ, NE")
			@QueryParam("qcntcomp")
			String qcntcomp,

			@ApiParam(allowMultiple = false)
			@QueryParam("qcntval")
			Integer qcntval,

			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("route") 
			String imsmbr, 

			@ApiParam(allowMultiple = true, collectionFormat = "csv", 
			allowableValues = "AFFIN, BAL, CONV, CPIC, DYN, IOPREV, LCK, NOTINIT, QERR, QSTP, SUSPEND, STOQ, STOSCHD, TRACE, USTO")
			@QueryParam("status")
			String status,

			@ApiParam(allowMultiple = false, allowableValues = "N,Y")
			@QueryParam("conv")
			String conv,

			@ApiParam(allowMultiple=false, allowableValues = "E,N,P")
			@QueryParam("fp")
			String fp,

			@ApiParam(allowMultiple = false, allowableValues = "N,Y")
			@QueryParam("remote")
			String remote,

			@ApiParam(allowMultiple = false, allowableValues = "N,Y")
			@QueryParam("resp")
			String resp,

			@ApiParam(allowMultiple = true, collectionFormat="csv", 
			allowableValues = "AFFIN, ALL, AOCMD, CLASS, CMTMODE, CONV, CPRI, DCLWA, DEFN, DEFNTYPE, DIRROUTE, EDITRTN, EDITUC, EMHBSZ,"
					+ "EXPRTIME, FP, GLOBAL, IMSID, INQ, LCT, LOCAL, LPRI, MAXRGN, MODEL, MSGTYPE, MSNAME, NPRI, PARLIM, PGM, PLCT, PLCTTIME,"
					+ "PSB, QCNT, RECOVER, REMOTE, RESP, RGC, SEGNO, SEGSZ, SERIAL, SPASZ, SPATRUNC, STATUS, TIMESTAMP, TRANSTAT, WFI")
			@QueryParam("show1")
			String show,

			@ApiParam(allowMultiple = false, allowableValues = "WORK")
			@QueryParam("show2")
			String show2,

			@ApiParam(allowMultiple = false, allowableValues = "EXPORTNEEDED")
			@QueryParam("show3")
			String show3,

			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {

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
	@ApiOperation(produces="application/json", value = "Return data from START TRAN IMS command", httpMethod="PUT", notes = "<br>This service submits a 'Start TRAN' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Request Error"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response start(
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("names") 
			List<String> name,

			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("route") 
			String imsmbr, 

			@ApiParam(allowMultiple = true)
			@QueryParam("class") 
			List<Integer> clazz,

			@ApiParam(allowMultiple = false)
			@QueryParam("affinity") 
			boolean affinity,

			@ApiParam(allowMultiple = false)
			@QueryParam("all") 
			boolean all,

			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}

	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(produces="application/json", value = "Return data from CREATE TRAN IMS command", httpMethod="POST", notes = "<br>This service submits a 'Create TRAN' IMS command and returns the output", response = JSONObject.class)
	@ApiResponses(value = { @ApiResponse(code = 200, response = JSONObject.class, message = "Successful Operation"),
			@ApiResponse(code = 400, response = JSONObject.class, message = "Request Error"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public Response create(
			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("names") 
			List<String> names, 

			@ApiParam(allowMultiple = true, collectionFormat = "csv")
			@QueryParam("route") 
			String imsmbr, 

			@ApiParam(allowMultiple = false)
			@QueryParam("desc")
			String desc,

			@ApiParam(allowMultiple = false)
			@QueryParam("rsc")
			String rsc,

			@ApiParam(allowMultiple = false, allowableValues = "N, CMD, TRAN, Y, -1")
			@QueryParam("aocmd") 
			String aocmd,

			@ApiParam(allowMultiple = false, allowableValues = "range[1,999]")
			@QueryParam("class")
			Integer clazz,

			@ApiParam(allowMultiple = false, allowableValues = "SNGL, MULT")
			@QueryParam("cmtmode")
			String cmtmode,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("conv")
			String conv,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("dclwa")
			String dclwa,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("dirroute")
			String dirroute,

			@ApiParam(allowMultiple = false)
			@QueryParam("editrtn")
			String editrtn,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("edituc")
			String edituc,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,30720]")
			@QueryParam("emhbsz")
			Integer emhbsz,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,65535]")
			@QueryParam("exprtime")
			Integer exprtime,

			@ApiParam(allowMultiple = false, allowableValues = "E, N, P")
			@QueryParam("fp")
			String fp,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("inq")
			String inq,

			@ApiParam(allowMultiple = false, allowableValues = "range[1,65535]")
			@QueryParam("lct")
			Integer lct,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,14]")
			@QueryParam("lpri")
			Integer lpri,

			@ApiParam(allowMultiple = false, allowableValues = "range[0, infinity]")
			@QueryParam("maxrgn")
			Integer maxrgn,

			@ApiParam(allowMultiple = false, allowableValues = "MULTSEG, SNGLSEG")
			@QueryParam("msgtype")
			String msgtype,

			@ApiParam(allowMultiple = false)
			@QueryParam("msname")
			String msname,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,14]")
			@QueryParam("npri")
			Integer npri,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,65535]")
			@QueryParam("parlim")
			Integer parlim,

			@ApiParam(allowMultiple = false)
			@QueryParam("pgm")
			String pgm,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,65535]")
			@QueryParam("plct")
			Integer plct,

			@ApiParam(allowMultiple = false, allowableValues = "range[1,6553500]")
			@QueryParam("plcttime")
			Integer plcttime,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("recover")
			String recover,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("remote")
			String remote,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("resp")
			String resp,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,65535]")
			@QueryParam("segno")
			Integer segno,

			@ApiParam(allowMultiple = false, allowableValues = "range[0,65535]")
			@QueryParam("segsz")
			Integer segsz,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("serial")
			String serial,

			@ApiParam(allowMultiple = false, allowableValues = "range[1,2036]")
			@QueryParam("sidl")
			Integer sidl,

			@ApiParam(allowMultiple = false, allowableValues = "range[1,2036]")
			@QueryParam("sidr")
			Integer sidr,

			@ApiParam(allowMultiple = false, allowableValues = "range[16,32767]")
			@QueryParam("spasz")
			Integer spasz,

			@ApiParam(allowMultiple = false, allowableValues = "S, R")
			@QueryParam("spatrunc")
			String spatrunc,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("transtat")
			String transtat,

			@ApiParam(allowMultiple = false, allowableValues = "N, Y")
			@QueryParam("wfi")
			String wfi,


			@ApiParam(value = "IMS Connect host address", required = true) @HeaderParam("hostname") String hostname,
			@ApiParam(value = "IMS Connect port number", required = true) @HeaderParam("port") String port,
			@ApiParam(value = "IMS Connect plex name", required = true) @HeaderParam("plex") String plex) {


		return Response.ok().build();
	}


}