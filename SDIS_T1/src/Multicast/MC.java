package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;

public class MC implements Runnable {
	
	private static final String MESSAGE="Multicast data channel CONTROL:";
	private Multicast channel;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		
	}

	@Override
	public void run() {
		while(true) {
			
			//Can receive:
			// - STORED
			// - GETCHUNK
			// - DELETE
			// - REMOVED
			try {
				System.out.println(Message.getMessageType(channel.receive()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMessage(Message x) throws IOException {
		channel.send(x.getMessage());
	}
}
