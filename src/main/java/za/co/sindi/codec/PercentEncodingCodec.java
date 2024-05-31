/**
 * 
 */
package za.co.sindi.codec;

import java.io.CharArrayWriter;
import java.io.IOException;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * Percent-encoding according to specification found in RFC 3986, paragraph 2.1.
 * 
 * @author Buhake Sindi
 * @since 13 February 2012
 *
 */
public class PercentEncodingCodec extends CharacterCodec {
	
//	private static final byte[] UNRESERVED_CHARACTERS = {
//		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
//		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
//		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ,'-', '.', '_', '~'
//	};
	
	private static final char[] HEXADECIMAL_DIGITS_UPPERCASE = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	private static final char[] HEXADECIMAL_DIGITS_LOWERCASE = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	
	private static final char PERCENT_CHARACTER = '%';
	
	//Whether the hexadecimal digits be in uppercase or not (default false).
	private boolean toUpperCase = false;

	/**
	 * 
	 */
	public PercentEncodingCodec() {
		this(false);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param toUpperCase
	 */
	public PercentEncodingCodec(boolean toUpperCase) {
		super();
		this.toUpperCase = toUpperCase;
	}

	/* (non-Javadoc)
	 * @see com.neurologic.codec.Decoder#decode(byte[])
	 */
	public char[] decode(char[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null || data.length == 0) {
			return data;
		}
		
		CharArrayWriter caw = new CharArrayWriter();
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
			int index = i;
			if(c == PERCENT_CHARACTER) {
				caw.write(toCharacter(data[++i], data[++i], index));
			} else {
				caw.write(c);
			}
		}
	
		return caw.toCharArray();
	}
	
	private char toCharacter(char char1, char char2, int index) throws DecodingException {
		int digit1 = Character.digit(char1, 16);
		int digit2 = Character.digit(char2, 16);
		
		if (digit1 == -1) {
			throw new DecodingException("Invalid character '" + char1 + "' on index " + (index + 1));
		}
		
		if (digit2 == -1) {
			throw new DecodingException("Invalid character '" + char2 + "' on index " + (index + 2));
		}
		
		return (char) (((digit1 << 4) | digit2) & 0xFF);
	}

	/* (non-Javadoc)
	 * @see com.neurologic.codec.Encoder#encode(byte[])
	 */
	public char[] encode(char[] data) throws EncodingException {
		// TODO Auto-generated method stub
		if (data == null || data.length == 0) {
			return data;
		}
		
		CharArrayWriter caw = new CharArrayWriter();
		try {
			for (char c : data) {
				if((c >= 0x30 && c <= 0x39) //DIGITS [0..9] 
				 ||(c >= 0x41 && c <= 0x5A) //CHARACTERS ['A'..'Z']
				 ||(c >= 0x61 && c <= 0x7A) //CHARACTERS ['a'..'z']
				 ||(c == 0x2D) //HYPHEN
				 ||(c == 0x20) //SPACE
				 ||(c == 0x2E) //PERIOD
				 ||(c == 0x5F) //UNDERSCORE
				 ||(c == 0x7E) //TILDE
				) {
					caw.write(c);
				} else {
					caw.write(PERCENT_CHARACTER);
					caw.write(toHexDigits(c));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// This is actually never thrown....just catching nothing really.
			throw new RuntimeException(e);
		}
		
		return caw.toCharArray();
	}
	
	private char[] toHexDigits(char c) {
		char[] hexDigit = new char[2];
		int v0 = (c & 0xF0) >>> 4;
		int v1 = (c & 0x0F);
		hexDigit[0] = toUpperCase ? HEXADECIMAL_DIGITS_UPPERCASE[v0] : HEXADECIMAL_DIGITS_LOWERCASE[v0];
		hexDigit[1] = toUpperCase ? HEXADECIMAL_DIGITS_UPPERCASE[v1] : HEXADECIMAL_DIGITS_LOWERCASE[v1];
		
		return hexDigit;
	}
}
