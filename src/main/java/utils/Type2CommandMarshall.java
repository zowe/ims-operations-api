/**
 *  Copyright IBM Corporation 2018, 2019
 */

package utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import om.exception.OmCommandGenerationException;

public class Type2CommandMarshall {
    private static final Logger logger = LoggerFactory.getLogger(Type2CommandMarshall.class);
    
    /**
     * Marshall String into object
     */
    public Object fromJsonToObject(String jsonString,Class<?> clazz) throws OmCommandGenerationException{
        if(logger.isDebugEnabled()){
            logger.debug(">> fromJsonToObject()");
        }
        
        Object result = null;
        
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            result = mapper.readValue(jsonString,clazz);
        } catch (JsonParseException e) {
            String error = "Unable to marshall JSON String: " + jsonString + ", Class: " + clazz.getSimpleName() + ", Exception Type: " + e.getClass().getSimpleName();
            OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
            throw omCommandGenerationException;
        } catch (JsonMappingException e) {
            String error = "Unable to marshall JSON String: " + jsonString + ", Class: " + clazz.getSimpleName() + ", Exception Type: " + e.getClass().getSimpleName();
            OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
            throw omCommandGenerationException;
        } catch (IOException e) {
            String error = "Unable to marshall JSON String: " + jsonString + ", Class: " + clazz.getSimpleName() + ", Exception Type: " + e.getClass().getSimpleName();
            OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
            throw omCommandGenerationException;
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("<< fromJsonToObject()");
        }
        
        return result;
    }
}
