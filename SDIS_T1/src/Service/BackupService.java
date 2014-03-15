package Service;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private MDB mdb;
	private MDR mdr;
	private MC mc;

	private static final String FILENAME = "files.txt";
	private static final String FILEBEGIN = "/*File format:";
	private static final int COMMENTSIZE = 13;

	static private int diskSpace; //em kBytes
	static private List<LocalFile> localFiles; 
	static private HashMap<String, RemoteFile> remoteFiles;

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
		System.out.println(Message.byteArrayToHexString(a.getMessage()));

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
		remoteFiles = new HashMap<String,RemoteFile>();
		localFiles = new ArrayList<LocalFile>();
		readFile();

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



	private void readFile() {
		
		FileInputStream fileStream;

		try {
			System.out.println("Reading file "+FILENAME+"...");
			
			fileStream = new FileInputStream(FILENAME);
			DataInputStream input = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;

			if((line=reader.readLine())!=null) {
				if(line.startsWith(FILEBEGIN)) {
					for(int i=0; i<COMMENTSIZE; i++) {
						line = reader.readLine();
					}
				}
				try {
					diskSpace = Integer.parseInt(line);
					System.out.println("Total disk space: " + diskSpace);
				} catch(Exception e) {
					System.out.println("ERRO files.txt mal definido: falta espaço máximo de disco.");
					reader.close();
					return;
				}
			} else {
				System.out.println("ERRO files.txt vazio.");
				reader.close();
				return;
			}

			while ((line = reader.readLine()) != null)   {

				String[] splits=line.split(" - ", 2);
				if(splits.length!=2) {
					System.out.println("ERRO files.txt: linha " + line + " mal definida.");
					reader.close();
					return;
				}

				LocalFile newFile=new LocalFile(splits[1], splits[0]);
				localFiles.add(newFile);

				//System.out.println(line);
				//System.out.println(splits[0] + ", " + splits[1]);
			}
			System.out.println("Number of local files: " + localFiles.size() +"\n");

			reader.close();
			input.close();

		} catch (FileNotFoundException e) {
			System.out.println("ERROR: files.txt não encontrado");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getDiskSpace() {
		return diskSpace;
	}

	public static List<LocalFile> getLocalFiles() {
		return localFiles;
	}

	public static HashMap<String, RemoteFile> getRemoteFiles() {
		return remoteFiles;
	}
	
	public static boolean addRemoteFile(String fileId, RemoteFile file) {
		
		if(remoteFiles.containsKey(fileId))
			return false;
		else {
			remoteFiles.put(fileId, file);
			return true;
		}
	}

}
