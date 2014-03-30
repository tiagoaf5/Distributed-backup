package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import GUI.Window;
import Messages.Message;
import Messages.MessagePutChunk;
import Messages.MessageStored;
import Service.BackupService;
import Service.LocalFile;
import Service.RemoteFile;


public class MDB extends Thread {

	private static final String MESSAGE="MDB ";
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
					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) { 
						System.out.println(MESSAGE + "Wrong format! Ignoring..");
						Window.log(MESSAGE + "Wrong format! Ignoring..");
						continue;
					}

					System.out.println(MESSAGE + " received - PUTCHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					Window.log(MESSAGE + " received - PUTCHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					if(BackupService.getAvailableDiskSpace() - msg.getChunkSize() < 0) { 
						//System.out.println("available: " + BackupService.getAvailableDiskSpace());
						//System.out.println("chunk: " + msg.getChunkSize());
						//System.out.println("disk: " + BackupService.getDiskSpace());
						
						System.out.println(MESSAGE + "Maximum disk space reached! Ignoring chunk");
						Window.log(MESSAGE + "Maximum disk space reached! Ignoring chunk");
						continue;
					}
					else
						System.out.println(BackupService.getAvailableDiskSpace()); //TODO: remove
					
					if(!BackupService.isLocal(msg.getFileId())/* || true*/) { //TODO: Remove this true
						RemoteFile file = BackupService.getRemote(msg.getFileId());

						final String fileId = msg.getFileId();
						final int chunkNo = msg.getChunkNo();
						boolean alreadyStored = false;

						if(file == null) { //fileId not found
							
							file = new RemoteFile(msg.getFileId(), msg.getReplicationDeg());
							file.addChunk(msg); //creates file with chunk data
							BackupService.addRemoteFile(msg.getFileId(), file);
							System.out.println(MESSAGE + " added new remote file - FileId: " + msg.getFileId());
							Window.log(MESSAGE + " added new remote file - FileId: " + msg.getFileId());

						} else {
							alreadyStored = !file.addChunk(msg);

							if(alreadyStored) //true if chunkNo already stored
								System.out.println(MESSAGE + " chunk already stored");
						}

						//if(!alreadyStored) {
						new Thread(new Runnable() {

							@Override
							public void run() {

								try {
									int r = ThreadLocalRandom.current().nextInt(0,401);
									RemoteFile f = BackupService.getRemote(fileId);
									sleep(r);

									//Enhancement 1 - if the current replication degree is greater or equal than the wanted discard chunk
									if(f.getChunk(chunkNo).getCurReplicationDeg() - 1 >= f.getReplicationDeg()) {
										f.removeChunk(chunkNo);

										if(f.getNumberChunks()==0) //if has no chunks left delete file
											BackupService.deleteRemoteFile(f.getId());

										return;
									}
									//if(chunkNo % 2 == 0)
									MessageStored answer=new MessageStored(fileId, chunkNo);
									BackupService.getMc().sendMessage(answer);
									BackupService.saveRemoteOnDisk();
									
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}
						}).start();
						//}

					} else {
						System.out.println(MESSAGE + " local file");
					}
					msg=null;
				} else {
					System.out.println(MESSAGE + " - Invalid message!");
					Window.log(MESSAGE + " - Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(MessagePutChunk msg) {

		System.out.println(MESSAGE + "sended " + msg.getType() + " FileID: " + msg.getFileId() +
				" ChunkNo: " + msg.getChunkNo());
		Window.log(MESSAGE + "sended " + msg.getType() + " FileID: " + msg.getFileId() +
				" ChunkNo: " + msg.getChunkNo());
		
		try {
			channel.send(msg.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void backupFiles() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<LocalFile> localFiles = BackupService.getLocalFiles();

				for (int i = 0; i < localFiles.size(); i++){
					backupFile(localFiles.get(i));
					try {
						sleep(ThreadLocalRandom.current().nextInt(200, 2000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				BackupService.getBackupHandler().start();
			}
		}).start();

	}

	public void backupFile(LocalFile f) {

		System.out.println(MESSAGE + "started backup file " + f.getFileName());
		
		int previousLenght = 64000;
		while(true) {
			try {
				byte[] z = f.nextChunk();
				if(z == null && previousLenght < 64000) //The previous loop was the last chunk
					break;

				int deltaT = 500;
				int count = 0;

				MessagePutChunk msg = new MessagePutChunk(f.getId(), f.getOffset(), f.getReplicationDeg());
				msg.setChunk(z);
				
				while(count < 5) {
					sendMessage(msg); //send Message
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

	public synchronized void  backupChunk(LocalFile f, int chunkNo) {
		try {

			int deltaT = 500;
			int count = 0;

			MessagePutChunk msg = new MessagePutChunk(f.getId(), chunkNo, f.getReplicationDeg());
			byte[] z = f.getChunkData(chunkNo);
			msg.setChunk(z);

			while(count < 5) {
				sendMessage(msg); //send Message
				Thread.sleep(deltaT); //wait for stored messages

				//check replication rate
				if(f.getChunk(f.getOffset()).getCurReplicationDeg() >= f.getReplicationDeg())
					break;

				count++;
				deltaT *= 2;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
