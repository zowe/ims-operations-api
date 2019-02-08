/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import om.constants.ImsCommandAttributes;
import om.version.Version;

public abstract class ServicesHelper {// extends Service {
	protected int sysplexId ;
	protected String imsplexName;
	protected Version version = null;
	protected boolean isDiscovering = false;
	protected Om om = null;
	protected Map<String, Version> resourceVersion = null;
	//private static final Logger logger = LoggerFactory.getLogger(ServicesHelper.class);
	
	
    
    protected ServicesHelper(Om om){
    	this.om = om;
    	this.sysplexId = om.getOMConnection().getEnvironment();
    	this.imsplexName = om.getOMConnection().getImsplex();
    }

//    /**
//     * Method converts a Datastore Exception often thrown by the abstract datastore dependency and converted into and
//     * OmDatastoreExecption that contains added meta-data such as where it originated from.
//     * @param e
//     * @param sysplexId
//     * @param imsplexId
//     * @param imsplexName
//     * @return
//     */
//    protected OmDatastoreException convertDatatstoreException(OmDatastoreException e, int sysplexId, String imsplexName) {
//        OmDatastoreException omDatastoreException = new OmDatastoreException(e);
//        omDatastoreException.setEnvironmentId(sysplexId);
//        omDatastoreException.setImsplexName(imsplexName);
//        omDatastoreException.setQuery("No Query Availble");
//        return omDatastoreException;
//    }
    
    /**
     * Method will review the route members passed and if there is at least one route member that is a '*', then a new
     * array is returned which removes all other members and resets it to an array of only '*'
     * @param routeMembers
     * @return
     */
    public String[] routeMemberFormater(String[] routeMembers) {
        /** Case: There is at least one asterisk as a member so reset it to a single asterisk **/
    	
    	HashSet<String> routeMemberList = new HashSet<String>(Arrays.asList(routeMembers));
    	
        if (routeMemberList.contains(ImsCommandAttributes.ASTERISK)) {
            return new String[] {ImsCommandAttributes.ASTERISK};
        }
        return routeMembers;
    }
    

   
	
	public Version setVersion(Map<String, Version> versions, String [] routeMembers){
		HashSet<String> routeMemberList = new HashSet<String>(Arrays.asList(routeMembers));
		
		if(routeMemberList.contains(ImsCommandAttributes.ASTERISK) || routeMemberList.size() > 1){
			return versions.get("latestResourceVersion");
		}else if(routeMembers[0] != null){
			return versions.get(routeMembers[0]);
		}
		
		return Version.IMS_ZERO_RELEASE;
	}
	
	public Version getVersion(){
		return this.version;
	}
}
