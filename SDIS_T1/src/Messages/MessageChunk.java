package Messages;

public class MessageChunk extends Message {

	private static final String MESSAGE_TYPE = "CHUNK";
	private String version;
	private int chunkNo;
	byte chunk[];
	
	public MessageChunk(String fileId, int chunkNo) {
		this.setVersion("1.0");
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + getVersion() + " " + 
				fileId + " " + 
				chunkNo + " ";

		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		return concatenate(concatenate(message.getBytes(charset), b), chunk);
	}

	@Override
	public boolean parseMessage(byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

}
