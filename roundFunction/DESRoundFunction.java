package roundFunction;

import permutations.PermutationFactory;
import permutations.PermutationType;
import main.Mapper;
import roundFunction.sBoxes.SBox;
import roundFunction.sBoxes.SBoxFactory;


public class DESRoundFunction implements RoundFunction{
	private final Mapper _mapper;
	private byte[] _expansionTable;
	private SBox[] _sBoxes;
	private byte[] _permutation;
	public DESRoundFunction(byte[] expansionTable,
							 SBox[] sBoxes,
							 byte[] permutation){
		_mapper = Mapper.getInstance();
		_expansionTable = expansionTable;
		_sBoxes = sBoxes;
		_permutation = permutation;
	}
	@Override
	public long apply(long block, long key) {
		long expandedBlock = _mapper.map(block, _expansionTable);
		long xORedBlock = expandedBlock ^ key;
		long substitutedBlock = performSubstitution(xORedBlock);
		long permutedBlock = _mapper.map(substitutedBlock, _permutation);
		return permutedBlock;
	}
	private long performSubstitution(long block){
		long mask = 63l;
		long output = 0;
		for(int byteIndex=0; byteIndex<8;byteIndex++){
				//get each 6bit block from the input block
			byte intermediate = (byte)((block & mask)>>(6 * byteIndex));
			output+=_sBoxes[byteIndex].substitute(intermediate)<<(4 * byteIndex);
			mask = mask << 6;
		}
		return output;
	}
	public static void main(String[] args){
		byte[] expansion = PermutationFactory.getInstance().getPermutation(
						PermutationType.EXPANSION);
		byte[] afterSBox = PermutationFactory.getInstance().getPermutation(
				PermutationType.AFTER_SBOX);
		DESRoundFunction drf = new DESRoundFunction(expansion,
				SBoxFactory.getInstance().getSBoxes(), afterSBox);
		long block = 0b100000110100000100101000010100000000111100100101l;
		long output= 0b01110110101010101000101000101000l;
		long block2 =0b1l;
		System.out.println("XX"+(drf.performSubstitution(block)-output));
		long key =0b00000000111111110000000011111111000000001111111100000000l;
		System.out.println(drf.apply(drf.apply(block2, key),key)-block2);
	}
	
}
