package Messages;

public class MessageStored extends Message {
	private static final String MESSAGE_TYPE = "STORED";
	private String version;
	private int chunkNo;

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
	public boolean parseMessage(byte[] data) {
		int count = 0; //to count fields
		int i; //data index

		for(i = 0; i < data.length; i++) {
			if (count == 0) { //read operation
				while(data[i] != SPACE) {
					i++;
				}
				count++;
				continue;
			}
			else if (count == 1) { //read version
				byte[] b = new byte[5];
				int x = 0;

				while(data[i] != SPACE) {
					if(x >= 5)
						break;
					b[x] = data[i];
					x++;
					i++;
				}
				version = byteArrayToString(b);
				count++;
				continue;
			}
			else if (count == 2) { // read fileId
				byte[] b = new byte[32];
				int x = 0;
				int k = i + 32;

				for(; i < k; i++,x++)
					b[x] = data[i];

				fileId = byteArrayToHexString(b);
				count++;
				continue;
			}
			else if (count == 3) { //read chunkNo
				byte[] b = new byte[6];
				int x = 0;

				while(data[i] != SPACE) {
					if(x >= 6)
						break;
					b[x] = data[i];
					x++;
					i++;
				}
				chunkNo = Integer.parseInt(byteArrayToString(b));
				count++;
				continue;
			}
			else if (count == 4) { //checks for  "<CRLF> <CRLF> "
				while(!(data[i-3] == CRLF && data[i-2] == SPACE && data[i-1] == CRLF && data[i] == SPACE)) {
					i++;
				}
				count++;
				System.out.println(version + "\n" + fileId + "\n" + chunkNo);
				return true;
			}
		}
		System.out.println("Error parsing");
		return false;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
