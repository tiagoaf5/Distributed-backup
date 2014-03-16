package Messages;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessagePutChunk extends Message {

	private static final String MESSAGE_TYPE = "PUTCHUNK";
	//private static final String MESSAGE_TYPE_ANSWER = "STORED";
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

	public void setChunk(byte s[]) {
		chunk = s;
	}
	
	public byte[] getChunk() {
		return chunk;
	}
	
	public byte[] getAnswer() {
		MessageStored res = getAnswerMessage(); 
		return res.getMessage();
	}
	
	public MessageStored getAnswerMessage() {
		return new MessageStored(fileId,chunkNo);
	}

	@Override
	public byte[] getMessage() {
		byte b[] = {CRLF,SPACE,CRLF,SPACE};
		
		String m1 = MESSAGE_TYPE + " " + getVersion() + " ";
		String m2 = " " + chunkNo + " " + replicationDeg + " ";
		
		//m1 + fileId + m2 + b + chunk
		byte p1[] = concatenate(stringToByteArray(m1), hexStringToByteArray(fileId));
		byte p2[] = concatenate(p1,stringToByteArray(m2));
		p1 = null;

		return concatenate(concatenate(p2,b), chunk);
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

		//read chunk data
		chunk = new byte[data.length - i];
		x = 0;

		for(;i < data.length; i++,x++)
			chunk[x] = data[i];

		//System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n" + replicationDeg);
		//System.out.println(byteArrayToHexString(chunk));

		return 0;
	}



	public static void main(String[] args) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String text = "nabo";
		md.update(text.getBytes(charset));
		byte[] digest = md.digest();


		/*MessagePutChunk a = new MessagePutChunk(new String(digest),2,5);
		byte[] message = a.getMessage();*/
		
		MessagePutChunk a = new MessagePutChunk("41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d", 5, 2);
		a.setChunk(Message.hexStringToByteArray("41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d"));

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
	
	public int getReplicationDeg() {
		return replicationDeg;
	}


}
