package main;

public class Mapper {
	private static Mapper instance = null;
	private final long BASE = 1;
	public static Mapper getInstance(){
		if(instance==null){
			return new Mapper();
		}
		return instance;
	}
	public Mapper(){
	}
	public long map(long block, byte[] mapping){
		long result=BASE;
		long answer = 0;
		for(int index=0;index<mapping.length;index++){
			result=block & (BASE << (mapping[index]-1));
			result = (result >> (mapping[index]-1));
			answer+=(result<<index);
		}
		return answer;
	}
	public static void main(String[] args){
		Mapper someMapper = new Mapper();
		byte[] mapping={6,5,1,3,4,2,0};
		long block = 0b1101001;
		System.out.println(someMapper.map(block, mapping));
	}
}
