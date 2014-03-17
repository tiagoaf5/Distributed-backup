package Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {
	//TODO: save Chunks in a specific folder
	private final String PATH = "RemoteFiles/";
	String fileId;
	int chunkNo;

	int replicationDeg;
	int currentReplicationDeg;

	public Chunk (String fileId, int chunkNo, int replicationDeg, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;

		this.replicationDeg = replicationDeg;
		this.currentReplicationDeg = 0;

		try {
			storeData(data);
			data = null;
		} catch (IOException e) {
			System.out.println("Problem storing fileId: " + fileId + " Chunkno: " + chunkNo);
			e.printStackTrace();
		}
		data = null;
	}
	
	public Chunk (String fileId, int chunkNo, int replicationDeg) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;

		this.replicationDeg = replicationDeg;
		this.currentReplicationDeg = 0;
	}

	private void storeData(byte[] data) throws IOException {
		File f = new File(getNameOnDisk());
		if(!f.exists()) {
			f.createNewFile();
		} 

		FileOutputStream o = new FileOutputStream(f); 
		o.write(data);
		o.close();
		data = null;
		currentReplicationDeg++;
	}

	private String getNameOnDisk() {
		return new String(PATH + fileId + ".part" + chunkNo);
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
	
	public synchronized void increaseCurReplicationDeg() {
		currentReplicationDeg++;
	}
	public synchronized int getCurReplicationDeg() {
		return currentReplicationDeg;
	}
	
	public boolean delete() {
		File f = new File(getNameOnDisk());

		if (!f.exists())
			return true;
		
		return f.delete();
	}
}
