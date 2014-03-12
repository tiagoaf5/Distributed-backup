package Messages;

public class MessageStored extends Message {
	private static final String MESSAGE_TYPE = "STORED";

	public MessageStored(String fileId, int chunkNo) {
		this.setVersion("1.0");
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	public MessageStored() {

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
