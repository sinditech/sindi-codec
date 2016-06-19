/**
 * 
 */
package za.co.sindi.codec;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Buhake Sindi
 * @since 24 October 2011
 *
 */
public class Base32Codec extends BinaryCodec {

	private static final byte[] BASE_32_CHARACTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'2', '3', '4', '5', '6', '7'
	};
	
	private static final byte[] BASE_32_DECODE_TABLE = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, // 20-2f
		-1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, // 30-3f 2-7
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, // 40-4f A-N
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 50-5a O-Z
	};
	 
	private static final int REMAINDER_PADDING_COUNT[] = {0, 6, 4, 3, 1};
	
	private static final byte PADDING = '=';
	
	/* (non-Javadoc)
	 * @see com.neurologic.codec.Decoder#decode(byte[])
	 */
	public byte[] decode(byte[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int length = data.length;
		if (length % 8 != 0) {
			throw new DecodingException("Data must be divisible by 8.");
		}
		
		int c, cursor = 0;
		int noPadding = 0;
		while (data[length - noPadding - 1] == PADDING) {
			noPadding++;
		}
		
		if (noPadding > 6) {
			throw new DecodingException("Inconsistent padding found (padding length = " + noPadding + ").");
		}
		
		byte[] result = new byte[(length - noPadding) * 5 / 8];
		
		int maxGrouping = (length - noPadding)/8;
		for (c = 0; c < (maxGrouping * 8); c+= 8) {
			while (BASE_32_DECODE_TABLE[data[c]] == -1) {
				c++;
			}
			
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c]] << 3) | (BASE_32_DECODE_TABLE[data[c + 1]] >> 2));
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 1]] << 6) | (BASE_32_DECODE_TABLE[data[c + 2]] << 1) | (BASE_32_DECODE_TABLE[data[c + 3]] >> 4));
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 3]] << 4) | (BASE_32_DECODE_TABLE[data[c + 4]] >> 1));
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 4]] << 7) | (BASE_32_DECODE_TABLE[data[c + 5]] << 2) | (BASE_32_DECODE_TABLE[data[c + 6]] >> 3));
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 6]] << 5) | BASE_32_DECODE_TABLE[data[c + 7]]);
		}
		
		if (noPadding > 0) {
			result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c]] << 3) | (BASE_32_DECODE_TABLE[data[c + 1]] >> 2));
			
			if (noPadding == 4) {
				result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 1]] << 6) | (BASE_32_DECODE_TABLE[data[c + 2]] << 1));
			} else if (noPadding <= 3) {
				result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 1]] << 6) | (BASE_32_DECODE_TABLE[data[c + 2]] << 1) | (BASE_32_DECODE_TABLE[data[c + 3]] >> 4));
				result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 3]] << 4) | (BASE_32_DECODE_TABLE[data[c + 4]] >> 1));
				
				if (noPadding == 1) {
					result[cursor++] = (byte) ((BASE_32_DECODE_TABLE[data[c + 4]] << 7) | (BASE_32_DECODE_TABLE[data[c + 5]] << 2) | (BASE_32_DECODE_TABLE[data[c + 6]] >> 3));
				}
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.neurologic.codec.Encoder#encode(byte[])
	 */
	public byte[] encode(byte[] data) throws EncodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int length = data.length;
		int maxGrouping = length / 5;
		int fullGroupings = 5 * maxGrouping;
		int remainderBytes = length - fullGroupings;
		
		int c, c0, c1, c2, c3, c4,  c5, c6, c7, cursor = 0;
		int maxPadding = REMAINDER_PADDING_COUNT[remainderBytes];
		byte[] result = new byte[8*(maxGrouping + 1)];
		
//		data = Arrays.copyOf(data, length + maxPadding);
		for (c = 0; c < fullGroupings; c+= 5) {
			
			c0 = ((data[c] & 0xF8) >> 3);
			c1 = ((data[c] & 0x07) << 2) | ((data[c + 1] & 0xC0) >> 6);
			c2 = ((data[c + 1] & 0x3E) >> 1);
			c3 = ((data[c + 1] & 0x01) << 4) | ((data[c + 2] & 0xF0) >> 4);
			c4 = ((data[c + 2] & 0x0F) << 1) | ((data[c + 3] & 0x80) >> 7);
			c5 = ((data[c + 3] & 0x7C) >> 2);
			c6 = ((data[c + 3] & 0x03) << 3) | ((data[c + 4] & 0xE0) >> 5);
			c7 = (data[c + 4] & 0x1F);
			
			//write
			result[cursor++] = BASE_32_CHARACTERS[c0];
			result[cursor++] = BASE_32_CHARACTERS[c1];
			result[cursor++] = BASE_32_CHARACTERS[c2];
			result[cursor++] = BASE_32_CHARACTERS[c3];
			result[cursor++] = BASE_32_CHARACTERS[c4];
			result[cursor++] = BASE_32_CHARACTERS[c5];
			result[cursor++] = BASE_32_CHARACTERS[c6];
			result[cursor++] = BASE_32_CHARACTERS[c7];
		}
		
		//Padding
		if (maxPadding > 0) {
			c0 = ((data[c] & 0xF8) >> 3);
			result[cursor++] = BASE_32_CHARACTERS[c0];
			
			if (remainderBytes == 1) {
				c1 = ((data[c] & 0x07) << 2);
				result[cursor++] = BASE_32_CHARACTERS[c1];
			} else {
				c1 = ((data[c] & 0x07) << 2) | ((data[c + 1] & 0xC0) >> 6);
				c2 = ((data[c + 1] & 0x3E) >> 1);
				
				result[cursor++] = BASE_32_CHARACTERS[c1];
				result[cursor++] = BASE_32_CHARACTERS[c2];
				
				if (remainderBytes == 2) {
					c3 = ((data[c + 1] & 0x01) << 4);
					result[cursor++] = BASE_32_CHARACTERS[c3];
				} else {
					c3 = ((data[c + 1] & 0x01) << 4) | ((data[c + 2] & 0xF0) >> 4);
					result[cursor++] = BASE_32_CHARACTERS[c3];
					
					if (remainderBytes == 3) {
						c4 = ((data[c + 2] & 0x0F) << 1);
						result[cursor++] = BASE_32_CHARACTERS[c4];
					} else {
						c4 = ((data[c + 2] & 0x0F) << 1) | ((data[c + 3] & 0x80) >> 7);
						c5 = ((data[c + 3] & 0x7C) >> 2);
						c6 = ((data[c + 3] & 0x03) << 3);
						
						result[cursor++] = BASE_32_CHARACTERS[c4];
						result[cursor++] = BASE_32_CHARACTERS[c5];
						result[cursor++] = BASE_32_CHARACTERS[c6];
					}
				}
			}
			
			//Finally...
			for (c = 0; c < maxPadding; c++) {
				result[cursor++] = PADDING;
			}
		}
		
		return result;
	}
	
	public static String encode(String s) throws EncodingException {
		return new String(new Base32Codec().encode(s.getBytes()));
	}
	
	public static String decode(String s) throws DecodingException {
		return new String(new Base32Codec().decode(s.getBytes()));
	}
	
//	public static void main(String[] args) {
//		
//		try {
////			String s = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
//			String s = "A";
//			System.out.println("Original length: " + s.length());
//			Encoder encoder = new Base32Codec();
//			
//			byte[] enc = encoder.encode(s.getBytes());
//			System.out.println(enc.length);
//			String encStr = new String(enc);
//			System.out.println(encStr.length());
//			System.out.println(encStr);
//			
//			//Decoder
//			Decoder decoder = new Base32Codec();
//			byte[] dec = decoder.decode(enc);
//			System.out.println(dec.length);
//			String decStr = new String(dec);
//			System.out.println(decStr.length());
//			System.out.println(decStr);
//			
//		} catch (EncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DecodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
