package Service;

import Messages.MessagePutChunk;


public class RemoteFile extends MyFile{
	
	public RemoteFile(String fileId, int replicationDeg) {
		super(fileId, replicationDeg);
	}

	public boolean addChunk(MessagePutChunk msg) {
		//System.out.println("---->" + msg.getChunkNo());
		
		if(chunks.containsKey(msg.getChunkNo()) || !fileId.equals(msg.getFileId()))
			return false;

		Chunk chunk = new Chunk(msg.getFileId(), msg.getChunkNo(), msg.getReplicationDeg(), msg.getChunk());
		
		chunks.put(msg.getChunkNo(), chunk);
		return true;
	}

}
