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
public enum OM_MESSAGE { 
	/**  OM_NON_ZERO_RC_TITTLE = Operations Manager returned a non-zero return code. */
	OM_NON_ZERO_RC_TITTLE("OM_NON_ZERO_RC_TITTLE"),
    
    /**  OM_MBR_NON_ZERO_RC_TITTLE = Operations Manager Member returned a non-zero return code. */
    OM_MBR_NON_ZERO_RC_TITTLE("OM_MBR_NON_ZERO_RC_TITTLE"),
    
    /** OM_COMMAND =  IMS Command: {0} */
    OM_COMMAND("OM_COMMAND"),
    
    /** OM_MBR_NAME =  Member Name: {0}  */
    OM_MBR_NAME("OM_MBR_NAME"),
    
    /**  OM_RETURN_CODE =  Return Code: {0} */
    OM_RETURN_CODE("OM_RETURN_CODE"),
    
    /**  OM_REASON_CODE =  Reason Code: {0} */
    OM_REASON_CODE("OM_REASON_CODE"),
    
    /**  OM_REASON_TEXT =  Reason Text: {0} */
    OM_REASON_TEXT("OM_REASON_TEXT"),
    
    /**  OM_MEASSAGES =  Messages: {0} */
    OM_MEASSAGES("OM_MEASSAGES"),
    
    /** OM_REASON_MEASSAGE =  Reason Message: {0}*/
    OM_REASON_MEASSAGE("OM_REASON_MEASSAGE"),
    
    /** OM_ZERO_RC_TITTLE = Operations Manager has successfully executed the command.*/
    OM_ZERO_RC_TITTLE("OM_ZERO_RC_TITTLE"),
    
    /** OM_MBR_ZERO_RC_TITTLE = Operations Manager Member has successfully executed the command.*/
    OM_MBR_ZERO_RC_TITTLE("OM_MBR_ZERO_RC_TITTLE"),
    
    /**  FORMAT_DELIMITER =  <br> */
    FORMAT_DELIMITER("FORMAT_DELIMITER"),
    
    /**  OM_URL_CSL_RC_RSN_CODE = http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=%2Fcom.ibm.ims{0}.doc.spr%2Fims_cslommcmdreq_csl.htm */
    OM_URL_CSL_RC_RSN_CODE("OM_URL_CSL_RC_RSN_CODE"),
            
    /**  OM_URL_COMPONENT_RC_RSN_CODE = http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=%2Fcom.ibm.imsmsgs.doc.msgs%2Fcompcodes%2Fims_cmdomcodes.html */
    OM_URL_COMPONENT_RC_RSN_CODE("OM_URL_COMPONENT_RC_RSN_CODE"),
    
    /** OM_LIVE_MODE_MSG = "Management Console is operating in LIVE mode, this occur if discovery was not performed or the product was upgraded requireing a re-discovery." **/
    OM_LIVE_MODE_MSG("OM_LIVE_MODE_MSG")

	;
	
	private String msgCode;

	private OM_MESSAGE(String msgCode){
	    this.msgCode = msgCode;
	}
	
    /* *******************************************************************************/
    /* * Resource bundle helpers
    /* *******************************************************************************/
    final static Logger logger             = LoggerFactory.getLogger(OM_MESSAGE.class);
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
            ResourceBundle resourceBundle = ResourceBundle.getBundle("OM_MESSAGE", Locale.getDefault(), classLoader);
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
            ResourceBundle resourceBundle = ResourceBundle.getBundle("OM_MESSAGE", Locale.getDefault(), classLoader);
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
