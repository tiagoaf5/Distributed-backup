package Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import Messages.Message;
import Messages.MessagePutChunk;
import Service.BackupService;
import Service.LocalFile;


public class MDB extends Thread {

	Multicast channel;
	
	public MDB(InetAddress address, int port) throws IOException {
		channel = new Multicast(address, port);
	}

	public void run() {
		
		System.out.println("...");
		
		while(true) {
			System.out.println(".");
			//Can receive:
			// - PUTCHUNK
			
			try { 
				byte[] rcv=channel.receive();
				String type=Message.getMessageType(rcv);
				System.out.println("Received: " + Message.getMessageType(channel.receive()));
				
				if(type.equals("PUTCHUNK")) {
					
					MessagePutChunk msg=new MessagePutChunk(); //TODO: assim nao estou a guardar o chunk mesmo
					//que afinal nao fosse para guardar??
					msg.parseMessage(rcv);
					
					if(!local(msg.getFileId())) {
						
					} else 
						System.out.println("Local file");
					
				} else {
					System.out.println("Invalid message!");
				}
				
				//TODO: é isto que tens de fazer
				//o que fazer quando recebe uma mensagem PUTCHUNK tens de ignorar as provenientes do teu pcs
				//capcihe?? o que faz isto
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private boolean local(String fileId) {
		
		List<LocalFile> localFiles=BackupService.getLocalFiles();
		
		int i=0;
		
		while(i<localFiles.size()) {
			LocalFile temp=localFiles.get(i);
			if(temp.getId().equals(fileId))
				return true;
			i++;
		}
		return false;
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
