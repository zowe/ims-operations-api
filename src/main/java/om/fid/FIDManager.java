/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.fid;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class that manages FID Readers.
 * Contains map that holds version (key) and FID Reader (value)
 * User inputs a version and receives the corresponding FID Reader
 * @author Jerry Li
 *
 */
public class FIDManager {
	
	private static FIDManager myInstance;
	private ResourceBundle myFidVersionMap;
	private static ConcurrentHashMap<String, FIDReader> myFIDReaders;
	
	/**
	 * Initialize version mapping. Put version and corresponding reader in map. 
	 */
	
	
	
	//testing code review remove this comment when finished...
	private FIDManager() {
		myFidVersionMap = ResourceBundle.getBundle("macroVersions");
		myFIDReaders = new ConcurrentHashMap<String, FIDReader>();
		for (String key : myFidVersionMap.keySet()) {
			FIDReader reader = new FIDReader(myFidVersionMap.getString(key));
			myFIDReaders.put(key, reader);
		}
	}
	
	public ConcurrentHashMap<String, FIDReader> getFIDReaders() {
		return myFIDReaders;
	}
	
	/**
	 * Returns correct FID Reader for version passed in
	 * @param version
	 * @return
	 */
	public FIDReader getFIDReader(String version) {
		return myFIDReaders.get(version);
	}
	
	public synchronized static FIDManager getInstance() {
		if (myInstance == null) {
			myInstance = new FIDManager();
		}
		
		return myInstance;
	}
}
