package Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class MyFile {
	
	protected final int CHUNK_SIZE = 64000;
	
	protected String fileId;
	protected int replicationDeg;
	
	protected HashMap<Integer, Chunk> chunks; //<chunkNo,chunk>
	
	public MyFile(String fileId, int replicationDeg) {
		this.fileId = fileId;
		this.replicationDeg = replicationDeg;
		chunks = new HashMap<Integer, Chunk>();
	}
	
	public MyFile() {
		chunks = new HashMap<Integer, Chunk>();
	}
	
	
	public String getId() {
		return fileId;
	}

	public void setReplication(int replication) {
		
		if(replication>9)
			this.replicationDeg=9;
		else
			this.replicationDeg = replication;
	}
	

	public int getReplicationDeg() {
		return replicationDeg;
	}

	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}
	
	
	public byte[] getChunkData(int chunkNo) {
		if(!chunks.containsKey(chunkNo))
			return null;

		try {
			return chunks.get(chunkNo).getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Chunk getChunk(int chunkNo) {
		if(!chunks.containsKey(chunkNo))
			return null;
		
		return chunks.get(chunkNo);
	}

	public void delete() {
		Iterator<Map.Entry<Integer,Chunk>> it = chunks.entrySet().iterator();
		while (it.hasNext()) {
			it.next().getValue().delete();
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void removeChunk(int chunkNo) {
		chunks.get(chunkNo).delete();
		chunks.remove(chunkNo);
	}
	
	public boolean increaseCurReplicationDeg (int chunkNo) {
		if(!chunks.containsKey(chunkNo))
			return false;

		chunks.get(chunkNo).increaseCurReplicationDeg();
		return true;
	}

}