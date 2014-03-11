import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe respons�vel pela leitura e armazenamento da informa��o relativas aos
 * ficheiros locais que ir�o entrar no sistema de backup.
 */
public class LocalFiles {

	private static final String FILENAME = "files.txt";
	private float diskSpace; //em kBytes
	private List<MyFile> files; 

	public LocalFiles() {
		
		files=new ArrayList<MyFile>();
		readFile();
	}

	public float getDiskSpace() {
		return diskSpace;
	}

	public void setDiskSpace(float diskSpace) {
		this.diskSpace = diskSpace;
	}

	/*
	 * Conteudo Ficheiro files.txt:
	 * espa�oDisco
	 * nomeFicheiro1 - replica�ao1
	 * nomeFicheiro2 - replica�ao2
	 * ...
	 * nomeFicheiroN - replica�aoN
	 */
	private void readFile() {
	
		FileInputStream fileStream;
		
		try {
			fileStream = new FileInputStream(FILENAME);
			DataInputStream input = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String line;
			for(int i=0; i<15; i++) {
				line = reader.readLine();
			}
			
			line=reader.readLine();
			setDiskSpace(Float.parseFloat(line));
			System.out.println(getDiskSpace());
			
			int j=0;
			String name="";
			while ((line = reader.readLine()) != null)   {
				
				if(j==0) {
					name=line;
					j++;
				}
				else {
					MyFile newFile=new MyFile(name, line);
					files.add(newFile);
					j=0;
				}
				//System.out.println(line);
				//System.out.println(name + ", " + line);
			}
			System.out.println("Numero de ficheiros lidos: " + files.size());
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: files.txt not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}