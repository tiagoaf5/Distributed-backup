package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import GUI.Window;
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

		System.out.println("Running multicast data channel for RESTORE...\n");

		while(true) {
			//Can receive:
			// - CHUNK
			try { 
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);

				if(type.equals("CHUNK")) {

					MessageChunk msg=new MessageChunk();

					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) {
						if(!msg.getVersion().equals(BackupService.getVersionEnhancement()))
						{
							System.out.println(MESSAGE + "Wrong format! Ignoring..");
							Window.log(MESSAGE + "Wrong format! Ignoring..");
							continue;
						}
						else {
							System.out.println(MESSAGE + "Version 2.2..");
							Window.log(MESSAGE + "Version 2.2..");
						}
					}

					System.out.println(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					//System.out.println(" received - CHUNK " + msg.getChunk().toString());
					Window.log(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());


					if(msg.getVersion().equals(BackupService.getVersionEnhancement()) && 
							!(msg.getVersion().equals(BackupService.getVersion()))) {
						continue;
					}

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
						System.out.println(MESSAGE + " received chunk with length " + length );

						if(length<64000 || file.hasReceivedAll()) //last Chunk //TODO: && ou || ??
						{
							new Thread (new Runnable() {

								@Override
								public void run() {

									System.out.println(MESSAGE + " restoring file " + file.getFileName());
									Window.log(MESSAGE + " restoring file " + file.getFileName());

									file.selfRestore();

									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									file.unCheckReceivedAll();
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
					Window.log(MESSAGE + " - Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(Message x) throws IOException {

		System.out.println(MESSAGE + " sended " + x.getType() + " FileId: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());
		Window.log(MESSAGE + " sended " + x.getType() + " FileId: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());

		channel.send(x.getMessage());
	}
}

