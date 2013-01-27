package main;

import permutations.PermutationFactory;
import permutations.PermutationType;
import keyEvolver.KeyEvolver;
import roundFunction.DESRoundFunction;
import roundFunction.RoundFunction;
import roundFunction.sBoxes.SBoxFactory;

public class Encrypter {
	private RoundFunction _f;
	private PermutationFactory _pf;
	private Mapper _mapper;
	public Encrypter(RoundFunction f){
		_f = f;
		_pf = PermutationFactory.getInstance();
		_mapper = Mapper.getInstance();
	}
	public long encrypt(long block, long key, int rounds){
		KeyEvolver keyEvolver = new KeyEvolver();
		long roundBlock = initialPermutation(block);
		long roundKey = key;
		for(int round =0; round < rounds; round++){
			roundKey = keyEvolver.evolveKey(roundKey, 56);
			roundBlock = oneRound(roundBlock,roundKey);
		}
		roundBlock = swapHalves(roundBlock);
		return inverseInitialPermutation(roundBlock);
	}
	public long decrypt(long block, long key, int rounds){
		KeyEvolver keyEvolver = new KeyEvolver();
		long[] keySet = keyEvolver.getKeySet(key, 56);
		int keySetSize = rounds;
		long roundBlock = initialPermutation(block);
		for(int round = 0; round < rounds; round++){
			roundBlock = oneRound(roundBlock,keySet[(keySetSize-1)-round]);
		}
		return inverseInitialPermutation(swapHalves(roundBlock));
	}
	private long oneRound(long block, long key){
		long[] splitBlock = splitBlock(block);
			//L(i) = R(i-1)
		long newLeft = splitBlock[0] << 32;
			//R(i) = L(i-1) XOR f(key, R(i-1)
		long right = _f.apply(splitBlock[0], key);
		long newRight = splitBlock[1] ^ right;
			//merge left and right
		return newLeft + newRight;
	}
	private long[] splitBlock(long block){
		long base = 0b1l;
		long mask = (base << 32)-1;
		long[] splitBlock = new long[2];
			//right portion of the number
		splitBlock[0]= (mask & block);
			//left portion of number
		splitBlock[1] = (block - splitBlock[0])>>32;
		return splitBlock;
	}
	private long swapHalves(long block){
		long right = block & ((0b1l << 32)-1);
		long left = block - right;
		long newLeft = (right << 32);
		long newRight = left >> 32;
		return newLeft + newRight;
	}
	private long initialPermutation(long block){
		byte[] initPermMapping = _pf.getPermutation(PermutationType.INITIAL);
		return _mapper.map(block,initPermMapping);
	}
	private long inverseInitialPermutation(long block){
		byte[] invInitPermMapping=
				_pf.getPermutation(PermutationType.INVERSE_INITIAL);
		return _mapper.map(block,invInitPermMapping);
	}
	
	public static void main(String[] args){
		PermutationFactory pf = PermutationFactory.getInstance();
		Encrypter someEncrypter = new Encrypter(new DESRoundFunction(
						pf.getPermutation(PermutationType.EXPANSION),
						SBoxFactory.getInstance().getSBoxes(),
						pf.getPermutation(PermutationType.AFTER_SBOX)));
		
		long encrypted = someEncrypter.encrypt(0b1111l, 0b100101010101010101l,1);
		long decrypted = someEncrypter.decrypt(encrypted, 0b100101010101010101l,1);
		System.out.println(decrypted);
	}
}
