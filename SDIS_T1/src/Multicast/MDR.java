package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Service.BackupService;
import Service.LocalFile;

public class MDR extends Thread {

	private static final String MESSAGE="Multicast data channel RESTORE: ";
	private Multicast channel;
	
	public MDR(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	@Override
	public void run() {

		System.out.println("Running multicast data channel for RESTORE...");

		while(true) {
			//Can receive:
			// - CHUNK
			try { 
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);
				

				if(type.equals("CHUNK")) {

					MessageChunk msg=new MessageChunk();
					msg.parseMessage(rcv);
					
					System.out.println(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					
					LocalFile file = BackupService.getLocal(msg.getFileId());
					
					if(file != null) {
						//byte[] chunk = msg.getChunk();
						//TODO: save it in tmp folder
						System.out.println(MESSAGE + " I received my Chunk :)");
						
					} 
					else if(BackupService.isRemote(msg.getFileId(), msg.getChunkNo())) {
						//Checks that chunk to let MC know that there was a peer o answered faster
						BackupService.getRemote(msg.getFileId()).getChunk(msg.getChunkNo()).setCheck(true);
					} 
					else {
						System.out.println(MESSAGE + " chunk not asked and I'm not tracking that chunk");
					}
					
					msg=null;
				} 
				else {
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

