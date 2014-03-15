import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import Messages.Message;
import Messages.MessagePutChunk;
import Multicast.*;

public class BackupService {

	private InetAddress mcAddress;
	private int mcPort;
	private InetAddress mdbAddress;
	private int mdbPort;
	private InetAddress mdrAddress;
	private int mdrPort;

	private LocalFiles localFiles;
	private MDB mdb;
	private MDR mdr;
	private MC mc;

	static private HashMap<String, RemoteFile> remoteFiles;
	//static private HashMap<String, LocalFile> localFiles;

	public static void main(String[] args) throws IOException {
		/*
		try {
			BackupService a = new BackupService(args);
			a.initReceivingThreads();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		MyFile f = new MyFile("1.pdf", 1);
		 */
		BackupService a = new BackupService(args);
		a.initReceivingThreads();
	}

	private void initReceivingThreads() {
		mdb.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		MessagePutChunk a = new MessagePutChunk("41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d", 5, 2);
		a.setChunk(Message.hexStringToByteArray("41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d41681c7cf03673502976034bfd68260d5663b8075192a89495265e3057ab8b7d"));
		mdb.sendMessage(a);
		
		/*
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
		mdb = new MDB(mdbAddress, mdbPort);
		mdr = new MDR(mdrAddress, mdrPort);
	}	

}
