// Bao-lim Smith
// 43277047
package Ciphers;

import java.util.ArrayList;

/**
 * Implements a block cipher based on the skeleton of Cipher.
 * 
 * @author bao
 */
public class Comp343Cipher implements Cipher 
{
	final int numRounds = 8; 

	private byte[] key;
	private int[] roundKey;

	// Input for the Feistel-type cipher.
	private int leftInput;
	private int rightInput;
	private int tempInput;

	// 4 bit inputs for sBox.
	private int s1;
	private int s2;
	private SBox sBox;

	/**
	 * Constructor.
	 */
	public Comp343Cipher()
	{
		// Get the required sBox. 
		sBox = new SBox(); 
	}

	/**
	 * Load a key into the Cipher, perform any key setup, etc.
	 * The key is assumed to be 16 bits in size, so it must have 
	 * array position 0 and 1 filled, as each position can only store 
	 * at most 8 bits.
	 * k[0] - high order
	 * k[1] - low order
	 * 
	 * @param block
	 * @return true on success
	 */
	@Override
	public boolean loadKey(byte[] key)
	{
		this.key = key;

		// Key scheduling algorithm.
		roundKey = new int[numRounds];

		roundKey[0] = this.key[0] & 0xFF; // High-order bytes.
		roundKey[1] = this.key[1] & 0xFF; // Low-order bytes.
		for (int i = 2; i < numRounds; i++)
			roundKey[i] = permutation(roundKey[i - 1], 3, true)^permutation(roundKey[i - 2], 5, true);

		return true; 
	}

	/**
	 * Delete the keys from the Cipher
	 * @return true on success
	 */
	@Override
	public boolean deleteKey() 
	{
	    for (int i = 0; i < key.length; i++)
			this.key[i] = 0;
		
		for (int j = 0; j < roundKey.length; j++)
			roundKey[j] = 0;

		return true;
	}

	/**
	 * Encrypt a block of plaintext.
	 * Due to Java only taking 8 bits for a byte variable, 
	 * we assume the input comes in pairs of 2 such that
	 * block[i] is the high order bits and block[i + 1] is the 
	 * low order bits. 
	 * 
	 * Both the encryption and decryption are invertible functions, 
	 * that act in the same way for both methods. 
	 * 
	 * @param block
	 * @return the block of ciphertext
	 */
	@Override
	public byte[] encrypt(byte[] block) 
	{
		for (int i = 0; i < block.length; i += 2)
		{
			leftInput = block[i] & 0xFF; // First 8 bits (high order).
			rightInput = block[i + 1] & 0xFF; // Last 8 bits (low). 		

			// Run the Fiestel-type cipher 8 times. 
			for (int j = 0; j < numRounds; j++)
			{
				// Xor the right input with the key value for this round. 
				tempInput = rightInput^roundKey[j];

				// Splits the right input again into 4 bit inputs required for the sBox, 	
				// and get the sBox'ed version of the output. 
				s1 = sBox.getSBoxOutput((tempInput >> 4) & 0xF); // High-order.
				s2 = sBox.getSBoxOutput(tempInput & 0xF); // Low-order.

				// Recreate the sBox input. 
				tempInput = (s1 << 4) | s2; 
				// Perform the permutation operation on it. 
				tempInput = permutation(tempInput, 2, true);

				// Xor the left input with the altered right input. 
				leftInput = leftInput^tempInput;

				// Switch the inputs for the next round. 
				// However, do not perform this switch at the last round. 
				if (j != numRounds - 1)
				{
					tempInput = leftInput;
					leftInput = rightInput;
					rightInput = tempInput; 
				}
			}

			// The now encrypted block. 
			block[i] = (byte) leftInput;
			block[i + 1] = (byte) rightInput;
		}
	
		deleteKey();
		
		return block;
	}

	/**
	 * Decrypt a block of ciphertext. 
	 * The only difference between encryption and decryption is the order of the round keys. 
	 * 
	 * @param block
	 * @return the block of plaintext
	 */
	@Override
	public byte[] decrypt(byte[] block) 
	{
		// Invert the rounds keys. 
		roundKey = invertRoundKey(roundKey);
				
		// Re-encrypt the values with the now inverted round keys. 
		return encrypt(block);		
	}

	/**
	 * The permutation is the rotation of the bits such that:
	 * z7z6....z1z0 -> z5z4...z0z7z6 for encryption/decryption. 
	 * 
	 * Also utilised in the key rotation algorithm, for creating the 
	 * round keys, only with encrypt always true. 
	 * 
	 * Has an added parameter to perform a inverse permutation, just in
	 * case it is needed for anything. 
	 * 
	 * @param b
	 * @param encrypt
	 * @return the rotated bits
	 */
	public int permutation(int rotVal, int n, boolean rotLeft)
	{
		// We can perform these actions to get the required permutation, as
		// we know the values will only be defined for up to 8 bits. 
		int temp;

		if (rotLeft)
		{
			temp = (rotVal >> (8 - n)) & 0xFF;
			rotVal <<= n;
			rotVal = (rotVal | temp) & 0xFF;

			return rotVal;
		}
		else
		{
			temp = (rotVal << (8 - n)) & 0xFF;
			rotVal >>= n;
			rotVal = (rotVal | temp) & 0xFF;

			return rotVal;
		}
	}

	/**
	 * Swaps (inverts) the round key array needed for the decryption method. 
	 * 
	 * @param roundKey
	 * @return the inverted round key array
	 */
	public int[] invertRoundKey(int[] roundKey)
	{
		int temp;

		for (int i = 0; i < numRounds/2; i++)
		{
			temp = roundKey[i];
			roundKey[i] = roundKey[numRounds - i - 1];
			roundKey[numRounds - i - 1] = temp;
		}

		return roundKey;
	}
	
	/**
	 * Takes a 32-bit message and then hashes it using this block cipher class 
	 * to create a 16-bit output message. 
	 * 
	 * Assume any message passed is always 32-bit, so it is of size 4. 
	 * 
	 * @param message
	 * @return the hashed message
	 */
	public byte[] hashMessage(byte[] message)
	{
		byte[] block = {message[0], message[1]};
		// Chaining variable 
		byte[] key = {message[2], message[3]};
		
		this.loadKey(key);
		// Hash the message. 
		return this.encrypt(block);
	}
	
	/**
	 * takes in an arbitrary length message each of 16 bits and a 16 bit chaining value. 
	 * Then hashes these via a Rabin-type scheme. 
	 * 
	 * @param message
	 * @param h chaining value
	 * @return the digest
	 */
	public byte[] compressFunc(ArrayList<byte[]> message, byte[] h)
	{
		this.loadKey(h);
		
		for (int i = 0; i < message.size(); i++)
		{
			h = this.encrypt(message.get(i));

			if (i < message.size() - 1)
				this.loadKey(h);
		}
		
		return h;
	}
}
