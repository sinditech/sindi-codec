/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;

/**
 * @author Bienfait Sindi
 * @since 24 February 2015
 *
 */
public interface CharacterEncoder extends Encoder {

	public char[] decode(char[] data) throws DecodingException;
}
