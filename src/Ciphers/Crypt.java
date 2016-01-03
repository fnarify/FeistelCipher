// Bao-lim Smith
// 43277047
package Ciphers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Creates a cipher from the class Comp343Cipher that encrypts and decrypts 
 * 16-bit blocks of data via a key. 
 * 
 * @author bao
 */
public class Crypt 
{
	static Comp343Cipher cipher = new Comp343Cipher();

	/**
	 * Constructor.
	 */
	public Crypt()
	{

	}

	public static void main(String[] args) throws IOException
	{		
		String readText = "";
		String writeText = "";
		byte[] key = new byte[2];
		byte[] block;
		boolean encrypt;
		FileInputStream in = null;
		FileOutputStream out = null;

		// Converts the hexadecimal String passed in the main args to a byte array.
		// Assuming the hex file is 6 characters long indicating a hex of 0xXXXX.
		// E.g. 0xDCEF has key[0] = D + C, key[1] = E + F.
		String s = args[2];
		key[0] = (byte) (Character.digit(s.charAt(2), 16) << 4 + Character.digit(s.charAt(3), 16));
		key[1] = (byte) (Character.digit(s.charAt(4), 16) << 4 + Character.digit(s.charAt(5), 16));

		if (args[3].equals("E"))
		{
			encrypt = true;

			readText = args[0];
			writeText = args[1];
		}
		else
		{
			encrypt = false;

			// Instead read the cipher text, write to the plain text. 
			String temp = readText;
			readText = writeText;
			writeText = temp;		
		}

		// Read the bytes from the input file.
		try 
		{
			in = new FileInputStream(readText);

			// Always assume even number of bytes. 
			if (in.available()%2 == 0)
				block = new byte[in.available()];
			else 
				block = new byte[in.available() + 1];

			// Reads a byte (8 bits) at a time. 
			in.read();
		} 
		finally	
		{
			if (in != null)
				in.close();
		}

		// Perform required action on the bytes by the cipher. 
		cipher.loadKey(key);

		if (encrypt)
			block = cipher.encrypt(block);
		else			
			block = cipher.decrypt(block);

		// Write the bytes that have been encrypted/decrypted to the output file.
		try 
		{
			out = new FileOutputStream(writeText);
			out.write(block);
		} 
		finally	
		{
			if (out != null)
				out.close();
		}
	}

	/**
	 * Translates a 4 character long hexadecimal string e.g 0xACCD into 2 
	 * 8 bit array elements. 
	 * @param s
	 * @param block
	 * @return the bits of the hex string
	 */
	public byte[] hexToByte(String s, byte[] block)
	{
		block[0] = (byte) (s.charAt(2) + s.charAt(3));
		block[1] = (byte) (s.charAt(4) + s.charAt(5));
		return block;
	}
}
