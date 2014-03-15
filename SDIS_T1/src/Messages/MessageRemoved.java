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
		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		String m1 = MESSAGE_TYPE + " " + getVersion() + " ";
		String m2 = " " + chunkNo + " ";

		//m1 + fileId + m2 + b
		byte p1[] = concatenate(stringToByteArray(m1), hexStringToByteArray(fileId));
		byte p2[] = concatenate(p1,stringToByteArray(m2));
		p1 = null;

		return concatenate(p2,b);
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
