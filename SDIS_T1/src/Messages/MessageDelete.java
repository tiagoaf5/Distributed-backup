package Messages;

public class MessageDelete extends Message {

	private static final String MESSAGE_TYPE = "DELETE";

	public MessageDelete(String fileId) {
		super(fileId);
	}

	public MessageDelete() {}

	@Override
	public byte[] getMessage() {
		String m1 = MESSAGE_TYPE + " " + fileId + "\r\n\r\n";

		return stringToByteArray(m1);
	}

	@Override
	public int parseMessage(byte[] data) {
		int i=0; //data index

		//skip messageType
		while(data[i] != SPACE) {
			i++;
		}

		i++;

		byte[] b = new byte[64];
		int x = 0;
		int k = i + 64;

		if(k > data.length)
			return -1;

		for(; i < k; i++,x++)
			b[x] = data[i];

		fileId = byteArrayToString(b);

		System.out.println(version + "\n" + fileId + "\n");

		return 0;
	}
}
