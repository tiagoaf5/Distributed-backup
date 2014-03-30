package Messages;

public class MessagePutChunk extends Message {

	private static final String MESSAGE_TYPE = "PUTCHUNK";
	//private static final String MESSAGE_TYPE_ANSWER = "STORED";
	private int replicationDeg;
	byte chunk[];

	public MessagePutChunk(String fileId, int chunkNo, int replicationDeg) {
		super(fileId,chunkNo);
		this.replicationDeg = replicationDeg;

		/****testing purpose**
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String text = "ola";
			md.update(text.getBytes(charset));
			chunk = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		*********************/

	}

	public MessagePutChunk() {

	}

	public void setChunk(byte s[]) {
		chunk = s;
	}
	
	public byte[] getChunk() {
		return chunk;
	}
	
	public int getChunkSize() {
		return chunk.length;
	}
	
	public int getChunkSizeInKB() {
		return chunk.length/1000;
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
		
		String m1 = MESSAGE_TYPE + " " + getVersion() + " " + fileId + 
				" " + chunkNo + " " + replicationDeg + "\r\n\r\n";
		
		return concatenate(stringToByteArray(m1), chunk);
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

		while(data[i] != (byte)0x0D && data[i+1] != (byte)0x0A) {	
			if(x >= 1)
				return -1;
			b[x] = data[i];
			x++;
			i++;
		}
		replicationDeg = Integer.parseInt(byteArrayToString(b));


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

	
	public int getReplicationDeg() {
		return replicationDeg;
	}

	@Override
	public String getType() {
		return MESSAGE_TYPE;
	}
	
}
