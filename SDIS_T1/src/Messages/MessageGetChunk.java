package Messages;

public class MessageGetChunk extends Message {
	private static final String MESSAGE_TYPE = "GETCHUNK";

	public MessageGetChunk(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	public MessageGetChunk() { }

	public MessageChunk getAnswer(byte[] data) {

		MessageChunk res=new MessageChunk(fileId,chunkNo);
		res.setChunk(data);
		return res;
	}

	@Override
	public byte[] getMessage() {
		
		String m1 = MESSAGE_TYPE + " " + getVersion() + " " + fileId + 
				" " + chunkNo + "\r\n\r\n";
		
		return stringToByteArray(m1);
	}

	@Override
	public int parseMessage(byte[] data) {
		int i = super.parseMessage(data);

		//Check header's end
		i = getHeaderTermination(i, data);

		if (i < 0)
			return -1;

		//System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n");
		return 0;
	}

	@Override
	public String getType() {
		return MESSAGE_TYPE;
	}
}
