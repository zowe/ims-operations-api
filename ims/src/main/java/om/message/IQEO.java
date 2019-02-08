/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.message;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class provides access to the resource bundle messages. Example usage is:
 * IQEO.IQEO0002E.msg(new Object[] {e.getLocalizedMessage()})
 * @author ddimatos 
 *
 */
public enum IQEO {
	/** IQEO0000E: A Java stack trace has been detected useful for technical support. The stack trace = {0} */
	IQEO0000E("IQEO0000E"),
	
	/** IQEO0001E: There is an error in the underlying protocol that will not allow a socket connection. Message = {0} */
	IQEO0001E("IQEO0001E"),
	
	/** IQEO0002E: There is an error in the underlying connection implementation that will not allow a connection. Message= {0} */
	IQEO0002E("IQEO0002E"),
	
	/** IQEO0003E: There is an error in the underlying connection execution that will not allow the request to be executed. Message = {0} */
	IQEO0003E("IQEO0003E"),
	
	/** IQEO0004E: The resource bundle cannot be found, or a resource is missing from a resource bundle. Message = {0} */
	IQEO0004E("IQEO0004E"),
	
	/** IQEO0005E: Invalid property for Imsplex Member. Message ={0} */
	IQEO0005E("IQEO0005E"),
	
	/** IQEO0006E = IQEO0006E: There is an error communication with IMSConnect. Message = {0}*/
	IQEO0006E("IQEO0006E"),
	
	/** IQEO0007E = IQEO0007E Command could not be executed. An error returned from IMS Connect or OM. Command {0} Connection {1} Connect Rc {2}, Connect Rsn Code {3}, OM Rc {4}, OM Rsn Code {5}, Om Rsn Msg {6}, Om Rsn Text {7} */
	IQEO0007E("IQEO0007E"),
	
	/** IQEO0008E: Invalid property for Program Member. Message = {0} */ 
	IQEO0008E("IQEO0008E"),
	
	/** IQEO0009E = IQEO0009E: An invalid argument has been detected. Message = {0} */
	IQEO0009E("IQEO0009E"),
	
		/** IQEO0010E: Invalid property for Transaction. Message ={0} */
	IQEO0010E("IQEO0010E"),
	
	/** IQEO0011E = IQEO0011E: There is an error getting an OmConnection. Message ={0} */
	IQEO0011E("IQEO0011E"),
	
	/** IQEO0012E = IQEO0012E: Unable to execute service {0}. Message ={1} */
	IQEO0012E("IQEO0012E"),
	
	/** IQEO0013E = IQEO0013E: There is an error accessing the locale cache.The environment is: {0}. The IMSPlex is: {1}. The IMSPlexId is: {2}. The following query failed: {2}.  */
	IQEO0013E("IQEO0013E"),
			
	/** IQEO0014E = IQEO0014E: There is an error communicating with the connection. The type is: {0}. The environment is: {1}. The IMSPlex is: {2}. The return code is: {3}. The reason code is: {4}. The error number is: {5}. */
	IQEO0014E("IQEO0014E"),
			
	/** IQEO0015E = IQEO0015E: An error occurred while communicating with Operations Manager. The command is: {0}. The error return code is: {1}. The reason code is: {2}. The reason message code is: {3}. The reason text is: {4}. The error number is: {5}. */
	IQEO0015E("IQEO0015E"),
	
	/** IQEO0016E = IQEO0016E: The method or field cannot be accessed for security reasons = {0}, method/field = {1} */
	IQEO0016E("IQEO0016E"),
	
	/** IQEO0017E = IQEO0017E: The method or field cannot be found within the class = {0}, method/field = {1} */
	IQEO0017E("IQEO0017E"),
	
	/** IQEO0018E = IQEO0018E: The argument(s) for the field or method are invalid. Message = {0}. Illegal Argument(s) = {1} */
	IQEO0018E("IQEO0018E"),
	
	/** IQEO0019E = IQEO0019E: There is an error with command generation. Message = {0}.  */
	IQEO0019E("IQEO0019E"),
	
    /** IQEO0020E = IQEO0020E: Unable to complete request, this version {0} of IMS is unsupported.  **/
    IQEO0020E("IQEO0020E"),
    
	/** IQEO3001I = IQEO3001I: Operations Manager has returned a non-zero return code for command = {0}, return code = {1}, reason code {2}, reason message code = {3}, reason text = {4}, review the member completion codes. */
	IQEO3001I("IQEO3001I"),
	
	;
	
	private String msgCode;

	private IQEO(String msgCode){
	    this.msgCode = msgCode;
	}
	
    /* *******************************************************************************/
    /* * Resource bundle helpers
    /* *******************************************************************************/
    final static Logger logger             = LoggerFactory.getLogger(IQEO.class);
    private ClassLoader classLoader        = this.getClass().getClassLoader();
    private String      resourceBundleName = this.getClass().getSimpleName();

    /**
     * <pre>
     * Get the resource bundle message with no formatting options and the default locale for this instance of
     * the java virtual machine.
     * If formatting is required, see {@link #msg(Object[])}, {@link Locale#getDefault}
     * @return
     * </pre>
     */
    public String msg() {
        String message = "";
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), classLoader);
            message = resourceBundle.getString(this.msgCode);
        } catch (MissingResourceException e) {
            String errMsg = "IQEO0004E: The resource bundle cannot be found, or a resource is missing from a resource bundle. Message = " + e + " " + Arrays.toString(e.getStackTrace());
            if (logger.isErrorEnabled()) {
                logger.error(errMsg);
            }
        }
        return message;
    }

    /**
     * <pre>
     * Get the resource bundle message with no formatting options and given locale.
     * If formatting is required, see {@link #msg(Object[])}, {@link Locale}
     * @return
     * </pre>
     */
    public String msg(Locale locale) {
        String message = "";

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale, classLoader);
            message = resourceBundle.getString(this.msgCode);
        } catch (MissingResourceException e) {
            String errMsg = "IQEO0004E: The resource bundle cannot be found, or a resource is missing from a resource bundle. Message = " + e + " " + Arrays.toString(e.getStackTrace());
            if (logger.isErrorEnabled()) {
                logger.error(errMsg);
            }
        }
        return message;
    }

    /**
     * <pre>
     * Get the resource bundle message with formatting options and the default locale for this instance of
     * the java virtual machine.
     * If formatting is required, see {@link #msg(Object[])}, {@link Locale#getDefault}
     * @return
     * </pre>
     */
    public String msg(Object[] msg) {
        MessageFormat msgfmt = null;

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), classLoader);
            String message = resourceBundle.getString(this.msgCode);
            msgfmt = new MessageFormat(message);
        } catch (MissingResourceException e) {
            String errMsg = "IQEO0004E: The resource bundle cannot be found, or a resource is missing from a resource bundle. Message = " + e + " " + Arrays.toString(e.getStackTrace());
            if (logger.isErrorEnabled()) {
                logger.error(errMsg);
            }
        }
        return msgfmt.format(msg);
    }

    /**
     * <pre>
     * Get the resource bundle message with formatting options and given locale.
     * If formatting is required, see {@link #msg(Object[])}, {@link Locale}
     * @return
     * </pre>
     */
    public String msg(Object[] msg, Locale locale) {
        MessageFormat msgfmt = null;

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale, classLoader);
            String message = resourceBundle.getString(this.msgCode);
            msgfmt = new MessageFormat(message);
        } catch (MissingResourceException e) {
            String errMsg = "IQEO0004E: The resource bundle cannot be found, or a resource is missing from a resource bundle. Message = " + e + " " + Arrays.toString(e.getStackTrace());
            if (logger.isErrorEnabled()) {
                logger.error(errMsg);
            }
        }
        return msgfmt.format(msg);
    }
}
