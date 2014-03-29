package Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Chunk implements Comparable<Chunk> {
	private String path = "RemoteFiles/";
	String fileId;
	int chunkNo;
	int size;
	int replicationDeg;

	/*Used as a flag to:
	 * - If a peer received a GETCHUNK message MDR will change check flag to let  a peer know 
	 *   that another peer already answered with a CHUNK message for this Chunk
	 * 
	 */
	boolean check = false;
	
	/*Used as a flag to:
	 * - If a peer received a CHUNK message MDR will change check flag to let  a peer know 
	 *   that another peer already answered with a CHUNK message for this Chunk
	 * 
	 */
	boolean restored=false;

	protected ArrayList<String> addresses; //Addresses that acknowledged


	public Chunk (String fileId, int chunkNo, int replicationDeg, byte[] data) { //called by remote file
		this.fileId = fileId;
		this.chunkNo = chunkNo;

		this.replicationDeg = replicationDeg;
		//this.currentReplicationDeg = 0;

		addresses = new ArrayList<String>();

		try {
			storeData(data);
			size = data.length;
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
		//this.currentReplicationDeg = 0;
		addresses = new ArrayList<String>();
		
		size = 0;
	}

	public int storeData(byte[] data) throws IOException {
		File f = new File(getNameOnDisk());
		if(!f.exists()) {
			f.createNewFile();
		} 

		FileOutputStream o = new FileOutputStream(f); 
		o.write(data);
		o.close();
		
		int length = data.length;
		data = null;
		
		return length;
		//currentReplicationDeg++;
	}

	private String getNameOnDisk() {
		return new String(path + fileId + ".part" + chunkNo);
	}

	public byte[] getData() throws IOException {
		File f = new File(getNameOnDisk());

		if (!f.exists())
			return null;

		FileInputStream fileStream = new FileInputStream(f);

		byte[] b = new byte[(int)f.length()];

		fileStream.read(b);
		fileStream.close();

		return b;
	}
	
	public byte[] recoverData() throws IOException {
		File f = new File(getNameOnDisk());

		if (!f.exists())
			return null;

		FileInputStream fileStream = new FileInputStream(f);

		byte[] b = new byte[(int)f.length()];

		fileStream.read(b);
		fileStream.close();

		return b;
	}

	public synchronized boolean increaseCurReplicationDeg(String ip) {
		if(addresses.contains(ip))
			return false;
		else
			addresses.add(ip);
		return true;
		//currentReplicationDeg++;
	}
	
	public boolean decreaseCurReplicationDeg(String ip) {
		if(addresses.contains(ip)) {
			addresses.remove(ip);
			return true;
		}
		else
			
		return false;
		
	}
	
	public synchronized int getCurReplicationDeg() {
		return addresses.size(); 
	}

	public boolean delete() {
		File f = new File(getNameOnDisk());

		BackupService.decrementDiskUsage(size);
		
		if (!f.exists())
			return true;

		return f.delete();
	}

	public synchronized boolean isChecked() {
		boolean current = check;
		this.check = false; //after I checked I want it to be false again
		
		return current;
	}

	public synchronized void setCheck(boolean check) {
		this.check = check;
	}

	public synchronized boolean getRestored() {
		return restored;
	}

	public synchronized void setRestored(boolean restored) {
		this.restored = restored;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileId() {
		return fileId;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public int getReplicationDeg() {
		return replicationDeg;
	}
	
	public int compareTo(Chunk x2) {
		Float ratio1 = new Float((getCurReplicationDeg() - 1) / getReplicationDeg());
		Float ratio2 = new Float((x2.getCurReplicationDeg() - 1) / x2.getReplicationDeg());
			
		return ratio1.compareTo(ratio2) * -1;
	}

	@Override
	public boolean equals(Object x) {
		Chunk x2 = (Chunk) x;
		
		Float ratio1 = new Float((getCurReplicationDeg() - 1) / getReplicationDeg());
		Float ratio2 = new Float((x2.getCurReplicationDeg() - 1) / x2.getReplicationDeg());
			
		return ratio1 == ratio2;
	}

	public int getSize() {
		return size;
	}

	
	
}
