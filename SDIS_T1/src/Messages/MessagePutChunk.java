package Messages;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessagePutChunk extends Message {

	private static final String MESSAGE_TYPE = "PUTCHUNK";
	private String version;
	private int chunkNo;
	private int replicationDeg;
	byte chunk[];

	public MessagePutChunk(String fileId, int chunkNo, int replicationDeg) {
		this.setVersion("1.0");
		this.fileId = fileId;
		this.chunkNo = chunkNo;
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
	public boolean parseMessage(byte[] data) {
		int count = 0; //to count fields
		int i; //data index

		for(i = 0; i < data.length; i++) {
			if (count == 0) { //read operation
				while(data[i] != SPACE) {
					i++;
				}
				count++;
				continue;
			}
			else if (count == 1) { //read version
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
				continue;
			}
			else if (count == 2) { // read fileId
				byte[] b = new byte[32];
				int x = 0;
				int k = i + 32;

				for(; i < k; i++,x++)
					b[x] = data[i];

				fileId = byteArrayToHexString(b);
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
				chunkNo = Integer.parseInt(byteArrayToString(b));
				count++;
				continue;
			}
			else if (count == 4) { //read replicationDeg
				byte[] b = new byte[2];
				int x = 0;

				while(data[i] != SPACE) {
					if(x >= 1)
						break;
					b[x] = data[i];
					x++;
					i++;
				}
				replicationDeg = Integer.parseInt(byteArrayToString(b));
				count++;
				continue;
			}
			else if (count == 5) { //checks for  "<CRLF> <CRLF> "
				while(!(data[i-3] == CRLF && data[i-2] == SPACE && data[i-1] == CRLF && data[i] == SPACE)) {
					i++;
				}
				count++;
				continue;
			}
			else if (count == 6) //read data
			{
				chunk = new byte[data.length - i];
				int x = 0;

				for(;i < data.length; i++,x++)
					chunk[x] = data[i];

				System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n" + replicationDeg);
				System.out.println(byteArrayToHexString(chunk));
				return true;
			}
		}
		System.out.println("Error parsing");
		return false;
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
		
		MessageStored c = new MessageStored(byteArrayToString(digest), 2);
		byte[] message1 = c.getMessage();
		
		MessageStored d = new MessageStored();
		d.parseMessage(message1);


	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
