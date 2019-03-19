/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.result;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import om.message.OmInteractionContext;
import om.message.OmMessageContext;



/**
 * Abstract class provides the container to contain the results from the execution of a
 * command that not only carries a generic payload in a properties map but also some
 * return codes that could have been returned by Ims Connect.
 * @author ddimatos
 *
 */
public class OmResultSet implements OmResult {
	private Properties[] 			responseProperties 			= new Properties[0];  //Response from IMS Connect API 
	private OmMessageContext 		omMessageContext			= null;
	private OmInteractionContext 	omInteractionContext 		= null;
	private Properties[] 			responsePropertiesHeaders 	= new Properties[0];  //Response from IMS Connect API 
	private Properties[] 			responseMsgData;				                  //Message data (if there is any)
	private Properties[] 			responseText;
	private boolean 				isDisplayCommand 			= false;
	private String 					environment 				= null;
	private String 					imsplex 					= null;


	public OmResultSet() {
		omMessageContext = new OmMessageContext();
		omInteractionContext = new OmInteractionContext();
	}

	public OmMessageContext getOmMessageContext(){
		return this.omMessageContext;
	}

	public void setOmMessageContext(OmMessageContext omMessageContext){
		this.omMessageContext = omMessageContext;
	}
	
	public OmInteractionContext getOmInteractionContext() {
		return this.omInteractionContext;
	}
	
	public void setOmInteractionContext(OmInteractionContext omInteractionContext) {
		this.omInteractionContext = omInteractionContext;
	}

	public Properties[] getResponseProperties() {

		/*
		 * Some keys in the responseProperties have random spacing. We want to trim the spaces to prevent 
		 * confusion. We also want to make them lower case. Values are untouched.
		 */
		return modifyKeys(responseProperties);
	}

	public void setResponseProperties(Properties[] props) {
		this.responseProperties = props;
	}
	
	public void setResponseText(Properties[] textMap) {
		this.responseText = textMap;
	}

	public Properties[] getResponsePropertiesHeaders() {
		return this.responsePropertiesHeaders;
	}

	public void setResponsePropertiesHeaders(Properties[] propsHeaders) {
		this.responsePropertiesHeaders = propsHeaders;
	}

	public Properties[] getResponseMsgData() {
		return this.responseMsgData;
	}

	public void setResponseMsgData(Properties[] msgData) {
		this.responseMsgData = msgData;
	}
	
	public void setDisplayCommandStatus(boolean bool) {
		this.isDisplayCommand = bool;
	}

	public void setEnvironment(String environment){
	    this.environment = environment;
	}
	
	public String getEnvironment(){
	    return this.environment;
	}
	
	public void setImsplex(String imsplex){
	    this.imsplex = imsplex;
	}
	
	public String getImsplex(){
	    return this.imsplex;
	}
	
	private Properties[] modifyKeys (Properties[] properties) {

		//We loop through the properties array
		for (int i = 0; i < properties.length; i++) {
			/*We cannot directly modify the key in the properties object, so we have to create 
			 * a new Properties object with the trimmed key
			 */ 
			Properties trimmmedResponse = new Properties();
			Set<Object> keySet = properties[i].keySet();
			/*
			 * Iterate through keySet and insert trimmed key with value in the new properties object
			 */
			for ( Object obj : keySet) {
				String key = (String) obj;
				String trimmedKey = key.trim();
				String lowerCaseKey = trimmedKey.toLowerCase();
				trimmmedResponse.put(lowerCaseKey, properties[i].get(obj));
			}
			properties[i] = trimmmedResponse;
		}
		return properties;

	}

	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		String format = "| %1$-25s| %2$-50s|" + NEW_LINE;
		String line = "+------------------------------------------------------------------------------+" + NEW_LINE;

		result.append(line);
		result.append(String.format(format, "Object", this.getClass().getSimpleName() ));  
		result.append(line);
		result.append(String.format(format, "Variable Description", "Value")); 
		result.append(String.format(format, "Enviroment ID", this.environment)); 
		result.append(String.format(format, "IMSPlex Name", this.imsplex)); 
		result.append(omMessageContext);
		
		Properties[] propsObj = getResponseProperties();

		if(propsObj != null){
			int length = propsObj.length;
			for(int i = 0; i < length; i++){
				Enumeration<Object> em = propsObj[i].keys();

				result.append(String.format(format, "Object", this.getClass().getSimpleName() ));  
				result.append(line);
				result.append(String.format(format, "Variable Description", "Value")); 
				result.append(line);

				while (em.hasMoreElements()){
					String key = (String)em.nextElement();
					result.append(String.format(format, key.trim(), propsObj[i].get(key)));  
				}
				result.append(line);
			}
		}

		return result.toString();
	}

	public String toStringAsTable(){
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		Properties[] responseResults = this.getResponseProperties();

		if(this.omMessageContext.getOmCommandType().equals(OmResult.COMMAND_TYPE.TYPE2.toString())){
			Properties[] responseHeaders = this.getResponsePropertiesHeaders();

			//1. Create a 2 Dimensional Array of FormatHeaders:ArrayList. FormatHeaders hold meta-data about the arrayList formating
			Map<OmResultResponseFormatter, ArrayList<String>> omResults = new LinkedHashMap<OmResultResponseFormatter, ArrayList<String>>();

			//2. Travers the response headers to collect the meta-data to insert into the formatHeaders
			for(Properties p : responseHeaders){

				//Set the formatHeader values
				OmResultResponseFormatter formatHeader = new OmResultResponseFormatter();
				formatHeader.columnName = (String) p.get("SLBL");
				formatHeader.columnDisplayName = formatHeader.columnName;
				formatHeader.columnWidth = (String) p.get("LEN");
				formatHeader.sort = (String) p.get("SORT");
				formatHeader.key = (String) p.get("KEY");
				formatHeader.skipb = (String) p.get("SKIPB");

				//If the value is set to "*" it means the results are varying, we need to find the max(result) to make padding correctly
				if(formatHeader.columnWidth.equals("*")){
					int currSize = 0;

					//For each (row) result, given the column we are viewing, we need to find the max length for all these results
					for(Properties rspResults : responseResults){
						String value = rspResults.getProperty(formatHeader.columnName);

						if(value != null){
							int valueLength = value.length();

							if(valueLength > currSize){
								currSize = valueLength;
							}
						}

						if(currSize != 0){
							formatHeader.columnWidth = String.valueOf(currSize);
						}else{
							formatHeader.columnWidth = String.valueOf(4); //We might want to show "null" whic is len of 4
						}
					}
				}

				//Format the display name to be the width of the columnwidth to make displaying text easier and consistent
				int formatHeaderLength = formatHeader.columnName.length(); //We should 
				while(formatHeaderLength < Integer.valueOf(formatHeader.columnWidth)){
					formatHeader.columnDisplayName+=" ";
					formatHeaderLength++;
				}

				//Set the format header with an empty arralist for results to be added
				omResults.put(formatHeader,new ArrayList<String>());
			}

			//2.Loop all results
			for(Properties p : responseResults){

				//2.1 Get a set of all keys/ColumnHeaders 
				Set<OmResultResponseFormatter> columnHeaders  = omResults.keySet();

				//2.2 Use each column header value, use it to pull the value from the result
				for(OmResultResponseFormatter header: columnHeaders){
					//	                  String value =  (String) p.getProperty(header.columnName,"");
					//	                  if(value != null){
					//	                      int valueLenth = value.length();
					//	                      while(valueLenth < Integer.valueOf(header.columnWidth)){
					//	                          value+=" ";
					//	                          valueLenth++;
					//	                      }
					//	                  }else{
					//	                      //If null flag formatHeader there are no results, we can use this to ease printing results
					//	                      //We can flag this empty and if when printing if skipb=y and empty we can not print it else if empty and skipb=n we print empty
					//	                  }

					String value =  p.getProperty(header.columnName);
					if(value != null && !value.trim().isEmpty()){
						value = value.trim();
						int valueLenth = value.length();
						while(valueLenth < Integer.valueOf(header.columnWidth)){ //TODO: Very inefficient 
							value+=" ";
							valueLenth++;
						}
						header.isColumnEmpty =false;
					}else{
						//If null flag formatHeader there are no results, we can use this to ease printing results
						//We can flag this empty and if when printing if skipb=y and empty we can not print it else if empty and skipb=n we print empty
						value = "";
						int valueLenth = value.length();
						while(valueLenth < Integer.valueOf(header.columnWidth)){
							value+=" ";
							valueLenth++;
						}
					}

					omResults.get(header).add(value);
				}
			}
			String tab = "\t"; //"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;//;
			//Print
			for (Entry<OmResultResponseFormatter, ArrayList<String>> entry : omResults.entrySet()) {
				OmResultResponseFormatter formatHeader = entry.getKey();
				if(!formatHeader.isColumnEmpty ) { //&& formatHeader.skipb.equals("yes")){

					result.append(formatHeader.columnDisplayName + tab);
				}
			}

			boolean hasMore = true;
			int count = 0;
			while(hasMore){

				result.append(NEW_LINE);
				for (Entry<OmResultResponseFormatter, ArrayList<String>> entry : omResults.entrySet()) {
					if(!entry.getKey().isColumnEmpty){
						String value = entry.getValue().get(count);

						result.append(value + tab);

						if(entry.getValue().size() == count+1){
							hasMore = false;
						}
					}
				}
				count++;
			}
		}else{ //TYPE1 command formatting

			Properties[] propertiesToBePrinted;
			//If this is a display command, responseResults will be a bean
			//and we can't print that, we have to print out the responseText set earlier
			if (isDisplayCommand) {
				propertiesToBePrinted = responseText;
			//Else we can just print the responseResults
			} else {
				propertiesToBePrinted = responseResults;
			}
				for(Properties p : propertiesToBePrinted){
					Enumeration<Object> keys = p.keys(); 
					result.append("MbrName ").append("   ").append(NEW_LINE);
					result.append("--------").append("   ").append("-----------------------").append(NEW_LINE);

					while(keys.hasMoreElements()){
						String key = (String) keys.nextElement();
						String singleResponse = p.getProperty(key);
						String[] singleResponseAsArray = singleResponse.split(",\\n+");

						for(String str: singleResponseAsArray){
							result.append(key).append("    ").append(str).append(NEW_LINE);
						}
					}
					result.append(NEW_LINE);
				}
		}

		return result.toString();
	}

	public String toStringAsTable_batch(){
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "<br>";//System.getProperty("line.separator")

		Properties[] responseResults = this.getResponseProperties();

		if(this.omMessageContext.getOmCommandType().equals(OmResult.COMMAND_TYPE.TYPE2.toString())){
			Properties[] responseHeaders = this.getResponsePropertiesHeaders();

			//1. Create a 2 Dimensional Array of FormatHeaders:ArrayList. FormatHeaders hold meta-data about the arrayList formating
			Map<OmResultResponseFormatter, ArrayList<String>> omResults = new LinkedHashMap<OmResultResponseFormatter, ArrayList<String>>();

			//2. Travers the response headers to collect the meta-data to insert into the formatHeaders
			for(Properties p : responseHeaders){

				//Set the formatHeader values
				OmResultResponseFormatter formatHeader = new OmResultResponseFormatter();
				formatHeader.columnName = (String) p.get("SLBL");
				formatHeader.columnDisplayName = formatHeader.columnName;
				formatHeader.columnWidth = (String) p.get("LEN");
				formatHeader.sort = (String) p.get("SORT");
				formatHeader.key = (String) p.get("KEY");
				formatHeader.skipb = (String) p.get("SKIPB");

				//If the value is set to "*" it means the results are varying, we need to find the max(result) to make padding correctly
				if(formatHeader.columnWidth.equals("*")){
					int currSize = 0;

					//For each (row) result, given the column we are viewing, we need to find the max length for all these results
					for(Properties rspResults : responseResults){
						String value = rspResults.getProperty(formatHeader.columnName);

						if(value != null){
							int valueLength = value.length();

							if(valueLength > currSize){
								currSize = valueLength;
							}
						}

						if(currSize != 0){
							formatHeader.columnWidth = String.valueOf(currSize);
						}else{
							formatHeader.columnWidth = String.valueOf(4); //We might want to show "null" whic is len of 4
						}
					}
				}

				//Format the display name to be the width of the columnwidth to make displaying text easier and consistent
				int formatHeaderLength = formatHeader.columnName.length(); //We should 
				while(formatHeaderLength < Integer.valueOf(formatHeader.columnWidth)){
					formatHeader.columnDisplayName+=" ";
					formatHeaderLength++;
				}

				//Set the format header with an empty arralist for results to be added
				omResults.put(formatHeader,new ArrayList<String>());
			}

			//2.Loop all results
			for(Properties p : responseResults){

				//2.1 Get a set of all keys/ColumnHeaders 
				Set<OmResultResponseFormatter> columnHeaders  = omResults.keySet();

				//2.2 Use each column header value, use it to pull the value from the result
				for(OmResultResponseFormatter header: columnHeaders){
					//	                  String value =  (String) p.getProperty(header.columnName,"");
					//	                  if(value != null){
					//	                      int valueLenth = value.length();
					//	                      while(valueLenth < Integer.valueOf(header.columnWidth)){
					//	                          value+=" ";
					//	                          valueLenth++;
					//	                      }
					//	                  }else{
					//	                      //If null flag formatHeader there are no results, we can use this to ease printing results
					//	                      //We can flag this empty and if when printing if skipb=y and empty we can not print it else if empty and skipb=n we print empty
					//	                  }

					String value =  p.getProperty(header.columnName);
					if(value != null && !value.trim().isEmpty()){
						value = value.trim();
						int valueLenth = value.length();
						while(valueLenth < Integer.valueOf(header.columnWidth)){ //TODO: Very inefficient 
							value+=" ";
							valueLenth++;
						}
						header.isColumnEmpty =false;
					}else{
						//If null flag formatHeader there are no results, we can use this to ease printing results
						//We can flag this empty and if when printing if skipb=y and empty we can not print it else if empty and skipb=n we print empty
						value = "";
						int valueLenth = value.length();
						while(valueLenth < Integer.valueOf(header.columnWidth)){
							value+=" ";
							valueLenth++;
						}
					}

					omResults.get(header).add(value);
				}
			}
			String tab = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;//"\t"; //;
			//Print
			for (Entry<OmResultResponseFormatter, ArrayList<String>> entry : omResults.entrySet()) {
				OmResultResponseFormatter formatHeader = entry.getKey();
				if(!formatHeader.isColumnEmpty ) { //&& formatHeader.skipb.equals("yes")){

					result.append(formatHeader.columnDisplayName + tab ); //+ "&nbsp;&nbsp;&nbsp"
				}
			}

			boolean hasMore = true;
			int count = 0;
			while(hasMore){

				result.append(NEW_LINE);
				for (Entry<OmResultResponseFormatter, ArrayList<String>> entry : omResults.entrySet()) {
					if(!entry.getKey().isColumnEmpty){
						String value = entry.getValue().get(count);

						result.append(value + tab);

						if(entry.getValue().size() == count+1){
							hasMore = false;
						}
					}
				}
				count++;
			}
		}else{ //TYPE1 command formatting

			Properties[] propertiesToBePrinted;
			//If this is a display command, responseResults will be a bean
			//and we can't print that, we have to print out the responseText set earlier
			if (isDisplayCommand) {
				propertiesToBePrinted = responseText;
			//Else we can just print the responseResults
			} else {
				propertiesToBePrinted = responseResults;
			}
				for(Properties p : propertiesToBePrinted){
					Enumeration<Object> keys = p.keys(); 
					result.append("MbrName ").append("   ").append(NEW_LINE);
					result.append("--------").append("   ").append("-----------------------").append(NEW_LINE);

					while(keys.hasMoreElements()){
						String key = (String) keys.nextElement();
						String singleResponse = p.getProperty(key);
						String[] singleResponseAsArray = singleResponse.split(",\\n+");

						for(String str: singleResponseAsArray){
							result.append(key).append("    ").append(str).append(NEW_LINE);
						}
					}
					result.append(NEW_LINE);
				}
		}

		return result.toString();
	}
	
	/**
	 * Provides a formatting module to maintain meta-data that OM sent us about the attributes
	 * and can is used to honor as well as format the results in a cummulative grid
	 *
	 */
	private class OmResultResponseFormatter{
		//TODO: Setters/getters for this
		String columnDisplayName = "";
		String columnName = "";
		String columnWidth = "";
		@SuppressWarnings("unused")
		String sort = "";
		@SuppressWarnings("unused")
		String key      = "";
		@SuppressWarnings("unused")
		String skipb    = "";
		Boolean isColumnEmpty = true;
	}
}
