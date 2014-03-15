package Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Messages.MessagePutChunk;


public class RemoteFile {
	
	private String fileId;
	private int replicationDeg;
	
	private HashMap<Integer, Chunk> chunks; //<chunkNo,chunk>


	public RemoteFile(String fileId, int replicationDeg) {
		this.fileId = fileId;
		this.replicationDeg = replicationDeg;
		chunks = new HashMap<Integer, Chunk>();
	}

	public boolean addChunk(MessagePutChunk msg) {
		if(chunks.containsKey(msg.getChunkNo()) || fileId != msg.getFileId())
			return false;

		Chunk chunk = new Chunk(msg.getFileId(), msg.getChunkNo(), msg.getReplicationDeg(), msg.getChunk());
		chunks.put(msg.getChunkNo(), chunk);

		return true;
	}

	public byte[] getChunk(int chunkNo) {
		if(!chunks.containsKey(chunkNo))
			return null;

		try {
			return chunks.get(chunkNo).getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean increaseCurReplicationDeg (int chunkNo) {
		if(!chunks.containsKey(chunkNo))
			return false;

		chunks.get(chunkNo).increaseCurReplicationDeg();
		return true;
	}

	public int getReplicationDeg() {
		return replicationDeg;
	}

	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}

	public String getFileId() {
		return fileId;
	}

	public void delete() {
		Iterator<Map.Entry<Integer,Chunk>> it = chunks.entrySet().iterator();
		while (it.hasNext()) {
			it.next().getValue().delete();
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

}
