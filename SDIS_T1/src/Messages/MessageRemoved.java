package Messages;

public class MessageRemoved extends Message {
	private static final String MESSAGE_TYPE = "REMOVED";

	public MessageRemoved() {
		super();
	}

	public MessageRemoved(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + version + " " + 
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
