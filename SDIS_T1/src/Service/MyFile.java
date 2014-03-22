package Service;

import java.io.IOException;
import java.util.ArrayList;
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
	
	public int getNumberChunks() {
		return chunks.size();
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
		
	public boolean increaseCurReplicationDeg (int chunkNo, String addr) {		
		if(!chunks.containsKey(chunkNo))
			return false;

		//increases replication degree only if the address addr haven't acknowledged yet
		chunks.get(chunkNo).increaseCurReplicationDeg(addr);
		return true;
	}
	
	public boolean decreaseCurReplicationDeg (int chunkNo, String addr) {		
		if(!chunks.containsKey(chunkNo))
			return false;

		//increases replication degree only if the address addr haven't acknowledged yet
		chunks.get(chunkNo).decreaseCurReplicationDeg(addr);
		return true;
	}
	

	public ArrayList<Integer> getChunksLowReplication() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		
		Iterator<Map.Entry<Integer,Chunk>> it = chunks.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().getCurReplicationDeg() < replicationDeg)
				a.add(it.next().getKey());
		}
		
		return a;
		
	}
}
