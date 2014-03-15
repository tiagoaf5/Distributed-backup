package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessageChunk;
import Messages.MessageDelete;
import Messages.MessageGetChunk;
import Messages.MessageRemoved;
import Messages.MessageStored;

public class MC implements Runnable {
	
	private static final String MESSAGE="Multicast data channel CONTROL:";
	private Multicast channel;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		
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

					msg=null;
				} else if(type.equals("GETCHUNK")) {
					MessageGetChunk msg=new MessageGetChunk();
					msg.parseMessage(rcv);
					
					msg=null;
				} else if(type.equals("DELETE")) {
					MessageDelete msg=new MessageDelete();
					msg.parseMessage(rcv);
					
					msg=null;
				} else if(type.equals("REMOVED")) {
					MessageRemoved msg=new MessageRemoved();
					msg.parseMessage(rcv);
					
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
		channel.send(x.getMessage());
	}
}
