/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Bienfait Sindi
 * @since 13 March 2014
 *
 */
public class HexCodec extends BinaryCodec {
	
	private static final byte[] HEX_DIGITS_LOWERCASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static final byte[] HEX_DIGITS_UPPERCASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	//Whether the hexadecimal digits be in uppercase or not (default false).
	private boolean toUpperCase = false;
	
	/**
	 * 
	 */
	public HexCodec() {
		this(false);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param toUpperCase
	 */
	public HexCodec(boolean toUpperCase) {
		super();
		this.toUpperCase = toUpperCase;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.codec.Decoder#decode(byte[])
	 */
	public byte[] decode(byte[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null || data.length == 0) {
			return data;
		}
		
		int length = data.length;
		if (length % 2 != 0) {
			throw new DecodingException("Character length of data is not of even length.");
		}
		
		byte[] output = new byte[length / 2];
		int index = 0;
		for (int i = 0; i < output.length; i++) {
			int value = toDigit((char) data[index], index) << 4;
			index++;
			value = value | toDigit((char) data[index], index);
			index++;
			output[i] = (byte) (value & 0xFF);
		}
		
		return output;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.codec.Encoder#encode(byte[])
	 */
	public byte[] encode(byte[] data) throws EncodingException {
		// TODO Auto-generated method stub
		if (data == null || data.length == 0) {
			return data;
		}
		
		byte[] hexDigits = toUpperCase ? HEX_DIGITS_UPPERCASE : HEX_DIGITS_LOWERCASE;
		byte[] output = new byte[data.length * 2];
		int index = 0;
		for (byte b : data) {
			output[index++] = hexDigits[((b & 0xF0) >>> 4)];
			output[index++] = hexDigits[(b & 0x0F)];
		}
		
		return output;
	}
	
	private int toDigit(char character, int index) throws DecodingException {
		int value = Character.digit(character, 16);
		if (value == -1) {
			throw new DecodingException("Illegal hexadecimal character " + character + " found on index " + index);
		}
		
		return value;
	}
}
