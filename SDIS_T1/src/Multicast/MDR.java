package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Messages.MessagePutChunk;
import Service.BackupService;
import Service.Chunk;
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
					
					final LocalFile file = BackupService.getLocal(msg.getFileId());
					String fileId = msg.getFileId();
					int chunkNo = msg.getChunkNo();
					
					if(file != null) {
						
						//it's a file I asked to restore -> save it in tmp folder
						Chunk c = file.getChunk(chunkNo);
						c.setPath("tmp/");
						int length = c.storeData(msg.getChunk());
						System.out.println(MESSAGE + " I received my Chunk :)");
						
						//TODO: Check if last chunk -> if so Merge it together
						
						
						if (length < 64000) //last Chunk
						{
							//TODO: Check if has all Chunks
							
							new Thread (new Runnable() {
								
								@Override
								public void run() {
									file.selfRestore();
								}
							}).start();
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
	
	
	 public void askRestoreFile(LocalFile f) {
		 
		 MDR mdr = BackupService.getMdr();

		 int previousLenght = 64000;
		 while(true) {
			 try {
				 byte[] z = f.nextChunk();

				 if(z == null && previousLenght < 64000) //The previous loop was the last chunk
					 break;

				 int deltaT = 400;
				 int count = 0;

				 MessagePutChunk msg = new MessagePutChunk(f.getId(), f.getOffset(), f.getReplicationDeg());
				 msg.setChunk(z);

				 while(count < 5) {
					 mdr.sendMessage(msg); //send Message
					 Thread.sleep(deltaT); //wait for stored messages

					 //check replication rate
					 if(f.getChunk(f.getOffset()).getCurReplicationDeg() >= f.getReplicationDeg())
						 break;

					 count++;
					 deltaT *= 2;
				 }

				 if(z == null) //last chunk with size 0
					 break;

				 previousLenght = z.length;
			 } catch (IOException e) {
				 e.printStackTrace();
			 } catch (InterruptedException e) {
				 e.printStackTrace();
			 }
		 }

	 }
}

