/**
 * 
 */
package za.co.sindi.codec;

import java.io.ByteArrayOutputStream;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * @author Buhake Sindi
 * @since 24 October 2011
 *
 */
public class ASCII85Codec extends BinaryCodec {
	
//	private static final byte[] BASE_85_CHARACTERS = {
//		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
//		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '#', '$', '%', '&', '(',
//        ')', '*', '+', '-', ';', '<', '=', '>', '?', '@', '^', '_', '`', '{', '|', '}', '~'
//	};
	
	private static final byte[] START_TOKEN = {'<', '~'};
	private static final byte[] END_TOKEN = {'~', '>'};
//	private static final byte[] NEWLINE = { '\r', '\n'};
	private static final int[] POW85 = { 85*85*85*85, 85*85*85, 85*85, 85, 1};
	private static final int[] MASK = {0xFF000000, 0xFF0000, 0xFF00, 0xFF};
	
	private boolean encloseEncoding;

	/**
	 * 
	 */
	public ASCII85Codec() {
		this(true);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param encloseEncoding
	 */
	public ASCII85Codec(boolean encloseEncoding) {
		super();
		this.encloseEncoding = encloseEncoding;
	}

	public byte[] decode(byte[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int length = data.length;
		int c = 0;
		if (encloseEncoding) {
			if (!(data[0] == START_TOKEN[0] && data[1] == START_TOKEN[1])) {
				throw new DecodingException("Couldn't find start token " + new String(START_TOKEN) + ".");
			}
			
			if (!(data[length - 1] == END_TOKEN[1] && data[length - 2] == END_TOKEN[0])) {
				throw new DecodingException("Couldn't find end token " + new String(END_TOKEN) + ".");
			}
			
			c += 2;
			length -= 2;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (; c < length; c++) {
			if (data[c] == 'z') {
				baos.write(0);
			} else {
				//Break if we're close to the end of the stream
				if (c + 5 >= length) {
					break;
				}
				
				int value = 0;
				for (int pow85 : POW85) {
					value += (data[c++] - 33)*pow85;
				}
				//revert count;
				c--;
				
				for (int cursor = 0; cursor < MASK.length; cursor++) {
					baos.write((value & MASK[cursor]) >> 24 - (8*cursor));
				}
			}
		}
		
		//Do we still have bytes left?
		if (c < length) {
			int value = 0;
			int paddingNeeded = 5 - (length - c);
			int cursor = 0;
			
			for (; c < length; c++) {
				if (data[c] == 'z') {
					baos.write(0);
				} else {
					value += (data[c] - 33)*POW85[cursor++]; 
				}
			}
			
			for (c = 0; c < paddingNeeded; c++) {
				value += ('u' - 33)*POW85[cursor + c];
			}
			
			for (c = 0; c < cursor - 1; c++) {
				baos.write((value & MASK[c]) >> 24 - (8*c));
			}
//			
//			baos.write((value & 0xFF0000) >> 16);
//			baos.write((value & 0xFF00) >> 8);
//			baos.write((value & 0xFF));
		}
		
		return baos.toByteArray();
	}

	public byte[] encode(byte[] data) throws EncodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int length = data.length;
		int noFullGroups = length / 4;
		int remainderPadding = 4 - (length % 4);
		byte[] result = new byte[5*noFullGroups + (5 - remainderPadding) + /*(5*noFullGroups / 76) +*/ (encloseEncoding ? 4 : 0)];
		int c = 0, cursor = 0;
//		int zCount = 0;
		
		//Start Token
		if (encloseEncoding) {
			for (byte b : START_TOKEN) {
				result[cursor++] = b;
			}
		}
		
		for (c = 0; c < (4*noFullGroups); c+= 4) {
//			if (cursor > 0 && (cursor - zCount*4) % (encloseEncoding ? 77 : 75) == 0) {
//				for (byte b : NEWLINE) {
//					result[cursor++] = b;
//				}
//			}
			
			int value = (data[c] << 24) | (data[c + 1] << 16) | (data[c + 2] << 8) | (data[c + 3]);
			if (value == 0) {
				result[cursor++] = 'z';
//				zCount++;
			} else {
				for (int pow85 : POW85) {
					int r = value / pow85;
					result[cursor++] = (byte) (r + 33);
					value -= (r * pow85);
				}
			}
		}
		
		//Do last encoding
		if (c < length) {
			int value = 0;
			int paddingNeeded = 4 - (length - c);
			
			for (; c < length; c++) {
				value |= (data[c] << 8*(3 - c));
			}
			
			if (value == 0) {
				result[cursor++] = 'z';
			} else {
				for (int i = 0; i < 5 - paddingNeeded; i++) {
					int r = value / POW85[i];
					result[cursor++] = (byte) (r + 33);
					value -= (r * POW85[i]);
				}
			}
		}
		
		//End Token
		if (encloseEncoding) {
			for (byte b : END_TOKEN) {
				result[cursor++] = b;
			}
		}
		
		return result;
	}
}
