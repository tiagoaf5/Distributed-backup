package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import Messages.Message;
import Messages.MessagePutChunk;
import Service.BackupService;
import Service.LocalFile;
import Service.RemoteFile;


public class MDB extends Thread {

	Multicast channel;
	
	public MDB(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	public void run() {
		
		System.out.println("...");
		
		while(true) {
			System.out.println(".");
			//Can receive:
			// - PUTCHUNK
			
			try { 
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);
				System.out.println("Received: " + Message.byteArrayToString(rcv));
				
				if(type.equals("PUTCHUNK")) {
					
					MessagePutChunk msg=new MessagePutChunk();
					msg.parseMessage(rcv);
					
					if(!local(msg.getFileId())) {
						RemoteFile file=remote(msg.getFileId());
						if(file==null) {
							file=new RemoteFile(msg.getFileId(), msg.getReplicationDeg());
							file.addChunk(msg);
							BackupService.addRemoteFile(msg.getFileId(), file);
							System.out.println("ola");
						} else {
							if(!file.addChunk(msg))
								System.out.println("Chunk already stored");
						}
					} else {
						System.out.println("Local file");
						msg=null;
					}
				} else {
					System.out.println("Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private boolean local(String fileId) {
		
		List<LocalFile> localFiles=BackupService.getLocalFiles();
		
		int i=0;
		
		while(i<localFiles.size()) {
			LocalFile temp=localFiles.get(i);
			if(temp.getId().equals(fileId))
				return true;
			i++;
		}
		return false;
	}

	private RemoteFile remote(String fileId) {
		
		HashMap<String, RemoteFile> remoteFiles=BackupService.getRemoteFiles();
		return remoteFiles.get(fileId);
	}
	
	public void sendMessage(MessagePutChunk msg) {
		
		System.out.println("Sending Message");
		try {
			channel.send(msg.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
