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
