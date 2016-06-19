/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Bienfait Sindi
 * @since 24 February 2015
 *
 */
public interface BinaryEncoder extends Encoder {

	public byte[] encode(byte[] data) throws EncodingException;
}
