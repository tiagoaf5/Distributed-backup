package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import GUI.Window;
import Messages.Message;
import Messages.MessageChunk;
import Service.BackupService;
import Service.Chunk;
import Service.LocalFile;

public class UDP extends Thread {
	private final String MESSAGE = "UDP Channel ";
	private final int SERVER_PORT = 7652;
	private final int SIZE = 65000; 

	/*
	 * Destination address for sender
	 * Not used for receiver
	 */
	private InetAddress address;


	private int portNumber;
	private DatagramSocket socket;

	//constructor for sender
	public UDP(String string1) throws UnknownHostException, SocketException {

		address=InetAddress.getByName(string1);
		portNumber = SERVER_PORT;
		socket = new DatagramSocket();
	}

	//constructor for sender
	public UDP(InetAddress add) throws SocketException {
		address = add;
		portNumber=SERVER_PORT;
		socket=new DatagramSocket();
	}

	//constructor for receiver
	public UDP() throws SocketException {
		portNumber = SERVER_PORT;
		socket = new DatagramSocket(portNumber);
	}

	public byte[] receive() {

		byte[] buf = new byte[SIZE];

		try {	
			DatagramPacket sp = new DatagramPacket(buf, buf.length); 

			socket.receive(sp);

			byte[] data = new byte[sp.getLength()];
			System.arraycopy(sp.getData(), sp.getOffset(), data, 0, sp.getLength());

			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buf;
	}

	public String receiveString() {

		byte[] buf = new byte[SIZE];
		DatagramPacket sp = new DatagramPacket(buf, buf.length); 
		try {	
			socket.receive(sp);
			//System.out.println("Received " + new String(p.getData()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(sp.getData());
	}

	public void send(byte[] msg) {

		System.out.println(MESSAGE + "Sending");
		try {
			DatagramPacket p = new DatagramPacket(msg, msg.length, address, portNumber);
			socket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message x) {

		System.out.println(MESSAGE + " sended " + x.getType() + " FileId: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());

		Window.log(MESSAGE + " sended " + x.getType() + " FileId: " + x.getFileId() +
				" ChunkNo: " + x.getChunkNo());
		
		send(x.getMessage());
	}

	public void sendString(String msg) {

		try {
			DatagramPacket p = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, portNumber);
			socket.send(p);
			//System.out.println("Sent " + new String(p.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopIt() {
		socket.close();
	}

	public void run() {
		while(true) {
			//Can receive:
			// - CHUNK
			try { 
				byte[] rcv = receive();
				String type=Message.getMessageType(rcv);

				if(type.equals("CHUNK")) {

					MessageChunk msg=new MessageChunk();

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

					System.out.println(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());
					//System.out.println(" received - CHUNK " + msg.getChunk().toString());
					Window.log(MESSAGE + " received - CHUNK FileId: " + msg.getFileId() + " ChunkNo: " + msg.getChunkNo());


					final LocalFile file = BackupService.getLocal(msg.getFileId());
					String fileId = msg.getFileId();
					int chunkNo = msg.getChunkNo();

					if(file != null) {

						//it's a file I asked to restore -> save it in tmp folder
						Chunk c = file.getChunk(chunkNo);
						c.setPath("tmp/");
						c.setRestored(true);


						int length = c.storeData(msg.getChunk());
						System.out.println(MESSAGE + " received chunk with length " + length );

						if(length<64000 || file.hasReceivedAll()) //last Chunk
						{
							new Thread (new Runnable() {

								@Override
								public void run() {

									System.out.println(MESSAGE + " restoring file " + file.getFileName());
									Window.log(MESSAGE + " restoring file " + file.getFileName());

									file.selfRestore();

									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									file.unCheckReceivedAll();
								}
							}).start();

							stopIt();
							return;

						}
					} 

					msg=null;
				} 
				else {
					System.out.println(MESSAGE + " - Invalid message!");
					Window.log(MESSAGE + " - Invalid message!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
