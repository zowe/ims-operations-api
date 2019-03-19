/**
 *  Copyright IBM Corporation 2018, 2019
 */

package json.java.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import json.java.Serializer;

/**
 *
 */
public class SerializerVerbose extends Serializer
{

    /**
     * The indent depth.
     */
    private int indent = 0;


    /**
     * Constructor.
     */
    public SerializerVerbose(Writer writer)
    {
        super(writer);
    }

    /**
     * Method to write a space to the output writer.
     * @throws IOException Thrown if an error occurs during write.
     */
    public void space() throws IOException {
        writeRawString(" ");
    }

    /**
     * Method to write a newline to the output writer.
     * @throws IOException Thrown if an error occurs during write.
     */
    public void newLine() throws IOException {
        writeRawString("\n");
    }

    /**
     * Method to write an indent to the output writer.
     * @throws IOException Thrown if an error occurs during write.
     */
    public void indent() throws IOException {
        for (int i=0; i<indent; i++) writeRawString("   ");
    }

    /**
     * Method to increase the indent depth of the output writer.
     * @throws IOException Thrown if an error occurs during write.
     */
    public void indentPush()
    {
        indent++;
    }

    /**
     * Method to reduce the indent depth of the output writer.
     */
    public void indentPop()
    {
        indent--;
        if (indent < 0) throw new IllegalStateException();
    }

    /**
     * Method to get a list of all the property names stored in a map.
     */
    public List getPropertyNames(Map map)
    {
        List propertyNames = super.getPropertyNames(map);

        Collections.sort(propertyNames);

        return propertyNames;
    }

}
