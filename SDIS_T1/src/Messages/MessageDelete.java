package Messages;

public class MessageDelete extends Message {

	private static final String MESSAGE_TYPE = "DELETE";

	public MessageDelete(String fileId) {
		this.fileId=fileId;
	}
	
	@Override
	public byte[] getMessage() {
		String message = MESSAGE_TYPE + " " + 
				fileId + " ";

		byte b[] = {CRLF,SPACE,CRLF,SPACE};

		return concatenate(message.getBytes(charset), b);
	}

	@Override
	public boolean parseMessage(byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

}
