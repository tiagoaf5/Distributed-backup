package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Messages.MessagePutChunk;
import Service.BackupService;
import Service.LocalFile;
import Service.RemoteFile;

public class MDR implements Runnable {

	private static final String MESSAGE="Multicast data channel RESTORE:";
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
				System.out.println(MESSAGE + " received - " + Message.byteArrayToHexString(rcv));

				if(type.equals("CHUNK")) {

					MessageChunk msg=new MessageChunk();
					msg.parseMessage(rcv);
					
					LocalFile local = BackupService.getLocal(msg.getFileId());
					
					if(!(local==null)) {
						byte[] chunk=msg.getChunk();
						
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
	

	public void sendMessage(Message x) throws IOException {
		System.out.println(MESSAGE + "Sending Message..");
		channel.send(x.getMessage());
	}
}

