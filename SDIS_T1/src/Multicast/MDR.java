package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Service.BackupService;
import Service.Chunk;
import Service.LocalFile;

public class MDR extends Thread {

	private static final String MESSAGE="MDR ";
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
					
					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) {
						System.out.println(MESSAGE + "Wrong format! Ignoring..");
						continue;
					}
					System.out.println(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					final LocalFile file = BackupService.getLocal(msg.getFileId());
					String fileId = msg.getFileId();
					int chunkNo = msg.getChunkNo();

					if(file != null) {

						//it's a file I asked to restore -> save it in tmp folder
						Chunk c = file.getChunk(chunkNo);
						c.setPath("tmp/");
						c.setRestored(true);
						
						System.out.println(MESSAGE + c.getRestored());
						int length = c.storeData(msg.getChunk());
						System.out.println(MESSAGE + " I received my Chunk :) " + length );

						
						if(length<64000) //last Chunk
						{

							if(file.hasReceivedAll()) {
								new Thread (new Runnable() {

									@Override
									public void run() {
										file.selfRestore();
										file.unCheckReceivedAll();
									}
								}).start();
							}
						}
					} 
					else if(BackupService.isRemote(fileId, chunkNo)) {
						//Checks that chunk to let MC know that there was a peer o answered faster
						BackupService.getRemote(fileId).getChunk(chunkNo).setCheck(true);
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

