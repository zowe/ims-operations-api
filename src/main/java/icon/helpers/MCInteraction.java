/**
 *  Copyright IBM Corporation 2018, 2019
 */

package icon.helpers;

import java.util.ArrayList;
import java.util.List;

public class MCInteraction {

	private int imsconnectId;		//IMSTM.IMSCONNECT.IMSCONNECT_ID 	- Primary Key
	private String hostname;		//IMSTM.IMSCONNECT.HOSTNAME 		- can be IP as well

	private Integer port;				//IMSTM.IMSCONNECT.PORT
	private int datastore;			//IMSTM.IMSCONNECT.DATASTORE 		- This is the IMSPLEX that is configured in the UI text field, it represents the master. Discovered IMSPLEXES are not masters
	private boolean uses_ssl;		//IMSTM.IMSCONNECT.USES_SSL
	private byte[] keystore;		//IMSTM.IMSCONNECT.KEYSTORE
	private String keystore_pass;	//IMSTM.IMSCONNECT.KEYSTORE_PASS
	private byte[] truststore;		//IMSTM.IMSCONNECT.TRUSTSTORE
	private String truststore_pass;	//IMSTM.IMSCONNECT.TRUSTSTORE_PASS
	private String imsplexName = null;
	private List<String> datastores = new ArrayList<String>();
	private String command;

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getImsconnectId() {
		return imsconnectId;
	}
	public void setImsconnectId(int imsconnectId) {
		this.imsconnectId = imsconnectId;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getDatastore() {
		return datastore;
	}
	public void setDatastore(int datastore) {
		this.datastore = datastore;
	}
	public boolean isUsesSsl() {
		return uses_ssl;
	}
	public void setUses_ssl(boolean uses_ssl) {
		this.uses_ssl = uses_ssl;
	}
	public byte[] getKeystore() {
		return keystore;
	}
	public void setKeystore(byte[] keystore) {
		this.keystore = keystore;
	}
	public String getKeystorePass() {
		return keystore_pass;
	}
	public void setKeystorePass(String keystore_pass) {
		this.keystore_pass = keystore_pass;
	}
	public byte[] getTruststore() {
		return truststore;
	}
	public void setTruststore(byte[] truststore) {
		this.truststore = truststore;
	}
	public String getTruststorePass() {
		return truststore_pass;
	}
	public void setTruststorePass(String truststore_pass) {
		this.truststore_pass = truststore_pass;
	}
	public String getImsPlexName() {
		return imsplexName;
	}
	public void setImsPlexName(String datastoreName) {
		this.imsplexName = datastoreName;
	}
	public List<String> getDatastores() {
		return datastores;
	}
	public void setDatastores(List<String> datastores) {
		this.datastores = datastores;
	}


}
