/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.services;

import java.util.HashMap;
import java.util.Map;

import om.connection.OMConnection;
import om.message.OmInteractionContext;
import om.message.OmMessageContext;
import om.service.CommandService;
import om.service.OmService;



/**
 * <pre>
 * Class provides access to given OM services such as databases, routing codes, transactions, programs, IMS Connect,etc.
 * Example how to access the various services, not there are some services that are extended to provide more than what 
 * OM would provide:
 *     public static void main(String[] args) throws com.ibm.ims.ea.om.common.exception.OmException {
        //(1) Get an OM Connection
        Om om = new Om(null);
        
        //(2) From the om connection get a service
        TransactionService transactionService = om.getTransactionService();
        
        //(3) Use the service to perform Om Operations
        try {
            java.util.Collection<com.ibm.ims.ea.om.transaction.Tran> transactions = transactionService.getTransactions();
        } catch (com.ibm.ims.ea.om.common.exception.OmDatastoreException e) {
            e.printStackTrace();
        } catch (com.ibm.ims.ea.om.common.exception.OmConnectionException e) {
            e.printStackTrace();
        }
        
        //(4) Use the extended services
        ExtendedIconServices extendedIconServices = om.getIconService().getExtendedIconServices();
        
        //(4.1) Extended services can be an aggregate of serveral commands or data manipulation and various return types
        java.util.Collection<String> imsplexes = extendedIconServices.getImsplexesForHostandPort("CSDMEC20.vmec.svl.ibm.com", 9999);
    }
 * @author ddimatos
 * </pre>
 */
public class Om implements OmService{
    
	private InteractionMode interactionMode = InteractionMode.DEFAULT;
    private OMConnection omConnection;
    
    //Collection of Message Contexts for each service called with this Om instance.
    private Map<String,OmMessageContext>  omMessageContexts = new HashMap<String, OmMessageContext>();
    private Map<String,OmInteractionContext> omInteractionContexts = new HashMap<String, OmInteractionContext>();
    
    /**
     * Add an OmMessageContext to the Om instance representing any services called during this
     * om interaction. 
     * @param omMessageContext
     * @return
     */
    public void addOmMessageContext(String key, OmMessageContext omMessageContext){
         omMessageContexts.put(key,omMessageContext);
    }
    
    /**
     * Get the collection of OmMessageContext interactions that occurred during this Om instance.
     * @return
     */
    public Map<String,OmMessageContext> getOmMessageContexts(){
        return this.omMessageContexts;
    }
    
    public void addOmInteractionContext(String key, OmInteractionContext omInteractionContex){
    	omInteractionContexts.put(key,omInteractionContex);
    }
   
    public Map<String,OmInteractionContext> getOmInteractionContexts(){
       return this.omInteractionContexts;
    }
   
    /**
     * Create an instance of Om to provide access to services this API can offer.
     * @param omConnection
     */
    public Om(OMConnection omConnection) {
        this.omConnection = omConnection;
    }   
    
    /**
     * Get the OmConnection this Om instance was constructed with.
     * @return
     */
    public OMConnection getOMConnection(){
        return this.omConnection;
    }
    
    /**
     * Release the OmConnection associated with this OM Instance. The release
     * of the omConnection is defined by the interface {@link OMConnection#releaseConnection()}
     */
    public void releaseConnection(){
    	this.omConnection.releaseConnection();
    }
    
  
    
    public CommandService getCommandService(){
        return new CommandServices(this);
    }
    
   
    
	public void setInteractionMode(InteractionMode interactionMode){
		this.interactionMode = interactionMode;
	}

	public boolean isInteractionModeLive(){
		if(this.interactionMode.value.equals(InteractionMode.LIVE.value())){
			return true;
		}
		return false;
	}

	public boolean isInteractionModeCache(){
		if(this.interactionMode.value.equals(InteractionMode.CACHE.value())){
			return true;
		}
		return false;
	}

	public boolean isInteractionModeDefault(){
		if(this.interactionMode.value.equals(InteractionMode.DEFAULT.value())){
			return true;
		}
		return false;
	}

	public enum InteractionMode {
		/**
		 * Interaction with OM when issuing IMS Commands will be only LIVE 
		 * meaning there is no caching involved. 
		 */
		LIVE("LIVE_MODE"),

		/**
		 * Interaction will only access what is in the cache. Depending on the implementation
		 * some attributes may not be returned such as Status since that is something generally
		 * accessed with a Auto or Live connection.
		 */
		CACHE("CACHE_MODE"),

		/**
		 * Interaction will be a hybrid of communicator where a Live connection is used for certain
		 * attributes such as Status and cached attributes which don't often change. The result 
		 * returned is a merged response as if it had come directly from OM. Often this provides
		 * performance.
		 */
		DEFAULT("DEFAULT_MODE");

		private String value = "";

		private InteractionMode(String value){
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		public static InteractionMode fromValue(String value) {
			return valueOf(value);
		}


		@Override
		public String toString(){
			return this.value;
		}
	}
}

