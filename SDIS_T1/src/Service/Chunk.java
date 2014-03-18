package Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Chunk {
	//TODO: save Chunks in a specific folder
	private final String PATH = "RemoteFiles/";
	String fileId;
	int chunkNo;

	int replicationDeg;

	/*Used as a flag to:
	 * - If a peer received a GETCHUNK message MDR will change check flag to let  a peer know 
	 *   that another peer already answered with a CHUNK message for this Chunk
	 * 
	 */
	boolean check = false; 

	protected ArrayList<String> addresses; //Addresses that acknowledged


	public Chunk (String fileId, int chunkNo, int replicationDeg, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;

		this.replicationDeg = replicationDeg;
		//this.currentReplicationDeg = 0;

		addresses = new ArrayList<String>();

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
		//this.currentReplicationDeg = 0;
		addresses = new ArrayList<String>();
	}

	private void storeData(byte[] data) throws IOException { //TODO: Maybe we need to change to public
		File f = new File(getNameOnDisk());
		if(!f.exists()) {
			f.createNewFile();
		} 

		FileOutputStream o = new FileOutputStream(f); 
		o.write(data);
		o.close();
		data = null;
		//currentReplicationDeg++;
	}

	private String getNameOnDisk() {
		return new String(PATH + fileId + ".part" + chunkNo);
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

	public synchronized boolean increaseCurReplicationDeg(String ip) {
		if(addresses.contains(ip))
			return false;
		else
			addresses.add(ip);
		return true;
		//currentReplicationDeg++;
	}
	public synchronized int getCurReplicationDeg() {
		return addresses.size()+1;
	}

	public boolean delete() {
		File f = new File(getNameOnDisk());

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
}
