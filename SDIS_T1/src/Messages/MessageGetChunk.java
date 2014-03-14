package Messages;

public class MessageGetChunk extends Message {
	private static final String MESSAGE_TYPE = "GETCHUNK";

	public MessageGetChunk(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	//TODO: set MessageChunk com o chunk necessario
	public byte[] answer() {

		MessageChunk res=new MessageChunk(fileId,chunkNo);
		return res.getMessage();
	}
	
	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + getVersion() + " " + 
				fileId + " " + 
				chunkNo + " ";

		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		return concatenate(message.getBytes(charset), b);
	}

	@Override
	public int parseMessage(byte[] data) {
		int i = super.parseMessage(data);

		//Check header's end
		i = getHeaderTermination(i, data);

		if (i < 0)
			return -1;

		System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n");
		return 0;
	}

}
