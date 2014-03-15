package Messages;

public class MessageDelete extends Message {

	private static final String MESSAGE_TYPE = "DELETE";

	public MessageDelete(String fileId) {
		super(fileId);
	}

	public MessageDelete() {}

	@Override
	public byte[] getMessage() {
		byte b[] = {SPACE,CRLF,SPACE,CRLF,SPACE};

		String m1 = MESSAGE_TYPE + " ";
		
		//m1 + fileId + b
		byte p1[] = concatenate(stringToByteArray(m1), hexStringToByteArray(fileId));

		return concatenate(p1,b);
	}

	@Override
	public int parseMessage(byte[] data) {
		int i=0; //data index

		//skip messageType
		while(data[i] != SPACE) {
			i++;
		}

		i++;

		byte[] b = new byte[32];
		int x = 0;
		int k = i + 32;

		if(k > data.length)
			return -1;

		for(; i < k; i++,x++)
			b[x] = data[i];

		fileId = byteArrayToHexString(b);

		System.out.println(version + "\n" + fileId + "\n");

		return 0;
	}
}
