/**
 *  Copyright IBM Corporation 2018, 2019
 */

package json.java;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;



/**
 *
 */
public class SanitizeSerializer extends Serializer
{

	public SanitizeSerializer(Writer writer) {
		super(writer);
	}

    /**
     * Method to write a complete JSON object to the stream.
     * @param object The JSON object to write out.
     * @throws IOException Thrown if an error occurs during write.
     */
    public Serializer writeObject(JSONObject object) throws IOException {
        if (null == object) return writeNull();

        // write header
        writeRawString("{");
        indentPush();

        Iterator iter = null;
        if (object instanceof OrderedJSONObject)
        {
            iter = ((OrderedJSONObject)object).getOrder();
        }
        else
        {
            List propertyNames = getPropertyNames(object);
            iter = propertyNames.iterator();
        }

        while ( iter.hasNext() )
        {
            Object key = iter.next();
            if (!(key instanceof String)) throw new IOException("attempting to serialize object with an invalid property name: '" + key + "'" );

            Object value = object.get(key);
            if (!JSONObject.isValidObject(value)) throw new IOException("attempting to serialize object with an invalid property value: '" + value + "'");

            newLine();
            indent();
            writeString((String)key);
            writeRawString(":");
            space();
            write(cleanseValue((String)key, value));

            if (iter.hasNext()) writeRawString(",");
        }

        // write trailer
        indentPop();
        newLine();
        indent();
        writeRawString("}");

        return this;
    }
    
    /**
     * Cleanses the value based on the key
     */
    protected Object cleanseValue(String key, Object value) {
    	return Sanitizer.sanitize(key, value);
    }

}
