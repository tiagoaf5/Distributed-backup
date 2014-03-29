package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

import GUI.Window;
import Messages.Message;
import Messages.MessageChunk;
import Messages.MessageDelete;
import Messages.MessageGetChunk;
import Messages.MessageRemoved;
import Messages.MessageStored;
import Service.BackupService;
import Service.BackupStatusHandler;
import Service.Chunk;
import Service.LocalFile;
import Service.Packet;
import Service.RemoteFile;

public class MC extends Thread {

	private static final String MESSAGE="MC ";
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

					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) {
						System.out.println(MESSAGE + "Wrong format! Ignoring..");
						Window.log(MESSAGE + "Wrong format! Ignoring..");
						continue;
					}

					System.out.println(MESSAGE + "received: STORED FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					Window.log(MESSAGE + "received: STORED FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					LocalFile local = BackupService.getLocal(msg.getFileId());
					if(!(local == null)) {
						local.increaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());
					}

					RemoteFile remote = BackupService.getRemote(msg.getFileId());
					if(!(remote == null)) { 
						//if file doesn't have chunkNo chunk doesn't do anything
						remote.increaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());
						BackupService.saveRemoteOnDisk();
					}	

					msg=null;
				} 
				else if(type.equals("GETCHUNK")) {

					final MessageGetChunk msg = new MessageGetChunk();

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

					System.out.println(MESSAGE + "received: GETCHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					Window.log(MESSAGE + "received: GETCHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					if(BackupService.isRemote(msg.getFileId(), msg.getChunkNo())) {

						new Thread(new Runnable() {

							@Override
							public void run() {

								try {
									RemoteFile file = BackupService.getRemote(msg.getFileId());
									byte[] data = file.getChunkData(msg.getChunkNo());

									int r = ThreadLocalRandom.current().nextInt(0,401);
									sleep(r);

									if(!BackupService.getRemote(msg.getFileId()).getChunk(msg.getChunkNo()).isChecked()) {
										//if no other peer was faster than me
										if(BackupService.getVersionEnhancement() == BackupService.getVersion()) {
											MessageChunk answer = msg.getAnswer(data);
											BackupService.getMdr().sendMessage(answer);		
										}
										else {
											MessageChunk answer = msg.getAnswer(new byte[]{0x00});
											answer.setVersion(BackupService.getVersionEnhancement());
											BackupService.getMdr().sendMessage(answer);
											
										}
									}

									data = null;	

								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}).start();

					} 
					else {
						System.out.println(MESSAGE + " chunk not found");
					}

				} else if(type.equals("DELETE")) {
					MessageDelete msg=new MessageDelete();

					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) {
						System.out.println(MESSAGE + "Wrong format! Ignoring..");
						Window.log(MESSAGE + "Wrong format! Ignoring..");
						continue;
					}

					System.out.println(MESSAGE + "received: DELETE FileId: " + msg.getFileId());
					Window.log(MESSAGE + "received: DELETE FileId: " + msg.getFileId());


					RemoteFile remote=BackupService.getRemote(msg.getFileId());
					if(remote == null) {
						System.out.println(MESSAGE + " file not found");
					} else {

						BackupService.deleteRemoteFile(msg.getFileId()); 
						BackupService.saveRemoteOnDisk();

						sendMessage(msg); //Enhancement 3

						System.out.println(MESSAGE + " deleting file with FileId: " + msg.getFileId());
						Window.log(MESSAGE + " deleting file with FileId: " + msg.getFileId());
					}

					LocalFile local = BackupService.getLocal(msg.getFileId());
					if(local != null) {
						local.increaseCountDeleted(); //Enhancement 3
					}


					msg=null;

				} else if(type.equals("REMOVED")) {
					final MessageRemoved msg = new MessageRemoved();

					if(msg.parseMessage(rcv) == -1 || !(msg.getVersion().equals(BackupService.getVersion()))) {
						System.out.println(MESSAGE + "Wrong format! Ignoring..");
						Window.log(MESSAGE + "Wrong format! Ignoring..");
						continue;
					}

					System.out.println(MESSAGE + "received: REMOVED FileId: " 
							+ msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					Window.log(MESSAGE + "received: REMOVED FileId: " 
							+ msg.getFileId() + " ChunkNo: " + msg.getChunkNo());

					RemoteFile remote=BackupService.getRemote(msg.getFileId());
					final LocalFile local=BackupService.getLocal(msg.getFileId());

					if(remote != null) {
						BackupService.saveRemoteOnDisk();
						remote.decreaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());
					}
					/*else*/ if (local != null) { //TODO: uncomment else 
						local.decreaseCurReplicationDeg(msg.getChunkNo(), pkt.getIp());

						if (local.getChunk(msg.getChunkNo()).getCurReplicationDeg() < local.getReplicationDeg()) {
							new Thread (new Runnable() {

								@Override
								public void run() {
									try {
										sleep(ThreadLocalRandom.current().nextInt(0,401));
										BackupStatusHandler.stopIt();
										BackupService.getMdb().backupChunk(local, msg.getChunkNo());
										BackupStatusHandler.startIt();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}			
								}
							}).start();
						}
					}

				} else {
					System.out.println(MESSAGE + " - Invalid message!");
					Window.log(MESSAGE + " - Invalid message type!");

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void sendMessage(Message x) throws IOException {

		System.out.println(MESSAGE + "sended " + x.getType() + " FileID: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());
		Window.log(MESSAGE + "sended " + x.getType() + " FileID: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());

		channel.send(x.getMessage());
	}

	public void sendBytes(byte[] x) throws IOException {
		System.out.println(MESSAGE + "Sending Message..");
		channel.send(x);
	}


	public void askRestoreFile(LocalFile f) {

		try {
			System.out.println(MESSAGE + "Restoring file " + f.getFileName());
			
			if(BackupService.getVersion() != BackupService.getVersionEnhancement()) {
				//TODO: Start UDP receive
			}

			for(int i=0; i<f.getNumberChunks(); i++) {

				int deltaT = 500;
				int count = 0;
				MessageGetChunk msg = new MessageGetChunk(f.getId(), i);
				msg.setVersion(BackupService.getVersionEnhancement());
						

				while(count<5) {

					sendMessage(msg); //send Message
					Thread.sleep(deltaT); //wait for chunk messages

					Chunk x = f.getChunk(i);
					//System.out.println(x.getRestored());

					if(x.getRestored())
						break;

					count++;
					deltaT+=500;

					if(count == 5) {
						System.out.println(MESSAGE + "Restore didn't work, aborting..");
						Window.log(MESSAGE + "Restore " + f.getFileName() + " didn't work");
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void askDeleteFile(LocalFile f)  {

		try {
			System.out.println(MESSAGE + "Deleting file " + f.getFileName());

			MessageDelete msg = new MessageDelete(f.getId());
			sendMessage(msg); 


			//Enhancement: other peers that have file resend the Delete Message: if I get enough deletes 
			// everyone deleted it if I don't I'll try periodically later to send this message again
			try {
				Thread.sleep(1000);

				if (!(f.getCountDeleted() >= f.getHighestReplicationDeg())) {
					BackupStatusHandler.addPendentMessage(msg);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}



			BackupService.deleteLocalFile(f); //delete local file

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sayRemovedFile (Chunk c) {

		try {
			System.out.println(MESSAGE + "removing fileId " + c.getFileId() + " chunkNo: " + c.getChunkNo());

			MessageRemoved msg = new MessageRemoved(c.getFileId(),c.getChunkNo());
			sendMessage(msg); //send Message
			//TODO: find a better way

		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
}
