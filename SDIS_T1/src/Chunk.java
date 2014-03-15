import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {
	//TODO: save Chunks in a specific folder
	String fileId;
	int chunkNo;

	int replicationDeg;
	int currentReplicationDeg;


	Chunk (String fileId, int chunkNo, int replicationDeg, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;

		this.replicationDeg = replicationDeg;
		this.currentReplicationDeg = 0;


		data = null;
		try {
			storeData(data);
		} catch (IOException e) {
			System.out.println("Problem storing fileId: " + fileId + " Chunkno: " + chunkNo);
			e.printStackTrace();
		}
	}

	private void storeData(byte[] data) throws IOException {
		File f = new File(getNameOnDisk());
		if(!f.exists()) {
			f.createNewFile();
		} 

		FileOutputStream o = new FileOutputStream(f); 
		o.write(data,0,32);
		o.close();
		data = null;
		currentReplicationDeg++;
	}

	private String getNameOnDisk() {
		return new String(fileId + ".part" + chunkNo);
	}

	public byte[] getData() throws IOException {
		File f = new File(getNameOnDisk());

		if (!f.exists())
			return null;

		FileInputStream fileStream = new FileInputStream(f);



		long length = f.length();
		byte[] b = new byte[(int)length];


		fileStream.read(b);
		fileStream.close();

		return b;
	}
}
