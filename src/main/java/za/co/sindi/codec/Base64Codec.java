/**
 * 
 */
package za.co.sindi.codec;

import java.io.UnsupportedEncodingException;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Buhake Sindi
 * @since 21 October 2011
 *
 */
public class Base64Codec extends BinaryCodec {

	private static final byte[] STANDARD_BASE_64_CHARACTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};
	
	private static final byte[] URL_SAFE_BASE_64_CHARACTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
	};
	
	private static final byte[] STANDARD_BASE_64_DECODE_INDEX = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
        55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
        24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
        35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
	};
	
	private static final byte[] URL_SAFE_BASE_64_DECODE_INDEX = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54,
        55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
        24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
        35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
	};
	
	private static final byte[] NEWLINE = {'\r', '\n'};
	
	private static final char PADDING = '=';
	
	private byte[] encodingTable;
	private byte[] decodingTable;
	private boolean urlSafe;
	private int lineLength;
	private byte[] lineSeparator;
	private boolean doPadding;

	/**
	 * @param urlSafe
	 * @param lineLength
	 * @param lineSeparator
	 * @param doPadding
	 */
	private Base64Codec(boolean urlSafe, int lineLength, byte[] lineSeparator, boolean doPadding) {
		super();
		
		if (lineLength > 0 && lineSeparator == null) {
			throw new IllegalArgumentException("No line separators was provided.");
		}
		
		if (lineLength > 0 && containsAlphabetOrPad(lineSeparator)) {
			try {
				throw new IllegalArgumentException(new String(lineSeparator, "UTF-8") + " contains Base 64 characters.");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace(); //THIS SHOULD NEVER HAPPEN...
			}
		}
		
		if (urlSafe) {
			encodingTable = URL_SAFE_BASE_64_CHARACTERS;
			decodingTable = URL_SAFE_BASE_64_DECODE_INDEX;
		} else {
			encodingTable = STANDARD_BASE_64_CHARACTERS;
			decodingTable = STANDARD_BASE_64_DECODE_INDEX;
		}
		
		this.urlSafe = urlSafe;
		this.lineLength = lineLength;
		this.lineSeparator = lineSeparator;
		this.doPadding = doPadding;
	}

//	/**
//	 * 
//	 */
//	public Base64Codec() {
//		this(false);
//		// TODO Auto-generated constructor stub
//	}
//	
//	/**
//	 * @param lineLength
//	 */
//	public Base64Codec(int lineLength) {
//		this(lineLength, NEWLINE);
//	}
//
//	/**
//	 * RFC 2045 Base 64 encoding
//	 * 
//	 * @param lineLength
//	 * @param lineSeparator
//	 */
//	public Base64Codec(int lineLength, byte[] lineSeparator) {
//		this(false);
//		if (lineLength > 0 && lineSeparator == null) {
//			throw new IllegalArgumentException("No line separators was provided.");
//		}
//		
//		if (lineLength > 0 && containsAlphabetOrPad(lineSeparator)) {
//			try {
//				throw new IllegalArgumentException(new String(lineSeparator, "UTF-8") + " contains Base 64 characters.");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace(); //THIS SHOULD NEVER HAPPEN...
//			}
//		}
//		
//		this.lineLength = lineLength;
//		this.lineSeparator = lineSeparator;
//	}
//
//	/**
//	 * 
//	 */
//	public Base64Codec(boolean urlSafe) {
//		super();
//		// TODO Auto-generated constructor stub
//		if (urlSafe) {
//			encodingTable = URL_SAFE_BASE_64_CHARACTERS;
//			decodingTable = URL_SAFE_BASE_64_DECODE_INDEX;
//		} else {
//			encodingTable = STANDARD_BASE_64_CHARACTERS;
//			decodingTable = STANDARD_BASE_64_DECODE_INDEX;
//		}
//	}
	
	public Base64Codec withoutPadding() {
		return new Base64Codec(urlSafe, lineLength, lineSeparator, false);
	}

	/* (non-Javadoc)
	 * @see com.neurologic.codec.Decoder#decode(byte[])
	 */
	public byte[] decode(byte[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
				
		int length = data.length;
		if (doPadding && length % 4 != 0) {
			throw new DecodingException("Data must be divisible by 4.");
		}
		
		int c, cursor = 0;
		int maxGrouping = length/4;
		int noPadding = 0;
		while (data[length - noPadding - 1] == PADDING) {
			noPadding++;
		}
		
		if (noPadding > 2) {
			throw new DecodingException("Inconsistent padding found (padding length = " + noPadding + ").");
		}
		
		byte[] result = new byte[3*(maxGrouping - noPadding) - (lineLength > 0 ? (length / lineLength) : 0)];
		
		for (c = 0; c < (4*(maxGrouping - (noPadding > 0 ? 1 : 0))); c+= 4) {
			while (decodingTable[data[c]] == -1) {
				c++;
			}
			
			result[cursor++] = (byte) ((decodingTable[data[c]] << 2) | (decodingTable[data[c + 1]] >> 4));
			result[cursor++] = (byte) ((decodingTable[data[c + 1]] << 4) | (decodingTable[data[c + 2]] >> 2));
			result[cursor++] = (byte) ((decodingTable[data[c + 2]] << 6) | decodingTable[data[c + 3]]);
		}
		
		if (noPadding > 0) {
			result[cursor++] = (byte) ((decodingTable[data[c]] << 2) | (decodingTable[data[c + 1]] >> 4));
			if (noPadding == 1) {
				result[cursor++] = (byte) ((decodingTable[data[c + 1]] << 4) | (decodingTable[data[c + 2]] >> 2));
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
		
		int c, c0, c1, c2, c3, cursor = 0;
		int length = data.length;
		int maxGrouping = length / 3;
		int remainderGrouping = length % 3;
		int maxPadding = 3 - remainderGrouping;
		int resultLength = remainderGrouping != 0 ? 4*(maxGrouping + 1) : (4 * maxGrouping);
		if (lineLength > 0) {
			resultLength += (4 * maxGrouping) == lineLength ? 0 : (4*maxGrouping / lineLength);
		}
//		byte[] result = new byte[doPadding ? resultLength : resultLength - (remainderGrouping != 0 ? maxPadding : remainderGrouping)];
		byte[] result = new byte[resultLength - (doPadding ? maxPadding : 0)];
		
		for (c = 0; c < (3*maxGrouping); c+= 3) {
			if (lineLength > 0 && c > 0 && (c * 4) % lineLength == 0) {
				for (byte b : lineSeparator) {
					result[cursor++] = b;
				}
			}
			
			//int b64 = (data[c] << 16) + (data[c + 1] << 8) + data[c + 2];
			c0 = ((data[c] & 0xFC) >> 2);
			c1 = ((data[c] & 0x03) << 4) | ((data[c + 1] & 0xF0) >> 4);
			c2 = ((data[c + 1] & 0x0F) << 2) | ((data[c + 2] & 0xC0) >> 6);
			c3 = (data[c + 2] & 0x3F);
			
			//write
			result[cursor++] = encodingTable[c0];
			result[cursor++] = encodingTable[c1];
			result[cursor++] = encodingTable[c2];
			result[cursor++] = encodingTable[c3];
		}
		
		//Padding
		if (remainderGrouping > 0) {
			c0 = ((data[c] & 0xFC) >> 2);
			result[cursor++] = encodingTable[c0];
			
			if (remainderGrouping == 1) {
				c1 =((data[c] & 0x03) << 4);
				result[cursor++] = encodingTable[c1];
			} else if (remainderGrouping == 2) {
				c1 = ((data[c] & 0x03) << 4) | ((data[c + 1] & 0xF0) >> 4);
				c2 = ((data[c + 1] & 0x0F) << 2);
				result[cursor++] = encodingTable[c1];
				result[cursor++] = encodingTable[c2];
			}
			
			if (doPadding) {
				for (c = 0; c < maxPadding; c++) {
					result[cursor++] = PADDING;
				}
			}
		}
		
		return result;
	}
	
	private boolean containsAlphabetOrPad(byte[] bytes) {
		if (bytes != null) {
			for (byte b : bytes) {
				if (b == PADDING || STANDARD_BASE_64_DECODE_INDEX[b] != -1 || URL_SAFE_BASE_64_DECODE_INDEX[b] != -1) {
					return true;
				}
			}
		}
		
		return false;
	}
	
//	public String encode(String s, String charset) throws EncodingException {
//		try {
////			return new String(new Base64Codec().encode(s.getBytes(charset)));
//			return new String(encode(s.getBytes(charset)));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			throw new EncodingException(e);
//		}
//	}
//	
//	public String decode(String s, String charset) throws DecodingException {
//		try {
////			return new String(new Base64Codec().decode(s.getBytes()), charset);
//			return new String(decode(s.getBytes()), charset);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			throw new DecodingException(e);
//		}
//	}
	
//	public static BinaryEncoder getEncoder() {
//		return new Base64Codec(false, 0, null, true);
//	}
//	
//	public static BinaryDecoder getDecoder() {
//		return new Base64Codec(false, 0, null, true);
//	}
//	
//	public static BinaryEncoder getUrlSafeEncoder() {
//		return new Base64Codec(true, 0, null, true);
//	}
//	
//	public static BinaryDecoder getUrlSafeDecoder() {
//		return new Base64Codec(true, 0, null, true);
//	}
//	
//	public static BinaryEncoder getMimeEncoder() {
//		return new Base64Codec(true, 76, NEWLINE, true);
//	}
//	
//	public static BinaryDecoder getMimeDecoder() {
//		return new Base64Codec(true, 76, NEWLINE, true);
//	}
	
	public static Base64Codec getBase64Codec() {
		return new Base64Codec(false, 0, null, true);
	}
	
	public static Base64Codec getBase64UrlSafeCodec() {
		return new Base64Codec(true, 0, null, true);
	}
	
	public static Base64Codec getBase64MimeCodec() {
		return new Base64Codec(true, 76, NEWLINE, true);
	}
	
//	public static void main(String[] args) {
//		try {
//			System.out.println(encode("123456789", "UTF-8") + ">");
////			System.out.println(encode("abcd", "UTF-8") + ">");
//			System.out.println("e881d08f89856ff96bee2e12d44567ac6d50b9e1ca891472d36dfd4c:".length());
//			System.out.println(Base64Codec.encode("e881d08f89856ff96bee2e12d44567ac6d50b9e1ca891472d36dfd4c:", "UTF-8") + ">");
//		} catch (EncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
