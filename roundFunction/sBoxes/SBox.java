package roundFunction.sBoxes;

public class SBox {
	private final static byte RIGHT_CLIPPER = 0b00000_1;
	private final static byte LEFT_CLIPPER= 0b1_00000;
	private final static byte MIDDLE_CLIPPER= 0b0_1111_0;
	private byte[][] _mapping;
	public SBox(byte[][] mapping){
		_mapping = mapping;
	}
	public byte substitute(byte input){
		byte rowIndex=getRowIndex(input);
		byte colIndex=getColIndex(input);
		return _mapping[rowIndex][colIndex];
	}
	private byte getRowIndex(byte input){
		byte rightBit = (byte)(RIGHT_CLIPPER & input);
		byte leftBit = (byte)((LEFT_CLIPPER & input)>>4);
		return (byte)(leftBit+rightBit);
	}
	private byte getColIndex(byte input){
		byte middle= (byte)(MIDDLE_CLIPPER & input);
		middle = (byte)(middle >> 1);
		return middle;
	}
	public static void main(String[] args){
		SBox mySBox = SBoxFactory.getInstance().getSBox(0);
		byte input = 0b101111;
		System.out.println(mySBox.substitute(input));
	}
}
