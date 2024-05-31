/**
 * 
 */
package za.co.sindi.codec.exception;

/**
 * @author Buhake Sindi
 * @since 21 October 2011
 *
 */
public class EncodingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4668616034686521739L;

	/**
	 * @param message
	 */
	public EncodingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public EncodingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EncodingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
