/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.exception.message;

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
public enum OM_EXCEPTION {
	/**  OMSERVLET_OM_EXCEPTION_TITTLE = An error occurred while communicating with Operations Manager. */
    OM_EXCEPTION_TITTLE("OM_EXCEPTION_TITTLE"),
    
    /**  OMSERVLET_OM_EXCEPTION_MESG = An error occurred while communicating with Operations Manager. The command is: {0}. The error return code is: {1}. The reason code is: {2}. The reason message code is: {3}. The reason text is: {4}. The error number is: {5}. */
    OM_EXCEPTION_MESG("OM_EXCEPTION_MESG"),
    
	;
	
	private String msgCode;

	private OM_EXCEPTION(String msgCode){
	    this.msgCode = msgCode;
	}
	
    /* *******************************************************************************/
    /* * Resource bundle helpers
    /* *******************************************************************************/
    final static Logger logger             = LoggerFactory.getLogger(OM_EXCEPTION.class);
    private ClassLoader classLoader        = this.getClass().getClassLoader();
    private String      resourceBundleName = this.getClass().getName();

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
