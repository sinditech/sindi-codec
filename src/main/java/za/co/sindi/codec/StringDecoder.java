/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;

/**
 * @author Bienfait Sindi
 * @since 14 January 2016
 *
 */
public interface StringDecoder extends Decoder {

	public String decode(String data) throws DecodingException;
}
