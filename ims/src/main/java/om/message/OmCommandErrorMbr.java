/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Object represents and IMS Command Error Member that can result from an interaction with 
 * OM and IMS. 
 *
 */
public final class OmCommandErrorMbr {
    //Private static strings to reducing new'ing
    private static final String omMemberTyp         = "omMemberTyp";        // "typ";
    private static final String omMemberStyp        = "omMemberStyp";       // "styp";
    private static final String omMemberVsn         = "omMemberVsn";        // "vsn";
    private static final String omMemberJobname     = "omMemberJobname";    // "jobname";
    private static final String omMemberRc          = "omMemberRc";         // "rc";
    private static final String omMemberRsn         = "omMemberRsn";        // "rsn";
    private static final String omMemberRsntxt      = "omMemberRsntxt";     // "rsntxt";
    private static final String omMemberMsg         = "omMemberMsg";        // "msg";
    private static final String omMemberName 		= "omMemberName";		// "name"
    private static final String omMemberMessageSummary  = "omMemberMessageSummary";
    private final static String omMemberMessageTittle    = "omMemberMessageTittle";
    
    //Using message construct because maping to JSON is easier
    private Map<String, Object> mbr = null;
    private Collection<String> msgs = null;
    
    public OmCommandErrorMbr() {
        mbr = new HashMap<String, Object>();
        mbr.put(OmCommandErrorMbr.omMemberTyp, "");
        mbr.put(OmCommandErrorMbr.omMemberStyp, "");
        mbr.put(OmCommandErrorMbr.omMemberVsn, ""); 
        mbr.put(OmCommandErrorMbr.omMemberJobname, "");
        mbr.put(OmCommandErrorMbr.omMemberRc, "");
        mbr.put(OmCommandErrorMbr.omMemberRsn, "");
        mbr.put(OmCommandErrorMbr.omMemberRsntxt, "");
        mbr.put(OmCommandErrorMbr.omMemberMsg, msgs);
        mbr.put(OmCommandErrorMbr.omMemberMessageSummary, "");
        mbr.put(OmCommandErrorMbr.omMemberMessageTittle, "");
        mbr.put(OmCommandErrorMbr.omMemberName, "");
    }
    
    public String getOmMemberName() {
    	return this.mbr.get(omMemberName).toString();
    }
    
    public void setOmMemberName(String name) {
    	this.mbr.put(OmCommandErrorMbr.omMemberName, name);
    }
    
    public String getOmMemberTyp() {
        return this.mbr.get(omMemberTyp).toString();
    }
    
    public void setOmMemberTyp(String typ) {
        this.mbr.put(OmCommandErrorMbr.omMemberTyp,typ);
    }
    
    public String getOmMemberStyp() {
        return this.mbr.get(OmCommandErrorMbr.omMemberStyp).toString();
    }
    
    public void setOmMemberStyp(String styp) {
        this.mbr.put(OmCommandErrorMbr.omMemberStyp,styp);
    }
    
    public String getOmMemberVsn() {
        return this.mbr.get(OmCommandErrorMbr.omMemberVsn).toString();
    }
    
    public void setOmMemberVsn(String vsn) {
        this.mbr.put(OmCommandErrorMbr.omMemberVsn,vsn);
    }
    
    public String getOmMemberJobname() {
        return this.mbr.get(OmCommandErrorMbr.omMemberJobname).toString();
    }
    
    public void setOmMemberJobname(String jobname) {
        this.mbr.put(OmCommandErrorMbr.omMemberJobname,jobname);
    }
    
    public String getOmMemberRc() {
        return this.mbr.get(OmCommandErrorMbr.omMemberRc).toString();
    }
    
    public void setOmMemberRc(String rc) {
        this.mbr.put(OmCommandErrorMbr.omMemberRc,rc);
    }
    
    public String getOmMemberRsn() {
        return this.mbr.get(OmCommandErrorMbr.omMemberRsn).toString();
    }
    
    public void setOmMemberRsn(String rsn) {
        this.mbr.put(OmCommandErrorMbr.omMemberRsn,rsn);
    }
    
    public String getOmMemberRsntxt() {
        return this.mbr.get(OmCommandErrorMbr.omMemberRsntxt).toString();
    }
    
    public void setOmMemberRsntxt(String rsntxt) {
        this.mbr.put(OmCommandErrorMbr.omMemberRsntxt,rsntxt);
    }
    
    public Collection<String> getOmMemberMsg() {
        return (Collection<String>) this.mbr.get(OmCommandErrorMbr.omMemberMsg);
    }
    
    public void setOmMemberMsg(Collection<String> msgs) {
        this.mbr.put(OmCommandErrorMbr.omMemberMsg,msgs);
    }
    
    public String getOmMemberMessageSummary() {
        return this.mbr.get(OmCommandErrorMbr.omMemberMessageSummary).toString();
    }
    
    public void setOmMemberMessageSummary(String omMemberMessageSummary) {
        this.mbr.put(OmCommandErrorMbr.omMemberMessageSummary,omMemberMessageSummary);
    }
    
    public String getOmMemberMessageTittle() {
        return this.mbr.get(OmCommandErrorMbr.omMemberMessageTittle).toString();
    }
    
    public void setOmMemberMessageTittle(String omMemberMessageTittle) {
        this.mbr.put(OmCommandErrorMbr.omMemberMessageTittle,omMemberMessageTittle);
    }
    
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        
        String NEW_LINE = System.getProperty("line.separator");
        String format = "| %1$-25s| %2$-50s|" + NEW_LINE;
       // String line = "+------------------------------------------------------------------------------+" + NEW_LINE;
        //result.append(line);
        for(Map.Entry<String, Object> entry : mbr.entrySet()){
            if(entry.getValue() != null)
            result.append(formatHelper(entry.getValue().toString(), 50, format, entry.getKey()));
        }
                
//        result.append(line); 
        return result.toString();
    }
                
    private String formatHelper(String str, int chunkSize, String format, String key){
        StringBuilder result = new StringBuilder();
        boolean chunked = false;
        int len = str.length();
        for (int i=0; i<len; i+=chunkSize){
            String part = str.substring(i, Math.min(len, i + chunkSize));
  
            if(chunked){
                result.append(String.format(format,"",part)); 
            }else{
                result.append(String.format(format,key,part)); 
            }
            chunked = true;
        }
        return result.toString();
    }
}
