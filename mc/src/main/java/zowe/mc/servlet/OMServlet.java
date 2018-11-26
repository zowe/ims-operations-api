package zowe.mc.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.resource.cci.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import json.java.JSONArray;
import json.java.JSONObject;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import om.exception.OmException;
import om.exception.message.OM_CONNECTION;
import om.exception.message.OM_EXCEPTION;
import om.message.OmCommandErrorMbr;
import om.message.OmMessageContext;
import om.result.OmResultSet;
import om.service.CommandService;
import om.services.Om;
import zowe.mc.exceptions.RestException;

@OpenAPIDefinition(
		info = @Info(
				title = "Management Console for Zowe",
				version = "1.0.0",
				description = "Management Console for Zowe allows users to use RESTFul APIs to submit IMS commmands"),
		servers = {@Server(url = "http://localhost:9080/mc/")})
@Stateless
public class OMServlet {

	@Resource (name = "mc_cf")
	private ConnectionFactory mcCF;

	static final Logger logger = LoggerFactory.getLogger(OMServlet.class);

	//Message status sent to the client for interpretation
//	private static enum OM_MESSAGE_STATUS_TYPE {
//		ERROR("error"), 
//		WARNING("warning"), 
//		SUCCESS("success"), 
//		INFORMATION("information"), 
//		CRITICAL("critical"), 
//		ATTENTION("attention"), 
//		COMPLIANCE("compliance");
//
//		private String value;
//
//		private OM_MESSAGE_STATUS_TYPE(String val) {
//			this.value = val;
//		}
//
//		@Override
//		public String toString() {
//			return value;
//		}
//	}

	/**
	 * Method will execute an IMS Type1/2 command orginating from the command console in the E4A UI. This method is 
	 * designed for use only with the UI and must have the required param values.
	 * @param params
	 * @param session
	 * @return
	 * @throws RestException 
	 * @throws OmDatastoreException 
	 */
	public JSONObject executeImsCommand(String command, MCInteraction mcSpec) throws RestException  //Make our own custom exception
	{



		JSONObject result = new JSONObject();

		//OM message contents sent back to UI
		JSONObject message = new JSONObject();
		//JSONObject commandExecutedText = new JSONObject();

		ArrayList<String> plexImsMbrs = new ArrayList<String>();

		IconOmConnection omConnection = null;
		Om om = null;
		OmResultSet omResultSet;

		IconOmConnectionFactory IconCF = new IconOmConnectionFactory();

		try {
			omConnection = IconCF.createIconOmConnectionFromData(mcSpec);


			om = new Om(omConnection);

			//Figure out how to deal with versioning later.
			CommandService cService = om.getCommandService();

			OmResultSet plexResultSet= cService.executeImsCommand("executeImsCommand","CMD(QUERY IMSPLEX TYPE(IMS) SHOW(STATUS))");
			Properties[] response = plexResultSet.getResponseProperties();


			for (int i = 0; i<response.length; i++) {
				plexImsMbrs.add(response[i].getProperty("IMSMBR"));
			}

			//We need to proccess the command, prepare it with the PREFIX and ROUTE SUFFIX
			omResultSet= cService.executeImsCommand("executeImsCommand",command);


			//Responseproperties is the results as a map from connect api
			Properties[] dataProperties = omResultSet.getResponseProperties();
			if(dataProperties != null){
				for(Properties p : dataProperties) {
					//prop.put("resourceId", counter++);
					result.putAll(p);
					//data.add(prop);
				}
			}

			//Type of command Type1 or Type2
			//result.put("imsCommandType",  omResultSet.getOmMessageContext().getOmCommandType());

			//If there is response message data being returned
			if(omResultSet.getResponseMsgData() != null) {
				//Convert to JSONArray and add to message
				Properties[] messageData = omResultSet.getResponseMsgData();
				JSONArray msgData = new JSONArray();
				for (Properties p : messageData) {
					JSONObject responseMsgData = new JSONObject();
					responseMsgData.putAll(p);
					msgData.add(responseMsgData);
				}
				message.put("msgDataFromType2", msgData);
			}

		}catch (OmConnectionException e) {
			JSONObject omConnectionExceptionJSON = omConnectionExceptionToJSON(e);
			result.put("Messages", omConnectionExceptionJSON);
			throw new RestException("Servlet has thrown exception", result);
		} catch (OmException e) {
			JSONObject omExceptionJSON = omExceptionToJSON(e);
			result.put("Messages", omExceptionJSON);
			throw new RestException("Servlet has thrown exception", result);
		}finally{
			if(om != null){
				om.releaseConnection();
			}
		}
		omMessageContextToJSON(om, result);

		return result;
	}



	// ************************************************************************************************************
	// * Private Servlet Helpers
	// ************************************************************************************************************
	protected void omMessageContextToJSON(Om om, JSONObject result){
		//Loop through the OM message context and set it in the response
		Set<Entry<String, OmMessageContext>> omMessages = om.getOmMessageContexts().entrySet();
		for (Entry<String, OmMessageContext> omMessage : omMessages) {
			result.put("Method", omMessage.getKey());
			this.omMessageContextToJSON(omMessage.getValue(), result);
		}
	}

	/**
	 * Method will map a OmMessageContext to a JSON object with the contracted Keys to be consumed by the client.
	 * @param omMessageContext
	 * @return
	 */
	private void omMessageContextToJSON(OmMessageContext omMessageContext, JSONObject result) {
		JSONObject omMessages = new JSONObject();

		//If there is a member error it can be a mesg per member so we have to travers the mbr errors
		//where as om would only return one error not a list of them. 
		if (omMessageContext.getOmCommandErrorMbrs() != null) {
			Collection<OmCommandErrorMbr> omCommandErrorMbrs = omMessageContext.getOmCommandErrorMbrs();
			for (OmCommandErrorMbr omCommandErrorMbr : omCommandErrorMbrs) {
				JSONObject omMessage = new JSONObject();
				omMessage.put("RSN", omCommandErrorMbr.getOmMemberRsn());
				omMessage.put("RSNTXT", omCommandErrorMbr.getOmMemberRsntxt());
				omMessage.put("RC", omCommandErrorMbr.getOmMemberRc());
				omMessage.put("COMMAND", omMessageContext.getOmCommandExecuted());
				omMessage.put("MESSAGE_TITLE", omCommandErrorMbr.getOmMemberMessageTittle());
				omMessage.put("MESSAGE", omCommandErrorMbr.getOmMemberMessageSummary());

				omMessages.put(omCommandErrorMbr.getOmMemberName(), omMessage);

			}
		} else if (omMessageContext != null) { //non-zero case, something went wrong
			JSONObject omMessage = new JSONObject();

			omMessage.put("COMMAND", omMessageContext.getOmCommandExecuted());
			omMessage.put("MESSAGE_TITLE", omMessageContext.getOmMessageTittle());
			omMessage.put("MESSAGE", omMessageContext.getOmMessageSummary());

			omMessages.put(omMessageContext.getOmName(), omMessage);
		}
		result.put("Messages", omMessages);
	}




	/**
	 * Convert an OM Exceptoin to JSON to be sent to the UI Single message.
	 * @param e
	 * @return
	 */
	private JSONObject omExceptionToJSON(OmException e) {
		JSONObject omExceptionJson = new JSONObject();

		//Log the exception since we have been called to process one.
		if (logger.isErrorEnabled()) {
			logger.error(e.getMessage());
		}

		if (e != null) {
			String msg = OM_EXCEPTION.OM_EXCEPTION_MESG.msg(new Object[] {e.getOmCommandExecuted(), e.getOmReturnCode(), e.getOmReasonCode(), e.getOmReasonMessage(), e.getOmReasonText(), e.getErrorNumber()});
			omExceptionJson.put("MESSAGE_TITLE", OM_EXCEPTION.OM_EXCEPTION_TITTLE.msg());
			omExceptionJson.put("MESSAGE", msg);
			omExceptionJson.put("COMMAND", e.getOmCommandExecuted());
			omExceptionJson.put("RC", e.getOmReturnCode());
			omExceptionJson.put("RSN", e.getOmReasonCode());
			omExceptionJson.put("RSNTXT", e.getOmReasonText());
		}

		JSONObject exception = new JSONObject();
		exception.put("OmException", omExceptionJson);

		return exception;
	}

	private JSONObject omConnectionExceptionToJSON(OmConnectionException e) {
		JSONObject omConnectionExceptionJson = new JSONObject();

		//Log the exception since we have been called to process one.
		if (logger.isErrorEnabled()) {
			logger.error(e.getMessage());
		}
		
		String msg = "";
		if (e != null) {

			msg = e.getMessage();
		}

		omConnectionExceptionJson.put("MESSAGE_TITLE", OM_CONNECTION.OM_CONNECTION_EXCEPTION_TITTLE.msg());
		omConnectionExceptionJson.put("MESSAGE", msg);
		omConnectionExceptionJson.put("COMMAND", "N/A");
		omConnectionExceptionJson.put("RC", e.getConnectionReturnCode());
		omConnectionExceptionJson.put("RSN", e.getConnectionReasonCode());


		JSONObject exception = new JSONObject();
		exception.put("OmConnectionException", omConnectionExceptionJson);

		return exception;
	}

}
