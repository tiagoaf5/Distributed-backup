package Service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	//private static final String FILEBEGIN = "/*File format:";
	//private static final int COMMENTSIZE = 13;

	private static final String FOLDER_REMOTE_FILES = "RemoteFiles";
	private static final String FOLDER_RESTORED_FILES = "RestoredFiles";
	private static final String FOLDER_TMP = "tmp";

	private static final String REMOTE_ON_DISK = "RemoteFiles/remotes.txt";
	static private int diskSpace; //em Bytes
	static private int currentDiskSpace; //em Bytes
	

	static private ArrayList<LocalFile> localFiles; 
	static private HashMap<String, RemoteFile> remoteFiles;

	private static final String VERSION = "1.0";
	private static String versionEnhancement = "1.0";

	private static BackupStatusHandler backupHandler;

	public void initReceivingThreads() {
		
		mc.start();
		mdb.start();
		mdr.start();

		mdb.backupFiles(); 
	}

	/*public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}*/

	static public void handleChangedDiskSpace () {
		if(getAvailableDiskSpace() < 0) {

			ArrayList<Chunk> list = getChunksOrderedByRatio();

			int i = 0;

			while (getAvailableDiskSpace() < 0) {
				if (i >= list.size())
					break;
				System.out.println("Disk space: " + i);
				getRemote(list.get(i).getFileId()).delete(list.get(i).getChunkNo());
				mc.sayRemovedFile(list.get(i));
				i++;
			}
		}
	}

	private static ArrayList<Chunk> getChunksOrderedByRatio() {
		ArrayList<Chunk> list = new ArrayList<Chunk>();


		Iterator<Map.Entry<String,RemoteFile>> it = remoteFiles.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry<String,RemoteFile> pair = it.next();

			RemoteFile f = pair.getValue();

			ArrayList<Chunk> tmp = f.getChunks();

			for(int j = 0; j < tmp.size(); j++)
				list.add(tmp.get(j));
		}


		Collections.sort(list,null);
		return list;
	}

	public BackupService(String args[]) throws UnknownHostException {
		setAddresses(args);
		initialize();
	}


	public BackupService() {
		initialize();
	}

	public void setAddresses(String args[]) throws UnknownHostException {
		this.mcAddress =  InetAddress.getByName(args[0]);
		this.mcPort = Integer.parseInt(args[1]);
		this.mdbAddress = InetAddress.getByName(args[2]);
		this.mdbPort = Integer.parseInt(args[3]);
		this.mdrAddress = InetAddress.getByName(args[4]);
		this.mdrPort = Integer.parseInt(args[5]);
		
		openMulticastSessions();
	}

	private void initialize() {
		remoteFiles = new HashMap<String,RemoteFile>();
		localFiles = new ArrayList<LocalFile>();
		readFile();
		createFolders();
		currentDiskSpace = 0;

		backupHandler = new BackupStatusHandler();

		//openMulticastSessions();
	}
	
	public static void incrementDiskUsage(int bytes) {
		currentDiskSpace += bytes;
	}
	
	public static void decrementDiskUsage(int bytes) {
		currentDiskSpace -= bytes;
	}
	
	public static int getCurrentDiskSpace() {
		return currentDiskSpace;
	}


	private void openMulticastSessions() {
		try {
			mc = new MC(mcAddress, mcPort);
			mdb = new MDB(mdbAddress, mdbPort);
			mdr = new MDR(mdrAddress, mdrPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	

	static public String getVersion() {
		return VERSION;
	}

	static public void setVersionEnhancement(String version1) {
		versionEnhancement = version1;
	}
	
	static public String getVersionEnhancement() {
		return versionEnhancement;
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
				/*if(line.startsWith(FILEBEGIN)) {
					for(int i=0; i<COMMENTSIZE; i++) {
						line = reader.readLine();
					}
				}*/
				try {
					diskSpace = Integer.parseInt(line);
					System.out.println("Total disk space: " + diskSpace);
				} catch(Exception e) {
					System.out.println("ERRO files.txt mal definido: falta espaco maximo de disco.");
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
			System.out.println("files.txt nao encontrado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getDiskSpace() {
		return diskSpace;
	}

	public static int getAvailableDiskSpace() {
		//return (int) (diskSpace - folderSize(new File(FOLDER_REMOTE_FILES)));
		
		return diskSpace - currentDiskSpace;
	}

	public static ArrayList<LocalFile> getLocalFiles() {
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
	
	public static synchronized void  saveRemoteOnDisk() {
		
		File file=new File(REMOTE_ON_DISK); 

		try{ // if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator<Map.Entry<String,RemoteFile>> it = remoteFiles.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String,RemoteFile> pair = it.next();
				RemoteFile f = pair.getValue();
				ArrayList<Chunk> tmp = f.getChunks();

				for(int j = 0; j < tmp.size(); j++) {
					String toWrite=f.getId()+" "+tmp.get(j).getChunkNo()+" "+ tmp.get(j).getReplicationDeg()+"\n";
					bw.write(toWrite);
					//System.out.println("FileId: "+f.getId() + " ChunkNo: "+ tmp.get(j).getChunkNo() + " Grau:" + tmp.get(j).getReplicationDeg());
				}
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public static void deleteRemoteFile(String fileId) {
		getRemote(fileId).delete();
		remoteFiles.remove(fileId);
	}

	private void createFolders() {
		File dir1 = new File(FOLDER_REMOTE_FILES);
		File dir2 = new File(FOLDER_TMP);
		File dir3 = new File(FOLDER_RESTORED_FILES);

		if (!dir1.exists())
			dir1.mkdir();

		if (!dir2.exists())
			dir2.mkdir();

		if (!dir3.exists())
			dir3.mkdir();
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
	
	
	public static LocalFile getLocalByName(String name) {

		List<LocalFile> localFiles = BackupService.getLocalFiles();
		int i=0;

		while(i<localFiles.size()) {
			LocalFile temp = localFiles.get(i);
			System.out.println(temp.getName());
			if(temp.getFileName().equals(name))
				return temp;
			i++;
		}
		return null;
	}

	public static void updateLocalFile(String name) {

		LocalFile f=getLocalByName(name);
		String path=f.getFileName();
		int replication=f.getReplicationDeg();
		mc.askDeleteFile(f);
		
		try {
			LocalFile updated=new LocalFile(path, replication);
			localFiles.add(updated);
			mdb.backupFile(updated);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static LocalFile addLocalFile(String text, String text2) {
		try {
			LocalFile newFile = new LocalFile(text, text2);
			localFiles.add(newFile);
			return newFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void deleteLocalFile(LocalFile f) {
		localFiles.remove(f);
	}
	
	public static void setDiskSpace(int diskSpace) {
		BackupService.diskSpace = diskSpace;
	}

	public static BackupStatusHandler getBackupHandler() {
		return backupHandler;
	}
}
