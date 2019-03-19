/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class OmInteractionContext {

	private Message context = null;
	
	//Keys used for this context
	private static final String resourceAttributes 	= "resourceAttributes"; 	//Attributes that will map to the columns
	private static final String resourceLastUpdated = "resourceLastUpdated";	//Time the derby table was last update for this resource
	private static final String resourceCacheSize 	= "resourceCacheSize";		//Derby table resource size
	private static final String resourceVersion		= "resourceVersion";		//Version of IMS (latest version of ims in the IMSPlex)
	private static final String interactionMessage 	= "interactionMessage";		//Way for our UI to push messages to the user, free form messages
	private static final String liveModeEnabled 	= "liveModeEnabled";		//Flags if the interaction is LIVE vs having been discovered
	private static final String environment 		= "environment";			//Sysplex ID
	private static final String imsplexName 		= "imsplexName";			//imsplex name
	private static final String identifier 			= "identifier";				//method key (function name) from the services
	private static final String queryExecuted       = "queryExecuted";             //query that is run or equivalent type2command from the services
	
	public OmInteractionContext() {
		context = new Message();
		context.addMessage(resourceAttributes, new ArrayList<String>());				
		context.addMessage(resourceLastUpdated, "");
		context.addMessage(resourceCacheSize, "");
		context.addMessage(resourceVersion,"" );
		context.addMessage(interactionMessage, "");
		context.addMessage(liveModeEnabled, "false");
		context.addMessage(environment,"" );
		context.addMessage(imsplexName, "");
		context.addMessage(identifier, "");
		context.addMessage(queryExecuted, "");
	}

	/**
	 * Return a collection of attributes that correspond to the result that 
	 * came from OM. The collection will contain the largest set of attributes
	 * representing all responses. For example, a response as 5 attributes and
	 * another has 10, if the 5 are a subset of the 10 then the result will be
	 * 10 attributes.
	 * @return
	 */
	public ArrayList<String> getResourceAttributes(){
		return (ArrayList<String>) this.context.getMessage(resourceAttributes);
	}
	
	/**
	 * Set the IMS Attributes for this interaction with OM
	 * @param imsAttributesColumns
	 */
	public void setResourceAttributes(ArrayList<String> values){
		this.context.addMessage(resourceAttributes,values);
	}
	
	/**
	 * Set the IMS Attributes for this interaction with OM
	 * @param properties Should be the response properties 
	 * that are returned from the IMS Connect API after and
	 * interaction with OM. Method will extract the SLBL keys
	 * from the properties file and set them in the interaction
	 */
	public void setResourceAttributes(Properties[] properties){
		
         int propertiesLength = properties.length;
         ArrayList<String> columns = new ArrayList<String>();
         
         for (int i = 0; i < propertiesLength; i++) {
             columns.add(properties[i].getProperty("SLBL"));
         }
         
         this.context.addMessage(resourceAttributes,columns);
	}
	
	/**
	 * Get the last date the resource was updated in the cache
	 * @return
	 */
	public String getResourceLastUpdated(){
		return (String)this.context.getMessage(resourceLastUpdated);
	}
	
	/**
	 * Set the last date the resource was last updated
	 * @param value
	 */
	public void setResourceLastUpdated(Date value){
		if(value != null){
			this.context.addMessage(resourceLastUpdated,value.toString());
		}
	}
	
	/**
	 * Get the number of items in the cache for a resource
	 * @return
	 */
	public String getResourceCasheSize(){
		return (String)this.context.getMessage(resourceCacheSize);
	}
	
	/**
	 * Set the number of items in the cache for a resource
	 * @param value
	 */
	public void setResourceCacheSize(int value){
		this.context.addMessage(resourceCacheSize,String.valueOf(value));
	}
	
	/**
	 * Set the interaction is running in Live mode. Live mode occurs 
	 * when a user has not discovered resources
	 * @param value
	 */
	public void setLiveModeEnabled(boolean value){
		this.context.addMessage(liveModeEnabled,String.valueOf(value));
	}
	
	/**
	 * Get mode the interaction was run in, if true then it
	 * was live, otherwise false indicates the om interaction 
	 * visited the cache that was discovered. 
	 * @return
	 */
	public String getLiveModeEnabled(){
		return (String)this.context.getMessage(liveModeEnabled);
	}

	/**
	 * Set the environment ID (sysplex)
	 * @param value
	 */
	public void setEnvironment(int value){
		this.context.addMessage(environment,String.valueOf(value));
	}
	
	/**
	 * Get the environment ID (sysplex)
	 * @param value
	 * @return
	 */
	public String getEnvironment(){
		return (String)this.context.getMessage(environment);
	}
	
	/**
	 * Set the imsplex used with the om interaction
	 * @param value
	 */
	public void setImsplexName(String value){
		this.context.addMessage(imsplexName,value);
	}
	
	/**
	 * Get the imsplex used with the om interaction
	 * @param value
	 * @return
	 */
	public String getImsplexName(){
		return (String)this.context.getMessage(imsplexName);
	}
	
	/**
	 * Set the identifier that initiated the om interaction. An identifier
	 * is a method key, usually the name of the method in the service which 
	 * is later used in the OM instance to act as the key for each omInteraction
	 * @param value
	 */
	public void setIdentifier(String value){
		this.context.addMessage(identifier,value);
	}
	
	/**
	 * Get the identifier that initiated the om interaction. An identifier
	 * is a method key, usually the name of the method in the service which 
	 * is later used in the OM instance to act as the key for each omInteraction
	 * @param value
	 * @return
	 */
	public String getIdentifier(){
		return (String)this.context.getMessage(identifier);
	}

	/**
     * Get the queryExecuted that initiated the om interaction. This
     * is the type2command query intented to be executed by the service method used in the OM instance 
     * @param value
     * @return
     */
    public String getQueryExecuted(){
        return (String)this.context.getMessage(queryExecuted);
    }

	/**
	 * Get the IMS version used for the Om Interaction
	 * @param interactionMessage
	 */
	public String getResourceVersion(){
		return (String)this.context.getMessage(resourceVersion);
	}
	
	/**
	 * Set the IMS version for this OM interaction 
	 * @param value
	 */
	public void setResourceVersion(String value){
		this.context.addMessage(resourceVersion,value);
	}
	
	/**
	 * Message used by the OM runtime to send messages to the UI.
	 * @param imsUserMessage
	 */
	public void setInteractionMessage(String message){
		this.context.addMessage(interactionMessage,message);
	}
	
	/**
	 * Message used by the OM runtime to send messages to the UI.
	 * @return
	 */
	public String getInteractionMessage(){
		return (String)this.context.getMessage(interactionMessage);
	}

	/**
	 * Set a custom message to be sent to the UI for evaluation
	 * @param customMessageKey
	 * @param value
	 */
	public void addCustomMessage(String customMessageKey, String value){
		this.context.addMessage(customMessageKey,value);
	}
	
	/**
	 * Get a custom message to be sent to the UI for evaluation
	 * @param customMessageKey
	 * @return
	 */
	public String getCustomMessage(String customMessageKey){
		return (String)this.context.getMessage(customMessageKey);
	}
	
	@Override
	public String toString(){
	    return this.context.toString();
	}

	/**
     * Set the query executed that initiated the om interaction. 
     * @param queryExecutedValue
     */
    public void setQueryExecuted(String queryExecutedValue) {
        this.context.addMessage(queryExecuted,queryExecutedValue);
        
    }
}
