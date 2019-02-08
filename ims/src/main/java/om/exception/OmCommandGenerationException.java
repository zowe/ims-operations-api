/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.exception;

/**
 * Checked exception used to signal that a command was not able to be generated for a given request. 
 * This would typically occur because the serialization occuring did not have a correct object or JSON 
 * payload to start with. This type of error although checked, will often be wrapped as a runtime exception 
 * and thrown as an unchecked exception. 
 * 
 * <pre>
 * Example:
 *   String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
 *   OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
 *   throw omCommandGenerationException;
 * </pre>
 *
 */
public class OmCommandGenerationException extends Exception {
	
	
	private static final long serialVersionUID = -4900520258299409271L;
	private String parentExceptionName = "";
	
	/**
	 * To throw an exception whith only a message.
	 * @see #OmCommandGenerationException(String, Throwable)
	 * @param message
	 */
	public OmCommandGenerationException(String message) {
		super(message);
	}
	
	/**
	 * Throw an exception with only the cause. This has will wrap an existing throwable with 
	 * an {@link #OmCommandGenerationException}
	 * @see #OmCommandGenerationException(String, Throwable)
	 * @param cause
	 */
	public OmCommandGenerationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Throw an exception with a detailed message and throwable. This is the preferred way to 
	 * use this exception when applicable, not only is the throwable presrved the throwable's 
	 * class name is set in this exception for later analysis. 
	 * @see OmCommandGenerationException#getParentExceptionName()
	 * @param message
	 * @param cause
	 * <pre>
	 * Example:
	 *   String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
	 *   OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
	 *   throw omCommandGenerationException;
	 * </pre>
	 */
	public OmCommandGenerationException(String message, Throwable cause) {
		super(message, cause);
		this.parentExceptionName = cause.getClass().getSimpleName();
	}
	
	/**
	 * Returns the parent exception that this exception might have wrapped.
	 * This is useful when the exception intercepts a throwable and generailizes into
	 * one exception type.
	 * @return String, parent class name
	 */
	public String getParentExceptionName(){
	    return this.parentExceptionName;
	}
}
	
	
	
