package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import Messages.Message;
import Messages.MessagePutChunk;
import Messages.MessageStored;
import Service.BackupService;
import Service.LocalFile;
import Service.RemoteFile;


public class MDB extends Thread {

	private static final String MESSAGE="Multicast data channel BACKUP: ";
	private Multicast channel;

	public MDB(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	public void run() {

		System.out.println("Running multicast data channel for BACKUP...");

		while(true) {
			//Can receive:
			// - PUTCHUNK

			try { 
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);
				//System.out.println(MESSAGE + " received - " + Message.byteArrayToHexString(rcv));

				if(type.equals("PUTCHUNK")) {


					MessagePutChunk msg=new MessagePutChunk();
					msg.parseMessage(rcv);

					System.out.println(MESSAGE + " received - PUTCHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					if(!isLocal(msg.getFileId()) || true) { //TODO: Remove this true
						RemoteFile file = getRemote(msg.getFileId());

						final String fileId = msg.getFileId();
						final int chunkNo = msg.getChunkNo();
						boolean alreadyStored = false;

						if(file == null) {

							file = new RemoteFile(msg.getFileId(), msg.getReplicationDeg());
							file.addChunk(msg);
							BackupService.addRemoteFile(msg.getFileId(), file);
							System.out.println(MESSAGE + " added new remote file");

						} else {
							alreadyStored = !file.addChunk(msg);
							if(alreadyStored)
								System.out.println(MESSAGE + " chunk already stored");
						}
						
						if(!alreadyStored) {
							new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										int r = ThreadLocalRandom.current().nextInt(0,401);
										RemoteFile f = getRemote(fileId);
										sleep(r);
										
										//Enhancement 1 - if the current replication degree is greater or equal than the wanted discard chunk
										if(f.getChunk(chunkNo).getCurReplicationDeg() - 1 >= f.getReplicationDeg()) {
											f.removeChunk(chunkNo);
											return;
										}

										BackupService.getMc().sendMessage(new MessageStored(fileId, chunkNo));
									} catch (IOException e) {
										e.printStackTrace();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}
							}).start();
						}

					} else {
						System.out.println(MESSAGE + " local file");
					}
					msg=null;
				} else {
					System.out.println(MESSAGE + " - Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private boolean isLocal(String fileId) {

		List<LocalFile> localFiles = BackupService.getLocalFiles();

		int i=0;

		while(i<localFiles.size()) {
			LocalFile temp = localFiles.get(i);
			if(temp.getId().equals(fileId))
				return true;
			i++;
		}
		return false;
	}

	private RemoteFile getRemote(String fileId) {

		HashMap<String, RemoteFile> remoteFiles=BackupService.getRemoteFiles();
		return remoteFiles.get(fileId);
	}

	public void sendMessage(MessagePutChunk msg) {

		System.out.println(MESSAGE + "Sending Message..");
		try {
			channel.send(msg.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
