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
public interface CharacterDecoder extends Decoder {

	public char[] decode(char[] data) throws DecodingException;
}
