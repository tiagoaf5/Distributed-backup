import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/*
 * Classe que representa um ficheiro local que entrará 
 * no sistema de backup
 */
public class MyFile {

	private int replication;
	private String name;
	private Path path;
	private File systemFile;
	
	public MyFile(String name, int replication) {
		
		systemFile=new File(name);
		if(!systemFile.exists()) {
			System.out.println("ERRO: o ficheiro " + name + " não existe.");
			return;
		}
		setName(name);
		setReplication(replication);
		path=Paths.get(name);
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
	
	public byte[] getId() {
		//TODO: calcular devidamente os ids
		
		//>http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
		//>http://stackoverflow.com/questions/4793387/utf-16-encoding-in-java-versus-c-sharp
		//>http://beginnersbook.com/2013/12/java-string-getbytes-method-example/
		
		try {
			String res=getInfo();
			return applySHA256(res);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return null;
	}

	private byte[] applySHA256(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(key.getBytes("US-ASCII"));
		
		byte[] tmp = md.digest();
		//System.out.println(tmp);
		
		for(int i = 0; i < tmp.length; i++)
			System.out.print(String.format("%x",tmp[i]));
		
		return tmp;
	}

	private String getInfo() throws IOException {
		
		String res="";
		long last=systemFile.lastModified();
		UserPrincipal owner=Files.getOwner(path);
		int random=getRandom();
		
		res+=this.path + " " + Long.toString(last) + " " + owner.getName();// + " " + Integer.toString(random);

		System.out.println(res);

		return res;
	}
	
	private int getRandom() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt();
	}
}
