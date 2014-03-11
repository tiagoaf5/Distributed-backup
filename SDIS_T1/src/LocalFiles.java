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
	 * nomeFicheiro1 replica�ao1
	 * nomeFicheiro2 replica�ao2
	 * ...
	 * nomeFicheiroN replica�aoN
	 */
	private void readFile() {
	
		FileInputStream fileStream;
		
		try {
			fileStream = new FileInputStream(FILENAME);
			DataInputStream input = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String line;
		
			line=reader.readLine();
			setDiskSpace(Float.parseFloat(line));
			System.out.println(getDiskSpace());
			
			while ((line = reader.readLine()) != null)   {
				
				String[] splits = line.split(" ");
				MyFile newFile=new MyFile(splits[0], splits[1]);
				files.add(newFile);
				
				System.out.println(line);
				//System.out.println(splits[0] + ", " + splits[1]);
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
