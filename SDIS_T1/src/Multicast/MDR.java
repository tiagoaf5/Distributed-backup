package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Messages.MessagePutChunk;
import Service.BackupService;
import Service.RemoteFile;

public class MDR implements Runnable {

	private static final String MESSAGE="Multicast data channel RESTORE:";
	private Multicast channel;
	private static String fileId;
	private static int chunkNo;
	
	public MDR(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		fileId=null;
		chunkNo=0;
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
				System.out.println(MESSAGE + " received - " + Message.byteArrayToHexString(rcv));

				if(type.equals("CHUNK")) {

					MessageChunk msg=new MessageChunk();
					msg.parseMessage(rcv);

					if(asked(msg.getFileId(), msg.getChunkNo())) {
						byte[] chunk=msg.getChunk();
						//TODO: o que fazer com o chunk?
					} else {
						System.out.println(MESSAGE + " chunk not asked");
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

	private boolean asked(String id, int chunk) {
		return (id.equals(fileId)&&chunkNo==chunk);
	}
}

