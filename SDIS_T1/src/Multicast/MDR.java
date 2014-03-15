package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;

public class MDR implements Runnable {

	private static final String MESSAGE="Multicast data channel RESTORE:";
	private Multicast channel;
	
	public MDR(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	@Override
	public void run() {
		while(true) {
			//Can receive:
			// - CHUNK
			try {
				System.out.println(Message.getMessageType(channel.receive()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
