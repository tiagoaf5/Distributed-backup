package Messages;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class MessagePutChunk extends Message {

	private static final String MESSAGE_TYPE = "PUTCHUNK";
	private static String version;
	private String chunkNo;
	private String replicationDeg;
	byte chunk[];

	public MessagePutChunk(String fileId, int chunkNo, int replicationDeg) {
		this.version = "1.0";
		this.fileId = fileId;
		this.chunkNo = Integer.toString(chunkNo);
		this.replicationDeg = Integer.toString(replicationDeg);

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String text = "ola";
			md.update(text.getBytes(charset));
			chunk = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}


	}

	public MessagePutChunk() {

	}

	void setChunk(byte s[]) {
		chunk = s;
	}

	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + version + " " + 
				fileId + " " + 
				chunkNo + " " + 
				replicationDeg + " ";

		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		return concatenate(concatenate(message.getBytes(charset), b), chunk);

	}

	@Override
	public boolean parseMessage(byte[] data) {
		int count = 0;
		ArrayList<String> list = new ArrayList<String>(); // save data read

		byte[] b = new byte[35]; //temp buffer
		int x = 0; //buffer index
		int i; 

		
		StringBuilder s = new StringBuilder();
		
		
		
		
		//get messageType and Version
		for(i = 0; count < 2; i++,x++) {
			
			if(i > data.length) //if can't find enough spaces in the message the protocol isn't respected
				return false;
			
			if (count == 2) { // is about to read fileId
				int k = i + 32;
				
				for(; i < k; i++)
					s.append(String.format("%h", data[i]));
				
				x = -1;
				b = new byte[35];
				count++;
				continue;
			}

			if(data[i] == SPACE) { //if receives a space can export a field now
				list.add(new String(b).trim());
				x = -1;
				b = new byte[35];
				count++;
				continue;
			}

			b[x] = data[i];
			System.out.print(String.format("%c", data[i]));
		}
		System.out.println("\n");
		
		
		for(i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
		
		

		System.out.println("\n\nbenfica\n\n" + s.toString());
		return true;
	}



	public static void main(String[] args) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String text = "nabo";
		md.update(text.getBytes(charset));
		byte[] digest = md.digest();


		MessagePutChunk a = new MessagePutChunk(new String(digest),2,5);
		byte[] message = a.getMessage();

		for(int i = 0; i < message.length; i++)
			System.out.print(String.format("%x", message[i] & 0xFF));
		System.out.println("\n");
		



		/*System.out.println("\n");
		System.out.println(digest.length);
		
		System.out.println("NABO\n");
		System.out.println("" + String.format("%c", message[0]));
		
		a.parseMessage(message);
		
		System.out.println("NABO\n");
		System.out.println(getMessageType(message));*/
	}
}
