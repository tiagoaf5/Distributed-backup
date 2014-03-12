package Messages;

public class MessageGetChunk extends Message {

	private static final String MESSAGE_TYPE = "GETCHUNK";
	private String version;
	private int chunkNo;
	
	public MessageGetChunk(String fileId, int chunkNo) {
		this.fileId=fileId;
		this.chunkNo=chunkNo;
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

		return concatenate(message.getBytes(charset), b);
	}

	@Override
	public boolean parseMessage(byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

}
