import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import Messages.Message;
import Messages.MessageStored;
import Multicast.*;


public class BackupService {

	private InetAddress mcAddress;
	private int mcPort;
	private InetAddress mdbAddress;
	private int mdbPort;
	private InetAddress mdrAddress;
	private int mdrPort;

	LocalFiles localFiles;
	Multicast mdb;
	Multicast mdr;
	MC mc;


	public static void main(String[] args) {

		try {
			BackupService a = new BackupService(args);
			a.initReceivingThreads();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}




		/*
		byte[] nabo = new byte[11];
		byte[] nabao = new byte[64000];

		int i= 0;
		try {
			MyFile f = new MyFile("1.pdf", 1);
			while(true) {

				nabao = f.nextChunk();

				if(nabao == null)
					break;

				nabo[i] =nabao[0];
				System.out.println(i);
				i++;
			}

			System.out.println("->"+ Message.byteArrayToHexString(nabo) + "<-");
		} catch (IOException e) {
			e.printStackTrace();
		}	*/
	}

	public BackupService(String args[]) throws UnknownHostException {

		this.mcAddress =  InetAddress.getByName(args[0]);
		this.mcPort = Integer.parseInt(args[1]);
		this.mdbAddress = InetAddress.getByName(args[2]);
		this.mdbPort = Integer.parseInt(args[3]);
		this.mdrAddress = InetAddress.getByName(args[4]);
		this.mdrPort = Integer.parseInt(args[5]);

		//localFiles = new LocalFiles(); //get files to backup info

		try {
			openMulticastSessions();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void openMulticastSessions() throws IOException {
		mc = new MC(mcAddress, mcPort);
		mdb = new Multicast(mdbAddress, mdbPort);
		mdr = new Multicast(mdrAddress, mdrPort);
	}

	public void initReceivingThreads() throws InterruptedException {/*
		MessageStored abc = new MessageStored("41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d",5);
		try {
			while(true) {
				mc.sendMessage(abc);
				System.out.println(Message.byteArrayToHexString(abc.getMessage()));
				Thread.sleep(2000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

}
