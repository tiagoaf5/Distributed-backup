package Messages;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessagePutChunk extends Message {

	private static final String MESSAGE_TYPE = "PUTCHUNK";
	private int replicationDeg;
	byte chunk[];

	public MessagePutChunk(String fileId, int chunkNo, int replicationDeg) {
		super(fileId,chunkNo);
		this.replicationDeg = replicationDeg;

		/****testing purpose***/
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String text = "ola";
			md.update(text.getBytes(charset));
			chunk = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		/*********************/

	}

	public MessagePutChunk() {

	}

	void setChunk(byte s[]) {
		chunk = s;
	}

	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + getVersion() + " " + 
				fileId + " " + 
				chunkNo + " " + 
				replicationDeg + " ";

		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		return concatenate(concatenate(message.getBytes(charset), b), chunk);

	}

	@Override
	public int parseMessage(byte[] data) {
		int i = super.parseMessage(data);
	
		if(i < 0) {
			System.out.println("Error parsing message of type " + MESSAGE_TYPE);
			return -1;
		}
		
		//read replicationDeg
		byte[] b = new byte[2];
		int x = 0;

		while(data[i] != SPACE) {
			if(x >= 1)
				return -1;
			b[x] = data[i];
			x++;
			i++;
		}
		replicationDeg = Integer.parseInt(byteArrayToString(b));
		i++;
		
		//Check header's end
		i = getHeaderTermination(i, data);
		
		//read chunkdata
		chunk = new byte[data.length - i];
		x = 0;

		for(;i < data.length; i++,x++)
			chunk[x] = data[i];

		System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n" + replicationDeg);
		System.out.println(byteArrayToHexString(chunk));
		
		return 0;
	}



	public static void main(String[] args) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String text = "nabo";
		md.update(text.getBytes(charset));
		byte[] digest = md.digest();


		MessagePutChunk a = new MessagePutChunk(new String(digest),2,5);
		byte[] message = a.getMessage();

		/*for(int i = 0; i < message.length; i++)
			System.out.print(String.format("%x", message[i] & 0xFF));*/

		System.out.println(byteArrayToHexString(message) + "\n");

		MessagePutChunk b = new MessagePutChunk();
		b.parseMessage(message);
		
		
		System.out.println("*************************************");
		
		MessageRemoved c = new MessageRemoved(byteArrayToString(digest), 2);
		byte[] message1 = c.getMessage();
		
		MessageRemoved d = new MessageRemoved();
		d.parseMessage(message1);

	}

	
}
