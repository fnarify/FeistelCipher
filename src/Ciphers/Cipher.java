package Ciphers;
/**
 * Generic cipher interface
 */

/**
 * @author les
 * @version $Revision: 1.1 $
 */
public interface Cipher
{
	/**
	 * Load a key into the Cipher, perform any key setup, etc.
	 * @param block
	 * @return true on success
	 */
	public boolean loadKey(byte[] key);

	/**
	 * Delete the key from the Cipher
	 * @return true on success
	 */
	public boolean deleteKey();

	/**
	 * Encrypt a block of plaintext
	 * @param block
	 * @return the block of ciphertext
	 */
	public byte[] encrypt(byte[] block);

	/**
	 * Decrypt a block of ciphertext
	 * @param block
	 * @return the block of plaintext
	 */
	public byte[] decrypt(byte[] block);
}
