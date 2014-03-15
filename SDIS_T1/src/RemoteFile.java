import java.util.HashMap;

import Messages.MessagePutChunk;


public class RemoteFile {
	private String fileId;
	private HashMap<Integer, Chunk> chunks; //<chunkNo,chunk>
	
	
	public RemoteFile(String fileId) {
		this.fileId = fileId;
	}
	
	public boolean addChunk(MessagePutChunk msg) {
		if(chunks.containsKey(msg.getChunkNo()))
			return false;
		
		Chunk chunk = new Chunk(msg.getFileId(), msg.getChunkNo(), msg.getReplicationDeg(), msg.getChunk());
		chunks.put(msg.getChunkNo(), chunk);
		
		return true;
	}
	

}
