package Multicast;

import java.io.IOException;
import java.net.InetAddress;

import Messages.Message;
import Messages.MessagePutChunk;

public class MDB extends Thread {

	Multicast channel;
	
	public MDB(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	public void run() {
		System.out.println("...");
		while(true) {
			
			//Can receive:
			// - PUTCHUNK
			try {
				System.out.println(Message.getMessageType(channel.receive()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void sendMessage(MessagePutChunk msg) {
		System.out.println("Sending Message");
		try {
			channel.send(msg.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
