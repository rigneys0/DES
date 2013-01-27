package keyEvolver;

import permutations.PermutationFactory;
import permutations.PermutationType;
import main.Mapper;

public class KeyEvolver {
	private byte[] _roundShifts;
	private byte[] _roundContraction;
	private int _round=0;
	public KeyEvolver(){
		_roundShifts=PermutationFactory.getInstance().getPermutation(
				PermutationType.SHIFT_CONTROL);
		_roundContraction=PermutationFactory.getInstance().getPermutation(
				PermutationType.ROUND_KEY_CONTRACTOR);
	}
	public long evolveKey(long key,int length){
			//split the key
		if(_round == 0){
			long permutedKey = initialPermutation(key);
			return initialContract(permutedKey);
		}
		long[] keyHalves=splitKey(key,length);
		long shiftedKey = evolve(keyHalves[1],keyHalves[0],length/2,_round);
		_round = (_round + 1) % _roundShifts.length;
		return roundContract(shiftedKey);
	}
	public long[] getKeySet(long key, int length){
		long[] keySet = new long[_roundShifts.length];
		keySet[0]=evolveKey(key,length);
		for(int round=1; round<_roundShifts.length; round++){
		//	System.out.println("KK" + keySet[round-1]);
			keySet[round] = evolveKey(keySet[round-1],length);
		}
		return keySet;
	}
	private long leftCircularShift(long key, long numOfBits, long keyLength){
		//achieve all ones bit mask
	long nBitMask= (0b1 << (numOfBits))-1;
		//shift the mask into place
	long bitsToWrapMask = nBitMask << (keyLength - numOfBits);
	
	long valueOfBitsToWrap = key & bitsToWrapMask;
		//move the portion that doesnt wrap round to the left
	long newKey = key & ~bitsToWrapMask;
	newKey = newKey << (numOfBits);
		//put leading bits back onto the right side
	newKey += valueOfBitsToWrap >> (keyLength-numOfBits);
	return newKey;
}
	private long[] splitKey(long key, int length){
		long halfLength = length /2;
		long[] keyHalves = new long[2];
		keyHalves[1] = ((0b1 << halfLength)-1) & key;
			//move right half of key to low order bits
		keyHalves[1] = (key - keyHalves[1])>>halfLength;
		return keyHalves;
	}
	private long initialPermutation(long key){
		byte[] mapping = PermutationFactory.getInstance().getPermutation(
				PermutationType.INITIAL_KEY_PERMUTATION);
		return Mapper.getInstance().map(key, mapping);
	}
	private long initialContract(long key){
		byte[] mapping = PermutationFactory.getInstance().getPermutation(
				PermutationType.INITIAL_KEY_CONTRACTOR);
		return Mapper.getInstance().map(key, mapping);
	}
	private long roundContract(long key){
		return Mapper.getInstance().map(key, _roundContraction);
	}
	private long evolve(long leftKey, long rightKey,
			long length, int round){
		leftKey = leftCircularShift(leftKey,_roundShifts[round],length);
		rightKey = leftCircularShift(rightKey,_roundShifts[round],length);
		return (leftKey << length)+rightKey;
	}
	public static void main(String[] args){
		long initKey=0b0000000100000001000000010000000100000001000000010000000100000111l;
		KeyEvolver ke = new KeyEvolver();
		System.out.println(ke.initialContract(initKey));
		//System.out.println(ke.evolveKey(key3, 8));
	}
}