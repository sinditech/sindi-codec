/**
 * 
 */
package za.co.sindi.codec;

import java.nio.charset.StandardCharsets;

/**
 * @author Bienfait Sindi
 * @since 17 June 2017
 *
 */
public final class Charsets {

	private Charsets() {
		throw new AssertionError("Private Constructor.");
	}
	
	public static String asASCIIString(final byte[] bytes) {
		return new String(bytes, StandardCharsets.US_ASCII);
	}
	
	public static byte[] toASCIIBytes(final String s) {
		return s.getBytes(StandardCharsets.US_ASCII);
	}
	
	public static String asUTF8String(final byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static byte[] toUTF8Bytes(final String s) {
		return s.getBytes(StandardCharsets.UTF_8);
	}
	
	public static String asUTF16String(final byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_16);
	}
	
	public static byte[] toUTF16Bytes(final String s) {
		return s.getBytes(StandardCharsets.UTF_16);
	}
	
	public static String asUTF16BEString(final byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_16BE);
	}
	
	public static byte[] toUTF16BEBytes(final String s) {
		return s.getBytes(StandardCharsets.UTF_16BE);
	}
	
	public static String asUTF16LEString(final byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_16LE);
	}
	
	public static byte[] toUTF16LEBytes(final String s) {
		return s.getBytes(StandardCharsets.UTF_16LE);
	}
	
	public static String asISO8859_1String(final byte[] bytes) {
		return new String(bytes, StandardCharsets.ISO_8859_1);
	}
	
	public static byte[] toISO8859_1Bytes(final String s) {
		return s.getBytes(StandardCharsets.ISO_8859_1);
	}
}
