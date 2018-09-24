/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/

package om.common.exception;


public class OmDatastoreException extends Exception{

	private static final long serialVersionUID = 1L;
	private String query       = ""; 
	private int environmentId  = -1;
	private String imsplexName = ""; 
	
	public OmDatastoreException(String message) {
		super(message);
	}
	
	public OmDatastoreException(String message,Throwable cause){
		super(message,cause);
	}

	public OmDatastoreException(Throwable cause){
		super(cause);
	}
	
    public String getQuery(){
    	return this.query;
    }
    
    public void setQuery(String query){
    	this.query = query;
    }
    
	public int getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(int environment) {
		this.environmentId = environment;
	}

	public String getImsplexName() {
		return imsplexName;
	}

	public void setImsplexName(String imsplex) {
		this.imsplexName = imsplex;
	}
}
