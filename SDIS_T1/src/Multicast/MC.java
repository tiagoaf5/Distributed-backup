package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;


public class MC implements Runnable {
	Multicast channel;
	
	public MC(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
		
	}

	@Override
	public void run() {
		while(true) {
			try {
				System.out.println(Message.getMessageType(channel.receive()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
