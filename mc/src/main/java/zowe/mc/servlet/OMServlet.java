package zowe.mc.servlet;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

public class OMServlet {

	/**
	 * Method will execute an IMS Type1/2 command orginating from the command console in the E4A UI. This method is 
	 * designed for use only with the UI and must have the required param values.
	 * @param params
	 * @param session
	 * @return
	 * @throws OmDatastoreException 
	 */
	public JSONObject executeUserImsCommand(JSONArray params, HttpSession session) throws Exception //Make our own custom exception
	{
		//MAKE THESE STATIC
		String CMD_PREFIX = "CMD(";
		String ROUTE_PREFIX = " ROUTE(";
		String SUFFIX = ")";

		JSONObject result = new JSONObject();
		JSONObject commandExecutedGrid = new JSONObject();
		JSONArray columns = new JSONArray();                    //Column values used by gridx
		JSONArray  data = new JSONArray();                      //data used by gridx
		JSONObject commandExecutedText = new JSONObject();
		JSONObject paramValues = (JSONObject) params.get(0);    //Get the Resource Node key/values
		JSONObject commandValues = (JSONObject) params.get(1);  //Get the command sent over, may support more than one in the future
		JSONObject routedIms = (JSONObject) params.get(2);      //get the IMS's to route to

		//Values from the resource node
		Long env            =  (Long) paramValues.get("sysplexId");
		String imsplexName  = (String) paramValues.get("imsplexName");

		//Command that was entered
		String command      = (String) commandValues.get("commandEntered");

		//IMS's they selected in the dropdown to route to
		JSONArray routedImsArray = (JSONArray) routedIms.get("routedIms");

		String commandType = (String) paramValues.get("commandType");
		String fileName = (String) paramValues.get("fileName");
		//OM message contents sent back to UI
		JSONObject omMessageContext = new JSONObject();
		JSONObject message = new JSONObject();

		//User session
		//UserInfo userInfo = UserBinding.getUserInfo(session);        
		String routedImsString = "";

		StringBuffer commandFormatted = null;
		int counter = 0; //Used to create an ID for display a Grid.
		OMConnection omConnection = null;
		Om om = null;
		OmResultSet omResultSet;
		ArrayList<String> results = new ArrayList<String>();
		try {
			omConnection = OMConnectionManager.getInstance().getConnection(longToIntSafely(env), imsplexName, userInfo);
			om = new Om(omConnection);

			int routedImsArrayLength = routedImsArray.size();

			if(routedImsArrayLength >0){
				for(int i = 0;i < routedImsArrayLength; i++){
					if(i == routedImsArrayLength-1){
						routedImsString+=routedImsArray.get(i);
					}else{
						routedImsString+=routedImsArray.get(i) + ",";
					}
				}

				//Single Type1/Type2 command typed by the user 
				if(commandType.equalsIgnoreCase("SINGLE")){
					//We need to proccess the command, prepare it with the PREFIX and ROUTE SUFFIX
					commandFormatted = new StringBuffer(CMD_PREFIX).append(command).append(SUFFIX).append(ROUTE_PREFIX).append(routedImsString).append(SUFFIX);
					omResultSet= om.getCommandService().executeImsCommand("executeUserImsCommand",commandFormatted.toString());

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
				} else { //Batch File - commands execution
					LinkedList<String> resultsList = new LinkedList<String>();
					int i=1;
					String response = null;
					boolean isReturnCode = false;
					String fileNamePrint = "File Name.............: " + fileName +"<br>";

					resultsList.add(fileNamePrint);
					int count=0;

					JSONArray storeData = new JSONArray();
					if(command.contains("<div>")){
						command = command.replaceAll("<div>", "");
					}
					if(command.contains("</div>")){
						command = command.replaceAll("</div>", "");
					}
					for(String commandIms: command.split("<br />")){
						if(!commandIms.isEmpty()){
							//Comment in the editor need to be ignored
							if(!commandIms.matches("\\/\\*(.|\\s)*?\\*\\/|\\/\\/.*(?<!>)")) {
								commandFormatted = new StringBuffer();
								//We need to process the command, prepare it with the PREFIX and ROUTE SUFFIX
								commandFormatted = new StringBuffer(CMD_PREFIX).append(commandIms).append(SUFFIX).append(ROUTE_PREFIX).append(routedImsString).append(SUFFIX);
								omResultSet = om.getCommandService().executeImsCommand("executeImsCommand"+i++,commandFormatted.toString());

								if(!omResultSet.getOmMessageContext().getOmReturnCode().equals("00000000")){
									isReturnCode = true;
								}
								//System.out.println("isReturnCode: " + isReturnCode);  
								//System.out.println("count" +(count++));
								response = "=========================================================================================================================================================================================================================================================================================================================" + "<br>";
								response += commandFormatted + "<br>"; //Print command
								response += "=========================================================================================================================================================================================================================================================================================================================" + "<br>";
								//response += "<br>" + omResultSet.getOmMessageContext().getOmMessageSummary() + "<br>";

								response += "IMSplex....... : " +imsplexName + "<br>";
								response += "Routing....... : " +routedImsString + "<br>";
								response += "Return Code... : " +omResultSet.getOmMessageContext().getOmReturnCode() + "<br>";
								response += "Reason Code... : " +omResultSet.getOmMessageContext().getOmReasonCode() + "<br>";
								response += "Reason text... : " +omResultSet.getOmMessageContext().getOmReasonText() + "<br>";
								response += "Start Time.... : " +omResultSet.getOmMessageContext().getOmStartTime() + "<br>";
								response += "Stop Time..... : " + omResultSet.getOmMessageContext().getOmStopTime() + "<br>\n";
								response += omResultSet.toStringAsTable_batch(); //Print Response

								results.add(response);    
								//response message data being returned
								if(omResultSet.getResponseMsgData() != null) {
									//Convert to JSONArray and add to message
									Properties[] messageData = omResultSet.getResponseMsgData();
									JSONArray msgData = new JSONArray();
									for(Properties p: messageData) {
										JSONObject responseMsgData = new JSONObject();
										responseMsgData.putAll(p);
										msgData.add(responseMsgData);
									}
									message.put("msgDataFromType2", msgData);
								}
								result.put("message", omResultSet.getOmMessageContext().getOmMessageSummary());
								result.put("Start Time", omResultSet.getOmMessageContext().getOmStartTime());

								//Overall Return Code. 0 means - all good and 8 - means one or more command returned non-zero return code from OM(can be warning or error).
								String overAllRC;
								if(isReturnCode){
									overAllRC="OverAll Return Code...: " + "8";	
								} else {
									overAllRC="OverAll Return Code...: " + "0";
								}

								resultsList.add(overAllRC);
								resultsList.addAll(results);

								commandExecutedText = new JSONObject();
								commandExecutedText.put("commandRun", response); //resultsList.toString().replace(", ", "").replace("[", "").replace("]", "").trim()
								result.put("identity", "Batch Commands");
								result.put("overAllReturnCode", overAllRC);
								storeData.add(commandExecutedText); //commandRun
							}
						}
					}
					//To test what is the output(command results) size when written to a file 
					/*  File file = new File("C:/results2.txt");
    				if(!file.exists()){
    					file.createNewFile();
    				}

    				FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
    				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    				bufferedWriter.write(storeData.toString()); //response
    				bufferedWriter.close();*/
					result.put("data", storeData);
				}
			}else{
				if(commandType.equalsIgnoreCase("SINGLE")){ // Single line command - TODO - merge to have common logic for single and batch spoc
					//We need to proccess the command, prepare it with the PREFIX
					commandFormatted = new StringBuffer(CMD_PREFIX).append(command).append(SUFFIX);
					omResultSet = om.getCommandService().executeImsCommand("executeUserImsCommand",commandFormatted.toString());

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
				} 
			}
			message.put("omInteractionContexts", omInteractionContextsToJSON(om));
			message.put("omMessageContext", omMessageContextToJSON(om));
		}catch (OmConnectionException e) {
			message.put("OmConnectionException", omConnectionExceptionToJSON(e));
		} catch (OmException e) {
			message.put("OmException", omExceptionToJSON(e));
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			if(om != null){
				om.releaseConnection();
			}
		}
		result.put("message", message);
		return result;
	}
}
