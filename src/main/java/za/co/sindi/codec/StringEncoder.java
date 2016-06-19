/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Bienfait Sindi
 * @since 14 January 2016
 *
 */
public interface StringEncoder extends Encoder {

	public String encode(String data) throws EncodingException;
}
