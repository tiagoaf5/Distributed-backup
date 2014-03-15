import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Messages.*;

/*
 * Classe que representa um ficheiro local que entrará 
 * no sistema de backup
 */
public class MyFile {

	private int replication;
	private String name;
	private Path path;
	private File systemFile;
	private FileInputStream fileStream;
	private int offset = 0; //next 64KBytes to read
	private String fileId;
	
	private final int CHUNK_SIZE = 64000;
	
	public MyFile(String name, int replication) throws FileNotFoundException {
		
		systemFile=new File(name);
		if(!systemFile.exists()) {
			System.out.println("ERRO: o ficheiro " + name + " não existe.");
			return;
		}
		setName(name);
		setReplication(replication);
		path=Paths.get(name);
		fileStream = new FileInputStream(systemFile);
		computeFileId();
	}
	
	private void computeFileId() {
		try {
			String res=getInfo();
			fileId = Message.byteArrayToHexString(applySHA256(res));
			System.out.println(fileId);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		
	}

	public MyFile(String name, String replication) {
	
		systemFile=new File(name);
		if(!systemFile.exists()) {
			System.out.println("ERRO: o ficheiro " + name + " não existe.");
			return;
		}
		setName(name);
		setReplication(Integer.parseInt(replication));
		path=Paths.get(name);
	}

	public int getReplication() {
		return replication;
	}

	public void setReplication(int replication) {
		
		if(replication>9)
			this.replication=9;
		else
			this.replication = replication;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return fileId;

	}

	private byte[] applySHA256(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(key.getBytes("US-ASCII"));
		
		byte[] tmp = md.digest();
		//System.out.println(tmp);
		System.out.println(Message.byteArrayToHexString(tmp));
		
		return tmp;
	}

	private String getInfo() throws IOException {
		
		String res="";
		long last=systemFile.lastModified();
		UserPrincipal owner=Files.getOwner(path);
		int random=getRandom();
		
		res+=this.path + " " + Long.toString(last) + " " + owner.getName() + " " + Integer.toString(random);

		System.out.println(res);

		return res;
	}
	
	private int getRandom() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt();
	}
	
	public byte[] nextChunk() throws IOException {
		long size = systemFile.length();
		int chunkSize;
		byte[] b;
		
		if(size / CHUNK_SIZE == offset)
			//b = new byte[(int) (size % CHUNK_SIZE)];
			chunkSize = (int) (size % CHUNK_SIZE);
		else if (size / CHUNK_SIZE > offset)
			//b = new byte[CHUNK_SIZE];
			chunkSize = CHUNK_SIZE;
		else return null;
		
		b = new byte[chunkSize];
		
		int read = fileStream.read(b,0, chunkSize);
		
		System.out.println("-> " + b.length + "/" + size);
				
		if(read < CHUNK_SIZE)
			System.out.println("last chunk with size = " + read);
		
		offset++;
		
		//System.out.println("<" + Message.byteArrayToHexString(b).trim()+">");
			
		return b;
	}
}
