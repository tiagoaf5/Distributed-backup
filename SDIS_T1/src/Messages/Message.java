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
	
	protected byte[] concatenate(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
}
