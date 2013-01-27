package roundFunction.sBoxes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class SBoxFactory {
	private static SBoxFactory instance=null;
	private BufferedReader byteReader = null;
	private HashMap<Integer,SBox> cache;
	public SBoxFactory(String fileName){
		cache = new HashMap<Integer,SBox>();
		constructSBoxes(fileName);
	}
	public static SBoxFactory getInstance(){
		if(instance==null){
			instance=new SBoxFactory("SBOXSetUp");
		}
		return instance;
	}
	public SBox getSBox(int sBoxID){
		return cache.get(sBoxID % cache.size());
	}
	public SBox[] getSBoxes(){
		SBox[] arr = new SBox[cache.size()];
		return cache.values().toArray(arr);
	}
	private void constructSBoxes(String fileName){
		try{
			byteReader = new BufferedReader(new FileReader(fileName));
			int counter = 0;
			while(byteReader.ready()){
				byte[][] mapping = readSBox(0,4);
				cache.put(counter++,new SBox(mapping));
			}
		}
		catch(Exception eIO){
			System.out.println("Error Reading SBox Configuration");
		}	
		finally{
			close();
		}
	}
	
	private void close(){
		try{
			if(byteReader!=null){
				byteReader.close();
			}
		}
		catch(IOException eIO2){
			System.out.println("Failed To Close SBox Configuration File");
		}
	}
	
	private byte[][] readSBox(int width,int height) throws IOException{
		byte[][] buffer = new byte[height][];
		for(int row=0; row < height; row++){
			String[] currentLine = parseBytes(byteReader);
			buffer[row]=readRow(currentLine);
		}
		return buffer;
	}
	
	private String[] parseBytes(BufferedReader byteReader) throws IOException{
		String byteLine =byteReader.readLine();
		return byteLine.split(" ");
	}
	
	private byte[] readRow(String[] bytes){
		byte[] buffer = new byte[bytes.length];
		for(int col=0; col < bytes.length; col++){
			buffer[col]=Byte.parseByte(bytes[col]);
		}
		return buffer;
	}
	
	public static void main(String[] args){
		SBoxFactory.getInstance();
		
	}
}
