package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import Messages.Message;
import Messages.MessageChunk;
import Messages.MessageDelete;
import Messages.MessageGetChunk;
import Messages.MessagePutChunk;
import Messages.MessageRemoved;
import Messages.MessageStored;
import Service.BackupService;
import Service.LocalFile;
import Service.RemoteFile;

public class MC extends Thread {
	
	private static final String MESSAGE="Multicast data channel CONTROL: ";
	private Multicast channel;
	private static String filePut;
	private static int chunkPut;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		filePut=null;
		chunkPut=0;
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
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);

				if(type.equals("STORED")) {
					MessageStored msg=new MessageStored();
					msg.parseMessage(rcv);
					
					System.out.println(MESSAGE + " received - STORED FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					
					LocalFile local=BackupService.getLocal(msg.getFileId());
					if(!(local==null)) {
						local.increaseCurReplicationDeg(msg.getChunkNo());
					}
					
					RemoteFile remote = BackupService.getRemote(msg.getFileId());
					if(!(remote==null)) {
						remote.increaseCurReplicationDeg(msg.getChunkNo());
					}	
		
					msg=null;
					
				} else if(type.equals("GETCHUNK")) {
					MessageGetChunk msg=new MessageGetChunk();
					msg.parseMessage(rcv);

					RemoteFile file=BackupService.getRemote(msg.getFileId());
					if(file==null) {
						System.out.println(MESSAGE + " chunk not found");
					} else {
						
						byte[] data=file.getChunkData(msg.getChunkNo());
						//TODO enviar chunk
						System.out.println(MESSAGE + " sending chunk");
					}

					msg=null;
				} else if(type.equals("DELETE")) {
					MessageDelete msg=new MessageDelete();
					msg.parseMessage(rcv);
					
					RemoteFile file=BackupService.getRemote(msg.getFileId());
					if(file==null) {
						System.out.println(MESSAGE + " file not found");
					} else {
						BackupService.deleteRemoteFile(msg.getFileId());
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
