package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import Messages.*;
import Service.*;

public class MC extends Thread {
	
	private static final String MESSAGE="Multicast data channel CONTROL: ";
	private Multicast channel;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	public void run() {
		
		System.out.println("Running multicast data channel for CONTROL...");
		
		while(true) {
			//Can receive:
			// - STORED
			// - GETCHUNK
			// - DELETE
			// - REMOVED
			try {
				
				Packet pkt = channel.receive1();
				byte[] rcv = pkt.getData();
				
				String type = Message.getMessageType(rcv);

				if(type.equals("STORED")) {
					MessageStored msg=new MessageStored();
					msg.parseMessage(rcv);
					
					System.out.println(MESSAGE + " received - STORED FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					
					LocalFile local = BackupService.getLocal(msg.getFileId());
					if(!(local == null)) {
						local.increaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());
					}
					
					RemoteFile remote = BackupService.getRemote(msg.getFileId());
					if(!(remote == null)) { 
						//if file doesn't have chunkNo chunk doesn't do anything
						remote.increaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());
					}	
		
					msg=null;
					
				} else if(type.equals("GETCHUNK")) {
					MessageGetChunk msg = new MessageGetChunk();
					msg.parseMessage(rcv);
					//TODO: ana andou a mexer aqui na aula, cuidado!!!!
					if(BackupService.isRemote(msg.getFileId(), msg.getChunkNo())) {
						
						RemoteFile file=BackupService.getRemote(msg.getFileId());
						byte[] data=file.getChunkData(msg.getChunkNo());
						
						//TODO: esperar e mandar pelo MDR MessageChunk
						MessageChunk answer = msg.getAnswer(data);
						
						System.out.println(MESSAGE + " sending chunk");
						
					} else {
						System.out.println(MESSAGE + " chunk not found");
					}

					msg=null;
				} else if(type.equals("DELETE")) {
					MessageDelete msg=new MessageDelete();
					msg.parseMessage(rcv);
					
					RemoteFile file=BackupService.getRemote(msg.getFileId());
					//TODO: questao de ver chunkNo
					if(file==null) {
						System.out.println(MESSAGE + " file not found");
					} else {
						BackupService.deleteRemoteFile(msg.getFileId()); //TODO:
						System.out.println(MESSAGE + " deleting file");
					}
					
					msg=null;
				} else if(type.equals("REMOVED")) {
					MessageRemoved msg=new MessageRemoved();
					msg.parseMessage(rcv);
					
					//TODO:
					msg=null;
				} else {
					System.out.println(MESSAGE + " - Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void sendMessage(Message x) throws IOException {
		System.out.println(MESSAGE + "Sending Message..");
		channel.send(x.getMessage());
	}
}
