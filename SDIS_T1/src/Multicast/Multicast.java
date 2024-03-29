package Multicast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Service.Packet;

public class Multicast {

	private final int SIZE = 65000;

	private InetAddress mAddress;
	private int mPortNumber;
	private MulticastSocket mClient;

	public Multicast() {}

	public Multicast(String address, String port) throws IOException {

		mAddress=InetAddress.getByName(address);
		mPortNumber = Integer.parseInt(port);
		mClient = new MulticastSocket(mPortNumber);
		mClient.joinGroup(mAddress);
	}

	public Multicast(InetAddress address, int port) throws IOException {

		mAddress=address;
		mPortNumber = port;
		mClient = new MulticastSocket(mPortNumber);
		mClient.joinGroup(mAddress);
	}

	public void leaveGroup() {

		try {
			mClient.leaveGroup(mAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mClient.close();
	}

	public void send(byte[] toSend) throws IOException {

		DatagramPacket p = new DatagramPacket(toSend, toSend.length, mAddress, mPortNumber);
		mClient.send(p);
		//System.out.println("Sent " + new String(p.getData()));
	}

	public byte[] receive() throws IOException {
		Packet p = receive1();
		return p.getData();
	}

	public Packet receive1() throws IOException {

		byte[] buf = new byte[SIZE];
		DatagramPacket sp = new DatagramPacket(buf, buf.length); 
		/*sp.getLength();*/

		mClient.receive(sp);

		byte[] data = new byte[sp.getLength()];
		System.arraycopy(sp.getData(), sp.getOffset(), data, 0, sp.getLength());

		//System.out.println("Received " + new String(sp.getData()));
		return new Packet(sp.getAddress(), data);
	}
}
