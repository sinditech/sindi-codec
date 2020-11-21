/**
 * 
 */
package za.co.sindi.codec;

import java.io.CharArrayWriter;

import za.co.sindi.codec.exception.DecodingException;
import za.co.sindi.codec.exception.EncodingException;

/**
 * Implementation of Section 6.7 of RFC 204 (Rule 1 through 5).
 * 
 * @author Buhake Sindi
 * @since 22 October 2011
 *
 */
public class QuotedPrintableCodec extends CharacterCodec {
	
	//Characters that can appear as themselves.
	private static final char TAB = 9;
	private static final char SPACE = 32;
	private static final char CR = 13;
	private static final char LF = 10;
	private static final char ESPACE_CHARACTER = '=';
//	private static final char[] ENCODABLE_CHARACTERS = {'!', '"', '#', '$', '@', '[', '\\', ']', '^', '`', '{', '|', '}', '~'};
	private static final char[] ALLOWED_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private boolean encodeCrLf;
//	private static ArrayList<Byte> encodableCharacterList;
	
//	static {
//		encodableCharacterList = new ArrayList<Byte>();
//		for (char b : ENCODABLE_CHARACTERS) {
//			encodableCharacterList.add(b);
//		}
//	}
	
	/**
	 * 
	 */
	public QuotedPrintableCodec() {
		this(false);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param encodeCrLf
	 */
	public QuotedPrintableCodec(boolean encodeCrLf) {
		super();		
		this.encodeCrLf = encodeCrLf;
	}

	public char[] decode(char[] data) throws DecodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int c = 0;
		CharArrayWriter caw = new CharArrayWriter();
		for (c = 0; c  < data.length; c++) {
			char b = data[c];
			
			if (b != ESPACE_CHARACTER) {
				caw.write(b);
			} else {
				char b1 = data[++c];
				char b2 = data[++c];
				
				if (b1 != CR && b2 != LF) {
					if (!isCharacterAllowed(b1) || !isCharacterAllowed(b2)) {
						throw new DecodingException("Encoded value '=" + new String(new char[] {b1, b2}) + "' is not allowed.");
					}
					
					caw.write((b1 << 4) | (b2));
				}
			}
		}
		
		return caw.toCharArray();
	}
	
	private boolean isCharacterAllowed(char b) {
		for (char a : ALLOWED_CHARACTERS) {
			if (b == a) {
				return true;
			}
		}
		
		return false;
	}

	public char[] encode(char[] data) throws EncodingException {
		// TODO Auto-generated method stub
		if (data == null) {
			return null;
		}
		
		int c = 0;
		int lineCursor = 0;
		CharArrayWriter caw = new CharArrayWriter();
		
		for (c = 0; c < data.length; c++) {
			char b = data[c];
			
			//Find how many characters we're going to put in....
			if (b == TAB || b == CR || b == LF || b == SPACE) {
				if (lineCursor > 72 && lineCursor + 3 < 76) {
					writeAsQuotedPrintable(caw, b);
					lineCursor += 3;
				} else {
					if ((b == CR || b == LF) && encodeCrLf) {
						writeAsQuotedPrintable(caw, b);
						lineCursor += 3;
					} else {
						caw.write(b);
						lineCursor++;
					}
				}
			} else if ((b >= 33 & b <= 60) || (b >= 62 && b <= 126)) {
				if (lineCursor + 1 > 75) {
					writeQuotedPrintableNewLine(caw);
					lineCursor = 0;
				}
				
				caw.write(b);
				lineCursor++;
			} else {
				if (lineCursor + 3 > 75) {
					writeQuotedPrintableNewLine(caw);
					lineCursor = 0;
				}
				
				writeAsQuotedPrintable(caw, b);
				lineCursor += 3;
			}
		}
		
		return caw.toCharArray();
	}
	
	private void writeQuotedPrintableNewLine(CharArrayWriter caw) {
		caw.write(ESPACE_CHARACTER);
		caw.write(CR);
		caw.write(LF);
	}
	
	private void writeAsQuotedPrintable(CharArrayWriter caw, char b) {
		char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
		char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
		caw.write(ESPACE_CHARACTER);
		caw.write(hex1);
		caw.write(hex2);
	}
	
	public static String decode(String s) throws DecodingException {
		return new String(new QuotedPrintableCodec().decode(s.toCharArray()));
	}
	
	public static String encode(String s) throws EncodingException {
		return new String(new QuotedPrintableCodec().encode(s.toCharArray()));
	}
}
