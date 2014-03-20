package Service;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Messages.MessageGetChunk;
import Multicast.*;

public class BackupService {

	private InetAddress mcAddress;
	private int mcPort;
	private InetAddress mdbAddress;
	private int mdbPort;
	private InetAddress mdrAddress;
	private int mdrPort;

	private static MDB mdb;
	private static MDR mdr;
	private static MC mc;

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
		//a.showInterface();
	}

	private void initReceivingThreads() {
		mc.start();
		mdb.start();
		mdr.start();
		
		
		mdb.backupFile(localFiles.get(0));
		
		
		try {
			Thread.sleep(10000);
			/*for(int i = 0; i < 11; i++) {
				mc.sendMessage(new MessageGetChunk(localFiles.get(0).getId(),i));
				Thread.sleep(500);
			}*/
			mc.askRestoreFile(localFiles.get(0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	/*	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LocalFile x = localFiles.get(0);
		Random r = new Random();
		
		while(true) {
			try {
				byte[] z = x.nextChunk();
				
				if(z == null)
					break;
				
				MessagePutChunk msg = new MessagePutChunk(x.getId(), x.getOffset(), x.getReplication());
				msg.setChunk(z);
				mdb.sendMessage(msg);
				//System.out.println(Message.byteArrayToHexString(msg.getMessage()));
				Thread.sleep(r.nextInt(400)+1);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		createFolders();

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

	public static void deleteRemoteFile(String fileId) {
		getRemote(fileId).delete();
		remoteFiles.remove(fileId);
	}
	
	private void createFolders() {
		File dir1 = new File("RemoteFiles");
		File dir2 = new File("tmp");
		
		if (!dir1.exists())
			dir1.mkdir();
		
		if (!dir2.exists())
			dir2.mkdir();
	}

	public void showInterface() {
		
		boolean menu=true;
		Scanner in = new Scanner(System.in);
		
		while(menu) {
			System.out.println("Choose an option (1-5): \n 1. Backup a file \n"
					+ " 2. Restore a file\n 3. Delete a file\n"
					+ " 4. Free disk space\n 5. Terminate");

			String s;
			int option;
			s=in.nextLine();
			
			try{
				option=Integer.parseInt(s);
				int res;
				switch(option) {
				case 1:
					res=chooseFile();
					//TODO: backup
					break;
				case 2:
					res=chooseFile();
					//TODO: restore
					break;
				case 3:
					res=chooseFile();
					//TODO: delete
					break;
				case 4: 
					res=getQuantity();
					//TODO: free disk space
					break;
				case 5:
					menu=false;
					break;
				}
			} catch (NumberFormatException e){
				
			}
		}
		in.close();
	}
	
	private int getQuantity() {
		
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Choose a size between 1 and "+diskSpace+":");

			String s;
			int size;
			s=in.nextLine();

			try{
				size=Integer.parseInt(s);
				if(size>0 && size<=diskSpace)
					return size;
			} catch (NumberFormatException e){

			}
		}
	}

	private int chooseFile() {

		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Choose a file (1-"+localFiles.size()+"):");
			
			int i=0; int j=1;
			while(i<localFiles.size()) {
				System.out.println(j+". "+localFiles.get(i).getName());
				j++;
				i++;
			}
			
			String s;
			int option;
			s=in.nextLine();

			try{
				option=Integer.parseInt(s);
				if(option>0 && option<=localFiles.size())
					return option;
			} catch (NumberFormatException e){

			}
		}
	}

	public static MDB getMdb() {
		return mdb;
	}

	public static MDR getMdr() {
		return mdr;
	}

	public static MC getMc() {
		return mc;
	}
	
	public static boolean isLocal(String fileId) {

		List<LocalFile> localFiles = BackupService.getLocalFiles();

		int i=0;

		while(i<localFiles.size()) {
			LocalFile temp = localFiles.get(i);
			if(temp.getId().equals(fileId))
				return true;
			i++;
		}
		return false;
	}

	public static RemoteFile getRemote(String fileId) {

		HashMap<String, RemoteFile> remoteFiles=BackupService.getRemoteFiles();
		return remoteFiles.get(fileId);
	}
	
	public static boolean isRemote(String fileId, int chunkNo) {

		HashMap<String, RemoteFile> remoteFiles=BackupService.getRemoteFiles();
		RemoteFile file=remoteFiles.get(fileId);
		
		if(file == null)
			return false;
		
		return (file.getChunk(chunkNo) != null);
	}
	
	public static LocalFile getLocal(String fileId) {

		List<LocalFile> localFiles = BackupService.getLocalFiles();
		int i=0;

		while(i<localFiles.size()) {
			LocalFile temp = localFiles.get(i);
			if(temp.getId().equals(fileId))
				return temp;
			i++;
		}
		return null;
	}
}
