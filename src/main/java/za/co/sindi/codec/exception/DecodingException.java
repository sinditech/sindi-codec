/**
 * 
 */
package za.co.sindi.codec.exception;

/**
 * @author Buhake Sindi
 * @since 21 October 2011
 *
 */
public class DecodingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7257247607717631534L;

	/**
	 * @param message
	 */
	public DecodingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DecodingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DecodingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
