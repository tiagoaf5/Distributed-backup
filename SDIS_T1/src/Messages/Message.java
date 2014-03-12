package Messages;

import java.nio.charset.Charset;

public abstract class Message {
	protected String fileId;
	
	static final public Charset charset = Charset.forName("US-ASCII");
	static final protected byte CRLF = (byte) 0xDA;
	static final protected byte SPACE = 0x20;
	
	public Message() {
		
	}
	
	abstract public byte [] getMessage();
	
	abstract public boolean parseMessage(byte[] data);
	
	static public String getMessageType(byte[] msg) {
		String type = new String();
		
		int i = 0;
		while (msg[i] != SPACE) {
			type += String.format("%c", msg[i]);
			i++;
		}
		
		return type.trim();
	}
	
	/*Utilities for messages*/
	
	protected byte[] concatenate(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String byteArrayToHexString(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String byteArrayToString(byte[] bytes) {
		return new String(bytes,charset).trim();
	}
	
	public static byte[] stringToByteArray(String string) {
		return string.getBytes(charset);
	}
	
}
