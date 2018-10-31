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
import json.java.JSONArray;
import json.java.JSONObject;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import om.exception.OmException;
import om.exception.message.OM_CONNECTION;
import om.exception.message.OM_EXCEPTION;
import om.message.IQEO;
import om.message.OmCommandErrorMbr;
import om.message.OmInteractionContext;
import om.message.OmMessageContext;
import om.result.OmResultSet;
import om.service.CommandService;
import om.services.Om;

@Stateless
public class OMServlet {

	@Resource (name = "mc_cf")
	private ConnectionFactory mcCF;

	static final Logger logger = LoggerFactory.getLogger(OMServlet.class);

	//Message Key's used for OM Message Communication
	private static final String COMMAND               = "command";                               //passed in context
	private static final String MESSAGE               = "message";                               //passed in context
	private static final String MESSAGE_TITTLE        = "messageTitle";                          //passed in context
	private static final String STATUS                = "status";                                //INFO,WARNING,ERROR - decided here in servlet
	private static final String OM_SUCCESS_ZERO       = "00000000";  

	//Message status sent to the client for interpretation
	private static enum OM_MESSAGE_STATUS_TYPE {
		ERROR("error"), 
		WARNING("warning"), 
		SUCCESS("success"), 
		INFORMATION("information"), 
		CRITICAL("critical"), 
		ATTENTION("attention"), 
		COMPLIANCE("compliance");

		private String value;

		private OM_MESSAGE_STATUS_TYPE(String val) {
			this.value = val;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Method will execute an IMS Type1/2 command orginating from the command console in the E4A UI. This method is 
	 * designed for use only with the UI and must have the required param values.
	 * @param params
	 * @param session
	 * @return
	 * @throws OmDatastoreException 
	 */
	public JSONObject executeUserImsCommand(String command, MCInteraction mcSpec)  //Make our own custom exception
	{


		//MAKE THESE STATIC
		//String CMD_PREFIX = "CMD(";
		String ROUTE_PREFIX = " ROUTE(";
		String SUFFIX = ")";

		JSONObject result = new JSONObject();

		//OM message contents sent back to UI
		JSONObject message = new JSONObject();
		JSONArray columns = new JSONArray();   
		JSONArray  data = new JSONArray(); 
		JSONObject commandExecutedGrid = new JSONObject();
		JSONObject commandExecutedText = new JSONObject();

		ArrayList<String> plexImsMbrs = new ArrayList<String>();

		StringBuffer commandFormatted = null;
		int counter = 0; //Used to create an ID for display a Grid.
		IconOmConnection omConnection = null;
		Om om = null;
		OmResultSet omResultSet;
		String routedImsString = "";

		IconOmConnectionFactory IconCF = new IconOmConnectionFactory();

		try {
			omConnection = IconCF.createIconOmConnectionFromData(mcSpec);


			om = new Om(omConnection);

			//Figure out how to deal with versioning later.
			CommandService cService = om.getCommandService();

			OmResultSet plexResultSet= cService.executeImsCommand("executeUserImsCommand","CMD(QUERY IMSPLEX TYPE(IMS) SHOW(STATUS))");
			Properties[] response = plexResultSet.getResponseProperties();

			if (plexImsMbrs.isEmpty()) {
				routedImsString = "*";
			}
			else {
				for (int i = 0; i<response.length; i++) {
					plexImsMbrs.add(response[i].getProperty("IMSMBR"));
				}

				if (!plexImsMbrs.containsAll(mcSpec.getDatastores())) {
					//throw exception, invalid datastores, are not inside the plex specified
				} else {
					StringBuilder sb = new StringBuilder();
					for (String s : mcSpec.getDatastores()) {
						sb.append(s);
						sb.append(",");
					}
					sb.deleteCharAt(sb.length()-1);
					routedImsString = sb.toString();
				}
			}

			//We need to proccess the command, prepare it with the PREFIX and ROUTE SUFFIX
			commandFormatted = new StringBuffer(command).append(ROUTE_PREFIX).append(routedImsString).append(SUFFIX);
			omResultSet= cService.executeImsCommand("executeUserImsCommand",commandFormatted.toString());

			//Build the columns to be used by the grid:
			Properties[] columnProperties = omResultSet.getResponsePropertiesHeaders();
			if(columnProperties != null){
				for(Properties p : columnProperties){
					JSONObject columnTitle = new JSONObject();
					String columnName = (String) p.get("SLBL");
					columnTitle.put("field", columnName);
					columnTitle.put("name", columnName);
					columns.add(columnTitle);
				}
			}
			//Responseproperties is the results as a map from connect api
			Properties[] dataProperties = omResultSet.getResponseProperties();
			if(dataProperties != null){
				for(Properties p : dataProperties){
					JSONObject prop = new JSONObject();
					prop.put("resourceId", counter++);
					prop.putAll(p);
					data.add(prop);
				}
			}

			commandExecutedGrid.put("columns",columns);
			commandExecutedGrid.put("data", data);
			commandExecutedGrid.put("identity", "resourceId");

			//This result is for displaying a dojo grid
			result.put("commandExecutedGrid", commandExecutedGrid);

			//This result is for displaying a result as formatted text
			commandExecutedText.put("commandRun",omResultSet.toStringAsTable());

			result.put("commandExecutedText", commandExecutedText);

			//Type of command Type1 or Type2
			result.put("imsCommandType",  omResultSet.getOmMessageContext().getOmCommandType());

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

			message.put("omInteractionContexts", omInteractionContextsToJSON(om));
			message.put("omMessageContext", omMessageContextToJSON(om));
		}catch (OmConnectionException e) {
			message.put("OmConnectionException", omConnectionExceptionToJSON(e));
		} catch (OmException e) {
			message.put("OmException", omExceptionToJSON(e));
		}finally{
			if(om != null){
				om.releaseConnection();
			}
		}
		result.put("message", message);
		return result;
	}



	// ************************************************************************************************************
	// * Private Servlet Helpers
	// ************************************************************************************************************
	protected JSONObject omInteractionContextsToJSON(Om om){
		JSONObject omInteractionContextsJson 	= new JSONObject();

		//Loop through the omInteracxtionContext and set it in the response
		Set<Entry<String, OmInteractionContext>> omInteractionContexts = om.getOmInteractionContexts().entrySet();
		for (Entry<String, OmInteractionContext> interactionContext : omInteractionContexts) {
			omInteractionContextsJson.put(interactionContext.getKey(), this.omInteractionContextToJSON(interactionContext.getValue()));
		}
		return omInteractionContextsJson;
	}

	protected JSONObject omMessageContextToJSON(Om om){
		JSONObject omMessageContext = new JSONObject();
		//Loop through the OM message context and set it in the response
		Set<Entry<String, OmMessageContext>> omMessages = om.getOmMessageContexts().entrySet();
		for (Entry<String, OmMessageContext> omMessage : omMessages) {
			omMessageContext.put(omMessage.getKey(), this.omMessageContextToJSON(omMessage.getValue()));
		}
		return omMessageContext;
	}

	protected JSONObject omInteractionContextToJSON(OmInteractionContext omInteractionContext){
		JSONObject interactionConext = new JSONObject();
		if(omInteractionContext != null){

			JSONArray imsAttributesJsonArray = new JSONArray();

			//We should not hit a null case here but just in case lets leave it for now. 
			if(omInteractionContext.getResourceAttributes() != null){
				imsAttributesJsonArray.addAll(omInteractionContext.getResourceAttributes());
			}

			interactionConext.put("resourceAttributes", imsAttributesJsonArray);
			interactionConext.put("resourceLastUpdated", omInteractionContext.getResourceLastUpdated());
			interactionConext.put("resourceCacheSize", omInteractionContext.getResourceCasheSize());
			interactionConext.put("resourceVersion", omInteractionContext.getResourceVersion());
			interactionConext.put("interactionMessage", omInteractionContext.getInteractionMessage());
			interactionConext.put("liveModeEnabled", omInteractionContext.getLiveModeEnabled());
			interactionConext.put("environment", omInteractionContext.getEnvironment());
			interactionConext.put("imsplexName", omInteractionContext.getImsplexName());
			interactionConext.put("identifier", omInteractionContext.getIdentifier());
		}
		return interactionConext;
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
			String logMsg = IQEO.IQEO0015E.msg(new Object[] {e.getOmCommandExecuted(), e.getOmReturnCode(), e.getOmReasonCode(), e.getOmReasonMessage(), e.getOmReasonText(), e.getErrorNumber()});
			logger.error(logMsg);
		}

		if (e != null) {
			String msg = OM_EXCEPTION.OM_EXCEPTION_MESG.msg(new Object[] {e.getOmCommandExecuted(), e.getOmReturnCode(), e.getOmReasonCode(), e.getOmReasonMessage(), e.getOmReasonText(), e.getErrorNumber()});
			omExceptionJson.put(STATUS, OM_MESSAGE_STATUS_TYPE.ERROR.toString());
			omExceptionJson.put(MESSAGE_TITTLE, OM_EXCEPTION.OM_EXCEPTION_TITTLE.msg());
			omExceptionJson.put(MESSAGE, msg);
			omExceptionJson.put(COMMAND, e.getOmCommandExecuted());
		}

		return omExceptionJson;
	}

	/**
	 * Method will map a OmMessageContext to a JSON object with the contracted Keys to be consumed by the client.
	 * @param omMessageContext
	 * @return
	 */
	private JSONArray omMessageContextToJSON(OmMessageContext omMessageContext) {
		JSONArray omMessages = new JSONArray(); //Collection of all the omMessages

		//If there is a member error it can be a mesg per member so we have to travers the mbr errors
		//where as om would only return one error not a list of them. 
		if (omMessageContext.getOmCommandErrorMbrs() != null) {
			Collection<OmCommandErrorMbr> omCommandErrorMbrs = omMessageContext.getOmCommandErrorMbrs();
			for (OmCommandErrorMbr omCommandErrorMbr : omCommandErrorMbrs) {
				JSONObject omMessage = new JSONObject();
				omMessage.put(STATUS, OM_MESSAGE_STATUS_TYPE.WARNING.toString());
				omMessage.put(MESSAGE_TITTLE, omCommandErrorMbr.getOmMemberMessageTittle());
				omMessage.put(MESSAGE, omCommandErrorMbr.getOmMemberMessageSummary());
				omMessage.put(COMMAND, omMessageContext.getOmCommandExecuted());
				omMessages.add(omMessage);
			}
		} else if (omMessageContext != null) { //non-zero case, something went wrong
			JSONObject omMessage = new JSONObject();
			if (!omMessageContext.getOmReturnCode().equals(OM_SUCCESS_ZERO)) {
				omMessage.put(STATUS, OM_MESSAGE_STATUS_TYPE.WARNING.toString());
				omMessage.put(MESSAGE_TITTLE, omMessageContext.getOmMessageTittle());
				omMessage.put(MESSAGE, omMessageContext.getOmMessageSummary());
				omMessage.put(COMMAND, omMessageContext.getOmCommandExecuted());
			} else { //success, we still need to propagate the OM Commands to the UI for display
				omMessage.put(STATUS, OM_MESSAGE_STATUS_TYPE.SUCCESS.toString());
				omMessage.put(MESSAGE_TITTLE, omMessageContext.getOmMessageTittle());
				omMessage.put(MESSAGE, omMessageContext.getOmMessageSummary());
				omMessage.put(COMMAND, omMessageContext.getOmCommandExecuted());
			}
			omMessages.add(omMessage);
		}

		return omMessages;
	}

	private JSONObject omConnectionExceptionToJSON(OmConnectionException e) {
		JSONObject omConnectionExceptionJson = new JSONObject();

		//Log the exception since we have been called to process one.
		if (logger.isErrorEnabled()) {
			String logMsg = IQEO.IQEO0014E.msg(new Object[] {e.getConnectionType(), e.getEnvironmentId(), e.getImsplexName(), e.getConnectionReturnCode(), e.getConnectionReasonCode(), e.getErrorNumber()});
			logger.error(logMsg);
			logger.error(e.getMessage());
		}

		String msg = "A";
		if (e != null) {

			msg = e.getMessage();
		}

		omConnectionExceptionJson.put(STATUS, OM_MESSAGE_STATUS_TYPE.ERROR.toString());
		omConnectionExceptionJson.put(MESSAGE_TITTLE, OM_CONNECTION.OM_CONNECTION_EXCEPTION_TITTLE.msg());
		omConnectionExceptionJson.put(MESSAGE, msg);
		omConnectionExceptionJson.put(COMMAND, "N/A");

		return omConnectionExceptionJson;
	}

}
