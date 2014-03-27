package Messages;

public class MessageChunk extends Message {

	private static final String MESSAGE_TYPE = "CHUNK";
	byte chunk[];

	public MessageChunk(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	public MessageChunk() {
	}

	public byte[] getChunk() {
		return chunk;
	}
	
	public void setChunk(byte[] chunk1) {
		chunk=chunk1;
	}
	
	@Override
	public byte[] getMessage() {
		
		String m1 = MESSAGE_TYPE + " " + getVersion() + " " + fileId + 
				" " + chunkNo + "\r\n\r\n";
		
		return concatenate(stringToByteArray(m1), chunk);
	}

	@Override
	public int parseMessage(byte[] data) {
		int i = super.parseMessage(data);

		if(i < 0) {
			System.out.println("Error parsing message of type " + MESSAGE_TYPE);
			return -1;
		}

		//Check header's end
		i = getHeaderTermination(i, data);

		//read chunkdata
		chunk = new byte[data.length - i];
		int x = 0;

		for(;i < data.length; i++,x++)
			chunk[x] = data[i];

		//System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n");
		//System.out.println(byteArrayToHexString(chunk));

		return 0;
	}

}
