package permutations;

import java.io.File;

public enum PermutationType implements Comparable<PermutationType>{
	EXPANSION("E"),
	INITIAL("I"),
	INVERSE_INITIAL("II"),
	AFTER_SBOX("P"),
	SHIFT_CONTROL("LS"),
	INITIAL_KEY_PERMUTATION("PC1"),
	ROUND_KEY_CONTRACTOR("PC2"),
	INITIAL_KEY_CONTRACTOR("KC");
	private File permFile;
	private PermutationType(String fileName){
		permFile = new File(fileName);
	}
	public File getFile(){
		return permFile;
	}
}
