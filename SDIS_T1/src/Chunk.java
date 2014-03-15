import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {
	String fileId;
	int chunkNo;
	
	int replicationDeg;
	int currentReplicationDeg;
	
	byte[] data;
	
	Chunk (String fileId, int chunkNo, int replicationDeg, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		
		this.replicationDeg = replicationDeg;
		this.currentReplicationDeg = 0;
		
		this.data = data;
		
		data = null;
		try {
			storeData();
		} catch (IOException e) {
			System.out.println("Problem storing fileId: " + fileId + " Chunkno: " + chunkNo);
			e.printStackTrace();
		}
	}
	
	private void storeData() throws IOException {
		File f = new File(getNameOnDisk());
		if(!f.exists()) {
		    f.createNewFile();
		} 
		
		FileOutputStream o = new FileOutputStream(f, false); 
		o.write(data);
		o.close();
		data = null;
		currentReplicationDeg++;
	}
	
	private String getNameOnDisk() {
		return new String(fileId + ".part" + chunkNo);
	}
	

}
