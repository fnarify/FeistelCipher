// Bao-lim Smith
// 43277047
package Ciphers;

/**
 * Implements the S-box functionality required for the encryption within
 * Comp343Cipher. 
 * 
 * @author bao
 */
public class SBox 
{
	private int[] sBox;

	/**
	 * Constructor, that initialises the required S-box. 
	 */
	public SBox()
	{
		sBox = new int[16];

		// Creates the output column. 
		sBox[0] = 0;
		sBox[1] = 1;
		sBox[2] = 11;
		sBox[3] = 13;
		sBox[4] = 9;
		sBox[5] = 14;
		sBox[6] = 6;
		sBox[7] = 7;
		sBox[8] = 12;
		sBox[9] = 5;
		sBox[10] = 8;
		sBox[11] = 3;
		sBox[12] = 15;
		sBox[13] = 2;
		sBox[14] = 4;
		sBox[15] = 10;
	}

	/**
	 * @param in
	 * @return the sBox output
	 */
	public int getSBoxOutput(int in)
	{
		return sBox[in];
	}
}
