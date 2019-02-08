/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Class provides a mechanism for users to pass any messages needed to the consumer of an OM Collection. 
 * For example:
 *  	Message message = new Message();
		message.addMessage("success", "true");
		message.addMessage("title", "Ims Connect Execution Error");
		message.addMessage("RSN", "12345678");
		message.addMessage("RSNTXT", "Failed to discover clients");
		message.addMessage("message", "IMS Explorer for admin was able unable to communicate with IMS Connect");
 * @author ddimatos
 *</pre>
 */
public class Message {
	
	private Map<String, Object> message = new HashMap<String, Object>();
	
	public Map<String, Object> getMessageMap(){
		return this.message;
	}
	
	public void addMessage(String key, Object value){
		this.message.put(key, value);
	}
	
	public Object getMessage(String key){
		return this.message.get(key);
	}
	
	   @Override
       public String toString(){
	       boolean linePrinted = false;
	       
           StringBuilder result = new StringBuilder();
           
           String NEW_LINE = System.getProperty("line.separator");
           String format = "| %1$-25s| %2$-50s|" + NEW_LINE;
           String line = "+------------------------------------------------------------------------------+" + NEW_LINE;
           
           for(Map.Entry<String, Object> entry : message.entrySet()){
               if(!(entry.getValue() instanceof String)){
                   if (entry.getValue() instanceof Collection<?>){
                       Collection<?> items = (Collection<?>) entry.getValue();
                       
                       if(items != null && items.size() > 0){
                    	   result.append(line);
                    	   result.append(String.format(format,entry.getKey(),"Value"));
                    	   result.append(line);
                       }
                       
                       for(Object obj: items){
                          // result.append(line);
                          // result.append(obj);
                          //result.append(formatHelper(obj.toString(), 50, format, entry.getKey()+":"+ obj.toString()));
                    	  result.append(formatHelper(obj.toString(), 50, format, ""));
                       }
                   }else if (entry.getValue() instanceof Map<?,?>){
                	   Map<String,String> map = (Map<String, String>) entry.getValue();
                	   for (Map.Entry<String, String> items : map.entrySet()) {
                		    result.append(formatHelper(items.getValue().toString(), 50, format, entry.getKey()+":"+ items.getKey()));
                		}
                   }else if (entry.getValue() instanceof ArrayList<?>){
                	   result.append(formatHelper(entry.getValue().toString(), 50, format, entry.getKey()));
                   }
                   
                   linePrinted = true;
               }else{
            	   if(!linePrinted){
                       result.append(line);
                       linePrinted = true;
                   }
            	   
                   result.append(formatHelper(entry.getValue().toString(), 50, format, entry.getKey()));
               }
           }
                   
           result.append(line); 
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
