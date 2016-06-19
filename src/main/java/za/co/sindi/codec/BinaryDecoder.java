/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;

/**
 * @author Bienfait Sindi
 * @since 25 February 2015
 *
 */
public interface BinaryDecoder extends Decoder {

	public byte[] decode(byte[] data) throws DecodingException;
}
