/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Buhake Sindi
 * @since 21 October 2011
 *
 */
public interface Encoder<T> {

	public T encode(T data) throws EncodingException;
}
