package Messages;

import java.nio.charset.Charset;

public abstract class Message {

	static final public Charset charset = Charset.forName("US-ASCII");
	static final protected byte CRLF = (byte) 0xDA;
	static final protected byte SPACE = 0x20;

	protected String version;
	protected String fileId;
	protected int chunkNo;
	
	public Message() {
		version = "1.0";
	}
	
	public Message(String fileId) {
		version = "1.0";
		this.fileId = fileId;
	}
	
	public Message(String fileId, int chunkNo) {
		version = "1.0";
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	abstract public byte [] getMessage();

	public int getChunkNo() {
		return chunkNo;
	}

	public String getFileId() {
		return fileId;
	}

	public int parseMessage(byte[] data) {

		int count = 1; //to count fields
		int i=0; //data index

		//skip messageType
		while(data[i] != SPACE) {
			i++;
		}

		i++;
		for(; i < data.length; i++) {
			if (count == 1) { //read version
				byte[] b = new byte[5];
				int x = 0;

				while(data[i] != SPACE) {
					if(x >= 5)
						break;
					b[x] = data[i];
					x++;
					i++;
				}
				version = byteArrayToString(b);
				count++;
				//System.out.println("Version: " + byteArrayToHexString(b) + " - " + version);
				continue;
			}
			else if (count == 2) { // read fileId
				byte[] b = new byte[32];
				int x = 0;
				int k = i + 32;

				for(; i < k; i++,x++)
					b[x] = data[i];

				fileId = byteArrayToHexString(b);
				//System.out.println("FileId: " + fileId);
				count++;
				continue;
			}
			else if (count == 3) { //read chunkNo
				byte[] b = new byte[6];
				int x = 0;

				while(data[i] != SPACE) {
					if(x >= 6)
						break;
					b[x] = data[i];
					x++;
					i++;
				}
				//System.out.println("chunkNo: " + byteArrayToHexString(b));
				
				chunkNo = Integer.parseInt(byteArrayToString(b));
				count++;
				i++;
				return i;

			}
		}

		return -1;
	}




	protected int getHeaderTermination(int i, byte[] data) {
		while(!(data[i-3] == CRLF && data[i-2] == SPACE && data[i-1] == CRLF && data[i] == SPACE)) {
			i++;
			if(i >= data.length)
				return -1;
		}
		i++;
		return i;
	}
	//returns type of a message
	static public String getMessageType(byte[] msg) {
		String type = new String();

		int i = 0;
		while (msg[i] != SPACE) {
			type += String.format("%c", msg[i]);
			i++;
		}

		return type.trim();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
