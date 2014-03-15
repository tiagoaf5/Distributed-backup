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
			try { //isto eu usei para experimentar agora tens de fazer o que se tem de fazer
				System.out.println(Message.getMessageType(channel.receive())); //TODO: é isto que tens de fazer
				//o que fazer quando recebe uma mensagem PUTCHUNK tens de ignorar as provenientes do teu pcs
				//capcihe?? o que faz isto
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
