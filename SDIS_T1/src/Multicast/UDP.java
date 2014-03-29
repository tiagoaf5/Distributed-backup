package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP {

	private final int SIZE = 65000; 

	private InetAddress address;
	private int portNumber;
	private DatagramSocket socket;

	public UDP(String string1, String string2) throws UnknownHostException, SocketException {

		address=InetAddress.getByName(string1);
		portNumber = Integer.parseInt(string2);
		socket = new DatagramSocket(portNumber);
	}

	public UDP(InetAddress add, int port) throws SocketException {

		address=add;
		portNumber=port;
		socket=new DatagramSocket(port);
	}

	public byte[] receive() {

		byte[] buf = new byte[SIZE];

		try {	
			DatagramPacket sp = new DatagramPacket(buf, buf.length); 
			socket.receive(sp);
			//System.out.println("Received " + new String(p.getData()));

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

		try {
			DatagramPacket p = new DatagramPacket(msg, msg.length, address, portNumber);
			socket.send(p);
			//System.out.println("Sent " + new String(p.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
