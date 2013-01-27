package permutations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class PermutationFactory {
	private static PermutationFactory instance=null;
	private HashMap<PermutationType,byte[]> cache;
	private BufferedReader someReader = null;
	
	public PermutationFactory(){
		cache = new HashMap<PermutationType,byte[]>();
	}
	
	public static PermutationFactory getInstance(){
		if(instance==null){
			instance = new PermutationFactory();
		}
		return instance;
	}
	
	public byte[] getPermutation(PermutationType pType){
		try{
			if(cache.containsKey(pType)){
				return cache.get(pType);
			}
			open(pType.getFile());
			String inputRead = readFile();
			String[] tokenizedInput= tokenize(inputRead," ");
			close();
			return toBytes(tokenizedInput);
		}
		catch(IOException eIO){
			System.out.println(eIO.getMessage());
			return null;
		}
		catch(Exception e){
			System.out.println("Unable to read Permutation file");
			return null;
		}
	}
	
	private void open(File someFile) throws IOException{
		if(someReader!=null){
			close();
		}
		someReader=new BufferedReader(new FileReader(someFile));
	}
	
	private String readFile() throws IOException{
		String inputRead = "";
		while(someReader.ready()){
			inputRead+=someReader.readLine()+" ";
		}
		return inputRead;
	}
	
	private String[] tokenize(String inputRead,String delimeter)
			throws IOException{
		return inputRead.split(delimeter);
	}
	
	private byte[] toBytes(String[] toConvert){
		byte[] mapping = new byte[toConvert.length];
		for(int index=0;index<toConvert.length;index++){
			mapping[index]=Byte.valueOf(toConvert[index]);
		}
		return mapping;
	}
	
	private void close() throws IOException{
		someReader.close();
	}
	public static void main(String[] args){
		PermutationFactory f = PermutationFactory.getInstance();
		byte[] expansion = f.getPermutation(PermutationType.EXPANSION);
		for(byte b : expansion){
			System.out.print(b+" ");
		}
	}
}
