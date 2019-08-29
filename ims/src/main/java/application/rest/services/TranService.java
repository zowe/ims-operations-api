
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import commands.query.tran.QueryTran.ShowOptions;
import commands.query.tran.QueryTran.StatusOptions;
import commands.type2.Type2Command;
import commands.update.tran.UpdateTran;
import commands.update.tran.UpdateTran.OptionOptions;
import commands.update.tran.UpdateTran.ScopeOptions;
import commands.update.tran.UpdateTran.StartOptions;
import commands.update.tran.UpdateTran.StopOptions;
import exceptions.RestException;
import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
@SecurityScheme(name = "Basic Auth", type = SecuritySchemeType.HTTP, scheme = "basic", in = SecuritySchemeIn.HEADER)
@CheckHeader
public class TranService {


	OMServlet omServlet = new OMServlet();

	private static final Logger logger = LoggerFactory.getLogger(TranService.class);

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({"ims-admin", "tran-user", "get-user"})
	@Operation(operationId= "querytran", summary = "Query information about IMS transactions across an IMSplex by using the 'QUERY TRAN' IMS command. For more information on each parameter, see the documentation for the 'QUERY TRAN' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = QueryTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response query(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema = @Schema(maxLength = 8)), description="Specifies the 1-8 character name of the transaction. Wildcards can be specified. The parameter is repeatable. If the value specified for this parameter is a specific or wildcard name, responses are returned for all the resource names that are processed.")
			@QueryParam("name") 
			String names, 

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,array=@ArraySchema(schema = @Schema(type = "integer")), description="Displays transactions that possess at least one of the specified classes.")
			@QueryParam("class") 
			List<Integer> clazz,

			@Parameter(schema = @Schema(allowableValues = {"LT", "LE", "GT", "GE", "EQ", "NE"}), description="Selects transactions that have a queue count less than (LT), less than or equal to (LE), greater than (GT), greater than or equal to (GE), equal to (EQ), or not equal to (NE) the value that is specified for the 'qcntval' parameter.")
			@QueryParam("qcntcomp")
			String qcntcomp,

			@Parameter(description="Specifies the queue count number that is used to display transactions. In the 'qcntcomp' parameter, you select whether the transactions that are displayed have a queue count that is less than (LT), less than or equal to (LE), greater than (GT), greater than or equal to (GE), equal to (EQ), or not equal to (NE) the number that you specify for this parameter.  If LT is specified for the 'qcntcomp' parameter, the number that you specify for this parameter cannot be 1.")
			@QueryParam("qcntval")
			Integer qcntval,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,
			array=@ArraySchema(schema = @Schema(allowableValues = 
		{"AFFIN", "BAL", "CONV", "CPIC", "DYN", "IOPREV", "LCK", "NOTINIT", "QERR", "QSTP", "SUSPEND", 
				"STOQ", "STOSCHD", "TRACE", "USTO"})), description="Selects transactions for display that possess at least one of the specified transaction statuses.")
			@QueryParam("status")
			String status,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description=" *Specifies the conversation option.*\n" + 
					"\n" + 
					"   *N*\n" + 
					"       *The transaction is not conversational.*\n" + 
					"   *Y*\n" + 
					"       *The transaction is conversational.*")
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(allowableValues = {"E", "N", "P"}), description="Selects transactions for display that possess the Fast Path option specified. If more than one FP option is specified, selects transactions for display that possess at least one of the Fast Path options specified.")
			@QueryParam("fp")
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Selects transactions for display that possess the remote option specified.")
			@QueryParam("remote")
			String remote,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Selects transactions for display that possess the response mode option specified.")
			@QueryParam("resp")
			String resp,

			@Parameter(style = ParameterStyle.FORM, 
			array=@ArraySchema(schema = @Schema(allowableValues = 
		{"AFFIN", "ALL", "AOCMD", "CLASS", "CMTMODE", "CONV", "CPRI", "DCLWA", "DEFN", "DEFNTYPE", "DIRROUTE", 
				"EDITRTN", "EDITUC", "EMHBSZ", "EXPRTIME", "FP", "GLOBAL", "IMSID", "INQ", "LCT", "LOCAL", 
				"LPRI", "MAXRGN", "MODEL", "MSGTYPE", "MSNAME", "NPRI", "PARLIM", "PGM", "PLCT", "PLCTTIME",
				"PSB", "QCNT", "RECOVER", "REMOTE", "RESP", "RGC", "SEGNO", "SEGSZ", "SERIAL", "SPASZ", "SPATRUNC", 
				"STATUS", "TIMESTAMP", "TRANSTAT", "WFI", "WORK", "EXPORTNEEDED"})), description="Specifies the transaction output fields to be returned. The transaction name is always returned along with the name of the IMS that created the output and the completion code. If this parameter is not specified, only the transaction names are returned if the QCNT, CLASS, or STATUS filter is also not specified. The only value that is supported for this parameter when the QCNT parameter is specified is the AFFIN value. No other SHOW options are supported with the QCNT() filter because of performance reasons.")
			@QueryParam("attributes")
			String show,

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
		
		if (username != null && password != null) {
			mcSpec.setRacfUsername(username);
			mcSpec.setRacfPassword(password);
			mcSpec.setRacfEnabled(true);
		}
		
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
			ArrayList<QueryTran.StatusOptions> statusOptions = new ArrayList<StatusOptions>();
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

			ArrayList<QueryTran.ShowOptions> showOptions = new ArrayList<ShowOptions>();
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
			@QueryParam("name") 
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
	@RolesAllowed({"ims-admin", "tran-user", "post-user"})
	@Operation(operationId ="createtran", summary = "Create an IMS transaction code that associates an application program resource (PGM) to be scheduled for execution in an IMS message processing region by using the 'CREATE TRAN' IMS command. For more information on each parameter, see the documentation for the 'CREATE TRAN' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = CreateTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response create(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(maxLength = 8)), description="Specifies the 1-8 character name of the transaction. Wildcards can be specified. The parameter is repeatable. If the value specified for this parameter is a specific or wildcard name, responses are returned for all the resource names that are processed.")
			@QueryParam("name") 
			String names,

			@Parameter(description = "Specifies the name of the descriptor to use as a model to define this resource.")
			@QueryParam("desc")
			String desc,

			@Parameter(description = "Specifies the name of the resource to use as a model to define this resource.")
			@QueryParam("rsc")
			String rsc,

			@Parameter(schema = @Schema(allowableValues = {"N", "CMD", "TRAN", "Y", "-1"}), description="Specifies the AOI option that you want to change, which indicates whether the transaction can issue the type-1 AOI CMD call or the type-2 AOI ICMD call.")
			@QueryParam("aocmd") 
			String aocmd,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "999"), description="Selects the transactions associated with the specified class or classes to be updated.")
			@QueryParam("class")
			Integer clazz,

			@Parameter(schema = @Schema(allowableValues = {"SNGL", "MULT"}), description="Specifies when database updates and non-express output messages are committed. This parameter affects emergency restart.")
			@QueryParam("cmtmode")
			String cmtmode,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description=" *Specifies the conversation option.*\n" + 
					"\n" + 
					"   *N*\n" + 
					"       *The transaction is not conversational.*\n" + 
					"   *Y*\n" + 
					"       *The transaction is conversational.*")
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the log write-ahead option.")
			@QueryParam("dclwa")
			String dclwa,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the MSC directed routing option.")
			@QueryParam("dirroute")
			String dirroute,

			@Parameter(description="Specifies the 1- to 8-character name of your transaction input edit routine that edits messages before the program receives the message. This name must begin with an alphabetic character. The specified edit routine (load module) must reside on the USERLIB data set before IMS system definition stage 2 execution. This routine cannot be the same one defined by the system definition TYPE EDIT= parameter.")
			@QueryParam("editrtn")
			String editrtn,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the edit to uppercase option.")
			@QueryParam("edituc")
			String edituc,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "30720"), description="Specifies the EMH buffer size required to run the Fast Path transaction. This overrides the EMHL execution parameter. If EMHBSZ is not specified, the EMHL execution parameter value is used. The value can be a number from 0 to 30 720.")
			@QueryParam("emhbsz")
			Integer emhbsz,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the elapsed time in seconds that IMS can use to cancel the input transaction. The value can be a number, in seconds, which can range from 0 to 65535. The default is 0, which means that no expiration time is set for this transaction.")
			@QueryParam("exprtime")
			Integer exprtime,

			@Parameter(schema = @Schema(allowableValues = {"E", "N", "P"}), description="Specifies the Fast Path option. E: The transaction is processed exclusively as Fast Path. The program must be defined as Fast Path exclusive. N: The transaction is not a candidate for Fast Path processing. The program must be defined as not Fast Path. P: The transaction is a potential candidate for Fast Path processing. Fast Path-potential transactions must be able to run under two applications: a Fast Path exclusive application and a non-Fast Path application.")
			@QueryParam("fp")
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the inquiry option.")
			@QueryParam("inq")
			String inq,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "65535"), description="Specifies the limit count. This is the number that, when compared to the number of input transactions queued and waiting to be processed, determines whether the normal or limit priority value is assigned to this transaction. The value can be a number from 1 to 65535. The default is 65535.")
			@QueryParam("lct")
			Integer lct,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"), description="Specifies the limit priority. This is the scheduling priority to which this transaction is raised when the number of input transactions enqueued and waiting to be processed is equal to or greater than the limit count value. The scheduling priority is an attribute used to select a transaction for scheduling. A transaction of higher priority is scheduled before a lower priority one, if they are defined with the same class. The value can be a number from 0 through 14.")
			@QueryParam("lpri")
			Integer lpri,

			@Parameter(schema = @Schema(type = "integer", minimum = "0"), description="Specifies a new value for the maximum number of regions that can be simultaneously scheduled for a given transaction. The value of this parameter must be between 0 and the number specified on the MAXPST=control region parameter.")
			@QueryParam("maxrgn")
			Integer maxrgn,

			@Parameter(schema = @Schema(allowableValues = {"MULTSEG", "SNGLSEG"}), description="Specifies the message type (single segment or multiple segment). It specifies the time at which an incoming message is considered complete and available to be routed to an application program for subsequent processing.")
			@QueryParam("msgtype")
			String msgtype,

			@Parameter(description="Specifies the one- to eight-character name of the logical link path in a multiple IMS system configuration (MSC).")
			@QueryParam("msname")
			String msname,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"), description="Specifies the normal scheduling priority. The scheduling priority is an attribute used to select a transaction for scheduling. A transaction of higher priority is scheduled before a lower priority one, if they are defined with the same class. The normal priority is assigned to the transaction as the scheduling priority when the number of input transactions enqueued and waiting to be processed is less than the limit count value. The value can be a number from 0 through 14. The default is 1.")
			@QueryParam("npri")
			Integer npri,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the parallel processing limit count. This is the maximum number of messages that can currently be queued, but not yet processed, by each active message region currently scheduled for this transaction. This is the threshold value to be used when the associated application is defined with a scheduling type of parallel. An additional region is scheduled whenever the current transaction enqueue count exceeds this parameter value multiplied by the number of regions currently scheduled for this transaction.\n" + 
					"\n" + 
					"The value can be a number from 0 to 32767 or 65535.")
			@QueryParam("parlim")
			Integer parlim,

			@Parameter(required = true, description="Specifies the name of the application program associated with the transaction.")
			@QueryParam("pgm")
			String pgm,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the processing limit count. This is maximum number of messages sent to the application program by the IMS for processing without reloading the application program. The value must be a number from 0 through 65535. ")
			@QueryParam("plct")
			Integer plct,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "6553500"), description="Specifies the processing limit count time. This is the amount of time (in hundredths of seconds) allowable to process a single transaction (or message). The number specifies the maximum CPU time allowed for each message to be processed in the message processing region.\n" + 
					"\n" + 
					"Batch Message Programs (BMPs) are not affected by this setting.\n" + 
					"\n" + 
					"The value can be a number, in hundredths of seconds, that can range from 1 to 6553500. A value of 6553500 means no time limit is placed on the application program.")
			@QueryParam("plcttime")
			Integer plcttime,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the recovery option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction should not be recovered.\n" + 
					"Y\n" + 
					"    The transaction should be recovered during an IMS emergency or normal restart.")
			@QueryParam("recover")
			String recover,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the remote option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction is not remote. The transaction is local and runs on the local system.\n" + 
					"Y\n" + 
					"    The transaction is remote. The transaction runs on a remote system.")
			@QueryParam("remote")
			String remote,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the response mode option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction is not response mode. For terminals specifying or accepting a default of OPTIONS=TRANRESP, input should not stop after this transaction is entered.\n" + 
					"Y\n" + 
					"    The transaction is response mode. The terminal from which the transaction is entered is held and prevents further input until a response is received.")
			@QueryParam("resp")
			String resp,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the segment number. This is the maximum number of application program output segments that are allowed into the message queues per Get Unique (GU) call from the application program. The value can be a number from 0 through 65535.")
			@QueryParam("segno")
			Integer segno,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the segment size. This is the maximum number of bytes allowed in any one output segment. The value can be a number from 0 through 65535.")
			@QueryParam("segsz")
			Integer segsz,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the serial option.\n" + 
					"\n" + 
					"N\n" + 
					"    Messages for the transaction are not processed serially. Message processing can be processed in parallel. Messages are placed on the suspend queue after a U3303 pseudoabend. Scheduling continues until repeated failures result in the transaction being stopped with a USTOP.\n" + 
					"Y\n" + 
					"    Messages for the transaction are processed serially. U3303 pseudoabends do not cause the message to be placed on the suspend queue but rather on the front of the transaction message queue, and the transaction is stopped with a USTOP. The USTOP of the transaction is removed when the transaction or the class is started with a /START or UPD TRAN command.")
			@QueryParam("serial")
			String serial,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"), description="Specifies the system identification (SYSID) of the local system in a multiple-IMS system (MSC) configuration. The local system is the originating system to which responses are returned. The value can be a number from 1 to 2036, if MSC is enabled, or 0, if MSC is not enabled. The local SYSID can be defined in any or all of the MSNAMEs or transactions.")
			@QueryParam("sidl")
			Integer sidl,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"), description="Specifies the system identification (SYSID) of the remote system in a multiple-IMS system (MSC) configuration. The remote system is the system on which the application program executes. The value can be a number from 1 to 2036, if MSC is enabled, or 0, if MSC is not enabled. The remote SYSID specified must also be defined for an MSNAME.")
			@QueryParam("sidr")
			Integer sidr,

			@Parameter(schema = @Schema(type = "integer", minimum = "16", maximum = "32767"), description="Specifies the scratchpad area (SPA) size, in bytes, for a conversational transaction. The value can be a number from 16 and 32767.")
			@QueryParam("spasz")
			Integer spasz,

			@Parameter(schema = @Schema(allowableValues = {"S", "R"}), description="Specifies the scratchpad area (SPA) truncation option of a conversational transaction. This defines whether the SPA data should be truncated or preserved across a program switch to a transaction that is defined with a smaller SPA.\n" + 
					"\n" + 
					"S\n" + 
					"    IMS preserves all of the data in the SPA, even when a program switch is made to a transaction that is defined with a smaller SPA. The transaction with the smaller SPA does not see the truncated data, but when the transaction switches to a transaction with a larger SPA, the truncated data is used.\n" + 
					"R\n" + 
					"    The truncated data is not preserved.")
			@QueryParam("spatrunc")
			String spatrunc,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies whether transaction level statistics should be logged for message driven programs. If Y is specified, transaction level statistics are written to the log in a X'56FA' log record.")
			@QueryParam("transtat")
			String transtat,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the wait-for input option. This attribute does not apply to Fast Path transactions, which always behave as wait-for-input transactions.")
			@QueryParam("wfi")
			String wfi,


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

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);
			
			if (username != null && password != null) {
				mcSpec.setRacfUsername(username);
				mcSpec.setRacfPassword(password);
				mcSpec.setRacfEnabled(true);
			}

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
	@RolesAllowed({"ims-admin", "tran-user", "post-user"})
	@Operation(operationId="deletetran", summary = "Delete IMS transactions by using the 'DELETE TRAN' IMS command. For more information on each parameter, see the documentation for the 'DELETE TRAN' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = DeleteTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response delete(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE,  array=@ArraySchema(schema = @Schema(maxLength = 8)), description=" Specifies the 1-8 character name of the transaction. Wildcards can be specified. The parameter is repeatable. If the value specified for this parameter is a specific or wildcard name, responses are returned for all the resource names that are processed.")
			@QueryParam("name") 
			String names,

			@Parameter(schema = @Schema(allowableValues = {"ALLRSP"}), description = "Indicates that the response lines are to be returned for all resources that are processed on the command. The default action is to return response lines only for the resources that resulted in an error. It is valid only with NAME(*). ALLRSP is ignored for other NAME values.")
			@QueryParam("option") 
			String option,

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

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);
			
			if (username != null && password != null) {
				mcSpec.setRacfUsername(username);
				mcSpec.setRacfPassword(password);
				mcSpec.setRacfEnabled(true);
			}

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
	@RolesAllowed({"ims-admin", "tran-user", "put-user"})
	@Operation(operationId="updatetran", summary = "Update, start or stop IMS transaction resources by using the 'UPDATE TRAN' IMS command. For more information on each parameter, see the documentation for the 'UPDATE TRAN' IMS command in IBM Knowledge Center.",
	responses = { @ApiResponse(content = @Content(mediaType="application/json")),
			@ApiResponse(responseCode = "200", description = "Successful Request",
			content = @Content(schema = @Schema(implementation = UpdateTransactionOutput.class))),
			@ApiResponse(responseCode = "400", description = "Request Error"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")})
	public Response update(
			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(maxLength = 8)), description="Specifies the 1-8 character name of the transaction. Wildcards can be specified. The parameter is repeatable. If the value specified for this parameter is a specific or wildcard name, responses are returned for all the resource names that are processed.")
			@QueryParam("name") 
			String names,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = @Schema(type = "integer")), description="Selects the transactions associated with the specified class or classes to be updated.")
			@QueryParam("class") 
			List<Integer> clazz, 

			@Parameter(schema = @Schema(allowableValues = {"AFFIN", "ALLRSP"}), description = "Specifies the additional functions to be performed. ALLRSP: Indicates that the response lines are to be returned for all resources that are processed on the command. The default action is to return response lines only for the resources that resulted in an error. This value is valid only if a wildcard (*) is specified on the 'name' parameter. This value is ignored for other 'name' parameter values. AFFIN: AFFIN is valid with START(SCHD) or STOP(SCHD). When used with START(SCHD), this value indicates that the transaction has local affinity to the IMS™ and that an inform request should be performed to register interest in the local affinity queue.")
			@QueryParam("option") 
			String option,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"ALL", "ACTIVE"})), description="Specifies where IMS should apply the change.")
			@QueryParam("scope") 
			String scope,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"Q", "SCHD", "SUSPEND", "TRACE"})), description="Specifies the attributes to be started.")
			@QueryParam("start") 
			String start,

			@Parameter(style = ParameterStyle.FORM, explode = Explode.FALSE, array=@ArraySchema(schema = 
			@Schema(allowableValues = {"Q", "SCHD", "TRACE"})), description="Specifies the attributes to be stopped.")
			@QueryParam("stop") 
			String stop,

			@Parameter(schema = @Schema(allowableValues = {"N", "CMD", "TRAN", "Y"}), description="Specifies the AOI option that you want to change, which indicates whether the transaction can issue the type-1 AOI CMD call or the type-2 AOI ICMD call.")
			@QueryParam("aocmd") 
			String aocmd,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "999"), description="Specifies the transaction class, which is an attribute used to select a transaction for scheduling. A transaction can be scheduled if there is a message processing region available for that class. The value can be a number from 1 to 999. This value must not exceed the value given (by specification or default) on the MAXCLAS= keyword of the IMSCTRL macro.")
			@QueryParam("setClass")
			Integer setClazz,

			@Parameter(schema = @Schema(allowableValues = {"SNGL", "MULT"}), description="Specifies when database updates and non-express output messages are committed. This parameter affects emergency restart.")
			@QueryParam("cmtmode")
			String cmtmode,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description=" *Specifies the conversation option.*\n" + 
					"\n" + 
					"   *N*\n" + 
					"       *The transaction is not conversational.*\n" + 
					"   *Y*\n" + 
					"       *The transaction is conversational.*")
			@QueryParam("conv")
			String conv,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"), description="Specifies a new value for the current priority of a transaction. The CPRI parameter is not allowed for BMP transactions, because BMP transactions should always have a priority of 0. The new CPRI value takes effect the next time the transaction is scheduled. Valid CPRI parameters are numeric values from 0 to 14.")
			@QueryParam("cpri")
			Integer cpri,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the log write-ahead option.")
			@QueryParam("dclwa")
			String dclwa,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the MSC directed routing option.")
			@QueryParam("dirroute")
			String dirroute,

			@Parameter(description="Specifies the 1- to 8-character name of your transaction input edit routine that edits messages before the program receives the message. This name must begin with an alphabetic character. The specified edit routine (load module) must reside on the USERLIB data set before IMS system definition stage 2 execution. This routine cannot be the same one defined by the system definition TYPE EDIT= parameter.")
			@QueryParam("editrtn")
			String editrtn,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the edit to uppercase option.")
			@QueryParam("edituc")
			String edituc,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "30720"), description="Specifies the EMH buffer size required to run the Fast Path transaction. This overrides the EMHL execution parameter. If EMHBSZ is not specified, the EMHL execution parameter value is used. The value can be a number from 0 to 30 720.")
			@QueryParam("emhbsz")
			Integer emhbsz,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the elapsed time in seconds that IMS can use to cancel the input transaction. The value can be a number, in seconds, which can range from 0 to 65535. The default is 0, which means that no expiration time is set for this transaction.")
			@QueryParam("exprtime")
			Integer exprtime,

			@Parameter(schema = @Schema(allowableValues = {"E", "N", "P"}), description="Specifies the Fast Path option. E: The transaction is processed exclusively as Fast Path. The program must be defined as Fast Path exclusive. N: The transaction is not a candidate for Fast Path processing. The program must be defined as not Fast Path. P: The transaction is a potential candidate for Fast Path processing. Fast Path-potential transactions must be able to run under two applications: a Fast Path exclusive application and a non-Fast Path application.")
			@QueryParam("fp")
			String fp,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the inquiry option.")
			@QueryParam("inq")
			String inq,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "65535"), description="Specifies the limit count. This is the number that, when compared to the number of input transactions queued and waiting to be processed, determines whether the normal or limit priority value is assigned to this transaction. The value can be a number from 1 to 65535. The default is 65535.")
			@QueryParam("lct")
			Integer lct,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"), description="Specifies the limit priority. This is the scheduling priority to which this transaction is raised when the number of input transactions enqueued and waiting to be processed is equal to or greater than the limit count value. The scheduling priority is an attribute used to select a transaction for scheduling. A transaction of higher priority is scheduled before a lower priority one, if they are defined with the same class. The value can be a number from 0 through 14.")
			@QueryParam("lpri")
			Integer lpri,

			@Parameter(schema = @Schema(allowableValues = {"ON", "OFF"}), description="Specifies that the LOCK status is to be set on or off.")
			@QueryParam("lock") 
			String lock,

			@Parameter(schema = @Schema(type = "integer", minimum = "0"), description="Specifies a new value for the maximum number of regions that can be simultaneously scheduled for a given transaction. The value of this parameter must be between 0 and the number specified on the MAXPST=control region parameter.")
			@QueryParam("maxrgn")
			Integer maxrgn,

			@Parameter(schema = @Schema(allowableValues = {"MULTSEG", "SNGLSEG"}), description="Specifies the message type (single segment or multiple segment). It specifies the time at which an incoming message is considered complete and available to be routed to an application program for subsequent processing.")
			@QueryParam("msgtype")
			String msgtype,

			@Parameter(description="Specifies the one- to eight-character name of the logical link path in a multiple IMS system configuration (MSC).")
			@QueryParam("msname")
			String msname,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "14"), description="Specifies the normal scheduling priority. The scheduling priority is an attribute used to select a transaction for scheduling. A transaction of higher priority is scheduled before a lower priority one, if they are defined with the same class. The normal priority is assigned to the transaction as the scheduling priority when the number of input transactions enqueued and waiting to be processed is less than the limit count value. The value can be a number from 0 through 14. The default is 1.")
			@QueryParam("npri")
			Integer npri,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the parallel processing limit count. This is the maximum number of messages that can currently be queued, but not yet processed, by each active message region currently scheduled for this transaction. This is the threshold value to be used when the associated application is defined with a scheduling type of parallel. An additional region is scheduled whenever the current transaction enqueue count exceeds this parameter value multiplied by the number of regions currently scheduled for this transaction.\n" + 
					"\n" + 
					"The value can be a number from 0 to 32767 or 65535.")
			@QueryParam("parlim")
			Integer parlim,

			@Parameter(description="Specifies the name of the application program associated with the transaction.")
			@QueryParam("pgm")
			String pgm,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the processing limit count. This is maximum number of messages sent to the application program by the IMS for processing without reloading the application program. The value must be a number from 0 through 65535.")
			@QueryParam("plct")
			Integer plct,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "6553500"), description="Specifies the processing limit count time. This is the amount of time (in hundredths of seconds) allowable to process a single transaction (or message). The number specifies the maximum CPU time allowed for each message to be processed in the message processing region.\n" + 
					"\n" + 
					"Batch Message Programs (BMPs) are not affected by this setting.\n" + 
					"\n" + 
					"The value can be a number, in hundredths of seconds, that can range from 1 to 6553500. A value of 6553500 means no time limit is placed on the application program.")
			@QueryParam("plcttime")
			Integer plcttime,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the recovery option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction should not be recovered.\n" + 
					"Y\n" + 
					"    The transaction should be recovered during an IMS emergency or normal restart. ")
			@QueryParam("recover")
			String recover,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the remote option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction is not remote. The transaction is local and runs on the local system.\n" + 
					"Y\n" + 
					"    The transaction is remote. The transaction runs on a remote system. ")
			@QueryParam("remote")
			String remote,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the response mode option.\n" + 
					"\n" + 
					"N\n" + 
					"    The transaction is not response mode. For terminals specifying or accepting a default of OPTIONS=TRANRESP, input should not stop after this transaction is entered.\n" + 
					"Y\n" + 
					"    The transaction is response mode. The terminal from which the transaction is entered is held and prevents further input until a response is received.")
			@QueryParam("resp")
			String resp,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the segment number. This is the maximum number of application program output segments that are allowed into the message queues per Get Unique (GU) call from the application program. The value can be a number from 0 through 65535.")
			@QueryParam("segno")
			Integer segno,

			@Parameter(schema = @Schema(type = "integer", minimum = "0", maximum = "65535"), description="Specifies the segment size. This is the maximum number of bytes allowed in any one output segment. The value can be a number from 0 through 65535.")
			@QueryParam("segsz")
			Integer segsz,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the serial option.\n" + 
					"\n" + 
					"N\n" + 
					"    Messages for the transaction are not processed serially. Message processing can be processed in parallel. Messages are placed on the suspend queue after a U3303 pseudoabend. Scheduling continues until repeated failures result in the transaction being stopped with a USTOP.\n" + 
					"Y\n" + 
					"    Messages for the transaction are processed serially. U3303 pseudoabends do not cause the message to be placed on the suspend queue but rather on the front of the transaction message queue, and the transaction is stopped with a USTOP. The USTOP of the transaction is removed when the transaction or the class is started with a /START or UPD TRAN command.")
			@QueryParam("serial")
			String serial,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"), description="Specifies the system identification (SYSID) of the local system in a multiple-IMS system (MSC) configuration. The local system is the originating system to which responses are returned. The value can be a number from 1 to 2036, if MSC is enabled, or 0, if MSC is not enabled. The local SYSID can be defined in any or all of the MSNAMEs or transactions.")
			@QueryParam("sidl")
			Integer sidl,

			@Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "2036"), description="Specifies the system identification (SYSID) of the remote system in a multiple-IMS system (MSC) configuration. The remote system is the system on which the application program executes. The value can be a number from 1 to 2036, if MSC is enabled, or 0, if MSC is not enabled. The remote SYSID specified must also be defined for an MSNAME.")
			@QueryParam("sidr")
			Integer sidr,

			@Parameter(schema = @Schema(type = "integer", minimum = "16", maximum = "32767"), description="Specifies the scratchpad area (SPA) size, in bytes, for a conversational transaction. The value can be a number from 16 and 32767.")
			@QueryParam("spasz")
			Integer spasz,

			@Parameter(schema = @Schema(allowableValues = {"S", "R"}), description="Specifies the scratchpad area (SPA) truncation option of a conversational transaction. This defines whether the SPA data should be truncated or preserved across a program switch to a transaction that is defined with a smaller SPA.\n" + 
					"\n" + 
					"S\n" + 
					"    IMS preserves all of the data in the SPA, even when a program switch is made to a transaction that is defined with a smaller SPA. The transaction with the smaller SPA does not see the truncated data, but when the transaction switches to a transaction with a larger SPA, the truncated data is used.\n" + 
					"R\n" + 
					"    The truncated data is not preserved.")
			@QueryParam("spatrunc")
			String spatrunc,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies whether transaction level statistics should be logged for message driven programs. If Y is specified, transaction level statistics are written to the log in a X'56FA' log record.")
			@QueryParam("transtat")
			String transtat,

			@Parameter(schema = @Schema(allowableValues = {"N", "Y"}), description="Specifies the wait-for input option. This attribute does not apply to Fast Path transactions, which always behave as wait-for-input transactions.")
			@QueryParam("wfi")
			String wfi,


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
			UriInfo uriInfo
			) {

		try {
			MCInteraction mcSpec = new MCInteraction();
			mcSpec.setHostname(hostname);
			mcSpec.setPort(Integer.parseInt(port));
			mcSpec.setImsPlexName(plex);
			
			if (username != null && password != null) {
				mcSpec.setRacfUsername(username);
				mcSpec.setRacfPassword(password);
				mcSpec.setRacfEnabled(true);
			}
			

			UpdateTran tran = new UpdateTran();
			if (names != null) {
				List<String> nameList = Arrays.asList(names.split("\\s*,\\s*"));
				tran.getNAME().addAll(nameList);
			}

			ArrayList<UpdateTran.StartOptions> startOptions = new ArrayList<StartOptions>();
			if (start != null) {
				List<String> startList = Arrays.asList(start.split("\\s*,\\s*"));
				for (String s : startList) {
					startOptions.add(UpdateTran.StartOptions.fromValue(s));
				}
				tran.getSTART().addAll(startOptions);
			}
			ArrayList<UpdateTran.StopOptions> stopOptions = new ArrayList<StopOptions>();
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

			ArrayList<UpdateTran.OptionOptions> optionOptions = new ArrayList<OptionOptions>();
			if (option != null) {
				List<String> optionList = Arrays.asList(option.split("\\s*,\\s*"));
				for(String s : optionList) {
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
			if (setClazz != null) {
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