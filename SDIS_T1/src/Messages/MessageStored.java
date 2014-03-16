package Messages;

public class MessageStored extends Message {
	private static final String MESSAGE_TYPE = "STORED";

	public MessageStored(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	public MessageStored() {

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

		//System.out.println(version + "\n" + fileId + "\n" + chunkNo + "\n");
		return 0;
	}
}
