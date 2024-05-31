/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;

/**
 * @author Buhake Sindi
 * @since 21 October 2011
 *
 */
public interface Decoder<T> {

	public T decode(T data) throws DecodingException;
}
