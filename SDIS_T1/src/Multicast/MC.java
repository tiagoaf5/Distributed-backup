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
import Service.RemoteFile;

public class MC implements Runnable {
	
	private static final String MESSAGE="Multicast data channel CONTROL:";
	private Multicast channel;
	private static String filePut;
	private static int chunkPut;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		filePut=null;
		chunkPut=0;
	}

	@Override
	public void run() {
		while(true) {
			
			System.out.println("Running multicast data channel for CONTROL...");
			
			//Can receive:
			// - STORED
			// - GETCHUNK
			// - DELETE
			// - REMOVED
			try {
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);
				System.out.println(MESSAGE + " received - " + Message.byteArrayToHexString(rcv));

				if(type.equals("STORED")) {
					MessageStored msg=new MessageStored();
					msg.parseMessage(rcv);
					
					if(askedPut(msg.getFileId(), msg.getChunkNo())) {
						//TODO o que fazer com isto??
					}
					
					msg=null;
				} else if(type.equals("GETCHUNK")) {
					MessageGetChunk msg=new MessageGetChunk();
					msg.parseMessage(rcv);

					RemoteFile file=remote(msg.getFileId());
					if(file==null) {
						System.out.println(MESSAGE + " chunk not found");
					} else {
						
						byte[] data=file.getChunk(msg.getChunkNo());
						//TODO enviar chunk
						System.out.println(MESSAGE + " sending chunk");
					}

					msg=null;
				} else if(type.equals("DELETE")) {
					MessageDelete msg=new MessageDelete();
					msg.parseMessage(rcv);
					
					RemoteFile file=remote(msg.getFileId());
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
	
	private RemoteFile remote(String fileId) {
	
		HashMap<String, RemoteFile> remoteFiles=BackupService.getRemoteFiles();
		return remoteFiles.get(fileId);
	}
	
	private boolean askedPut(String fileId, int chunkNo) {
		return (filePut.equals(fileId)&&chunkNo==chunkPut);
	}

	public void sendMessage(Message x) throws IOException {
		channel.send(x.getMessage());
	}
}
