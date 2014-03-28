package Service;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Classe que representa um ficheiro local que entrar� 
 * no sistema de backup
 */
public class LocalFile extends MyFile{
	private String name;
	private Path path;
	private File systemFile;
	private FileInputStream fileStream;
	private int offset = -1; //next 64KBytes to read

	private int countDeleted=0; //count number of deleted messages received

	public LocalFile(String name, int replication) throws FileNotFoundException {

		systemFile=new File(name);
		if(!systemFile.exists()) {
			System.out.println("ERRO: o ficheiro " + name + " nao existe.");
			return;
		}
		setName(name);
		setReplication(replication);
		path=Paths.get(name);
		fileStream = new FileInputStream(systemFile);
		computeFileId();
	}

	public LocalFile(String name, String replication) throws FileNotFoundException {

		systemFile=new File(name);
		if(!systemFile.exists()) {
			System.out.println("ERRO: o ficheiro " + name + " nao existe.");
			return;
		}
		setName(name);
		setReplication(Integer.parseInt(replication));
		path=Paths.get(name);
		fileStream = new FileInputStream(systemFile);
		computeFileId();
	}

	public int getCountDeleted() {
		return countDeleted;
	}

	public void increaseCountDeleted() {
		this.countDeleted++;
	}

	public boolean hasReceivedAll() { //checks if a file has received all chunks asked to restore

		for(int i=0; i<getNumberChunks(); i++) {
			if(!getChunk(i).getRestored()) {
				return false;
			}
		}
		return true;
	}

	private void computeFileId() {
		try {
			String res=getInfo();
			fileId = Message.byteArrayToHexString(applySHA256(res));
			//System.out.println(fileId);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 

	}

	public String getName() {
		return name;
	}
	
	public String getFileName() {
		return path.getFileName().toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	private byte[] applySHA256(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(key.getBytes("US-ASCII"));

		byte[] tmp = md.digest();
		//System.out.println(tmp);
		//System.out.println(Message.byteArrayToHexString(tmp));

		return tmp;
	}

	private String getInfo() throws IOException {

		String res="";
		long last=systemFile.lastModified();
		UserPrincipal owner=Files.getOwner(path);
		int random=getRandom();

		res+=this.path + " " + Long.toString(last) + " " + owner.getName() + " " + readChars()
				+ " " + Integer.toString(random);

		//System.out.println(res);

		return res;
	}

	private String readChars() {

		try {
			fileStream = new FileInputStream(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DataInputStream input = new DataInputStream(fileStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		int c, count=0;
		char[] text = new char[5];
		String finalText = null;
		
		try {
			while((c = reader.read()) !=-1 && count<5) {
				char character = (char) c;
				text[count]=character;
				count++;
			}
			
			finalText=new String(text);
			//System.out.println("c -- " + finalText);
			
		} catch (IOException e) {
			System.out.println("ERRO: file " + name + " nao encontrado");
			e.printStackTrace();
		}
		return finalText;
	}

	private int getRandom() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt();
	}

	public byte[] nextChunk() throws IOException {
		long size = systemFile.length();
		int chunkSize;
		byte[] b;

		if(size / CHUNK_SIZE == offset+1)
			//b = new byte[(int) (size % CHUNK_SIZE)];
			chunkSize = (int) (size % CHUNK_SIZE);
		else if (size / CHUNK_SIZE > offset+1)
			//b = new byte[CHUNK_SIZE];
			chunkSize = CHUNK_SIZE;
		else {
			fileStream.close();
			return null;
		}

		b = new byte[chunkSize];

		int read = fileStream.read(b,0, chunkSize);

		System.out.println("Getting chunk " + b.length + " of " + size);

		if(read < CHUNK_SIZE)
			System.out.println("last chunk with size = " + read);

		offset++;

		chunks.put(offset, new Chunk(fileId, offset, replicationDeg));

		System.out.println("<" + Message.byteArrayToString(b).trim()+">");

		return b;
	}

	public byte[] getChunkData(int chunkNo) {
		byte[] b2 = super.getChunkData(chunkNo);


		if (b2 == null) {
			try {
				int offset = -1;
				long size = systemFile.length();
				int chunkSize;
				byte[] b = null;

				fileStream = new FileInputStream(systemFile);

				while(offset != chunkNo) {

					if(size / CHUNK_SIZE == offset+1)
						chunkSize = (int) (size % CHUNK_SIZE);
					else if (size / CHUNK_SIZE > offset+1)
						chunkSize = CHUNK_SIZE;
					else {
						break;
					}

					b = new byte[chunkSize];

					fileStream.read(b,0, chunkSize);

					offset++;

				}

				fileStream.close();
				return b;
			}
			catch (IOException e) {
				e.printStackTrace();
			}


		}

		return b2;
	}

	public int getOffset() {
		return offset;
	}

	public void selfRestore() {
		try {
			File f = new File("RestoredFiles/" + path.getFileName());

			if(!f.exists()) {
				f.createNewFile();
			} 

			FileOutputStream o = new FileOutputStream(f); 
			for(int i = 0; i < chunks.size(); i++) {
				o.write(chunks.get(i).getData());
			}

			o.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void unCheckReceivedAll() {
		for(int i=0; i<getNumberChunks(); i++) {
			getChunk(i).setRestored(false);
		}
	}

}
