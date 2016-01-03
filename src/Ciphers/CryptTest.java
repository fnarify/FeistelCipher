// Bao-lim Smith
// 43277047
package Ciphers;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;


public class CryptTest
{		
	@Test
	public void basicTests()
	{
		Comp343Cipher cipher = new Comp343Cipher();

		int b1 = 15;
		int b2 = cipher.permutation(b1, 2, true);
		b1 <<= 2;
		assertEquals(b1, b2);
		b2 = cipher.permutation(b2, 2, false);
		assertEquals(b1 >> 2, b2);

		// Java only stores bytes in 8 bit intervals, any extra stored 
		// is stored as 1s.
		byte[] b = new byte[2];
		b[0] = (byte) 0xDC;
		b[1] = (byte) 0xEF;

		// Converting from hex to binary. 
		String s = "D";
		int temp = Character.digit(s.charAt(0), 16);
		assertEquals(temp, 13);	
	}

	@Test
	public void takingHighLowOrder()
	{
		int temp = 46322;
		int lowOrder = temp & 0xFF;
		int highOrder = temp >> 8 & 0xFF;

		assertEquals(highOrder, 180);
		assertEquals(lowOrder, 242);
		assertEquals(temp, lowOrder | highOrder << 8);
	}

	@Test
	public void testPermutation()
	{
		Comp343Cipher cipher = new Comp343Cipher();

		int temp = 182;
		assertEquals(cipher.permutation(temp, 2, true), 218);
		assertEquals(cipher.permutation(temp, 2, false), 173);

		assertFalse(cipher.permutation(temp, 8, true) == temp << 8);
	}

	@Test
	public void byteToInt()
	{
		byte b = (byte) 0xAD;
		int a = b & 0xFF;
		assertEquals(0xAD, a);
	}

	@Test
	public void testEncrypt()
	{
		Comp343Cipher cipher = new Comp343Cipher();

		byte[] key = new byte[2];
		key[0] = (byte) 0xDC;
		key[1] = (byte) 0xEF;
		byte[] block = new byte[2];
		block[0] = (byte) 0xAC;
		block[1] = (byte) 0xED;

		cipher.loadKey(key);
		block = cipher.encrypt(block);

		assertEquals(block[0], (byte) 0x5B);
		assertEquals(block[1], (byte) 0x0E);
	}

	@Test
	public void testDecrypt()
	{
		Comp343Cipher cipher = new Comp343Cipher();

		byte[] key = new byte[2];
		key[0] = (byte) 0xDC;
		key[1] = (byte) 0xEF;
		byte[] block = new byte[2];
		block[0] = (byte) 0x5B;
		block[1] = (byte) 0x0E;

		cipher.loadKey(key);
		block = cipher.decrypt(block);

		assertEquals(block[0], (byte) 0xAC);
		assertEquals(block[1], (byte) 0xED);
	}

	@Test
	public void testEncryptLarger()
	{
		Comp343Cipher cipher = new Comp343Cipher();

		byte[] key = new byte[2];
		key[0] = (byte) 0xDC;
		key[1] = (byte) 0xEF;
		byte[] block = new byte[4];
		block[0] = (byte) 0xAC;
		block[1] = (byte) 0xED;
		block[2] = (byte) 0xFE;
		block[3] = (byte) 0xCC;

		cipher.loadKey(key);
		block = cipher.encrypt(block);

		assertEquals(block[0], (byte) 0x5B);
		assertEquals(block[1], (byte) 0x0E);	
		assertEquals(block[2], (byte) 0xC9);
		assertEquals(block[3], (byte) 0x81);
	}

	@Test
	public void testBoth()
	{
		Comp343Cipher cipher = new Comp343Cipher();
		Random rand = new Random();
		int range = 0xFF + 1;
		int numRepeats = 1000000;
		byte[] key = new byte[2];
		byte[] block = new byte[2];
		byte b1;
		byte b2;
		
		for (int i = 0; i < numRepeats; i++)
		{		
			int a = rand.nextInt(range);
			int b = rand.nextInt(range);
			key[0] = (byte) a;
			key[1] = (byte) b;
			b1 = (byte) rand.nextInt(range);
			b2 = (byte) rand.nextInt(range);
			block[0] = b1;
			block[1] = b2;

			cipher.loadKey(key);
			block = cipher.encrypt(block);
			
			key[0] = (byte) a;
			key[1] = (byte) b;
			cipher.loadKey(key);
			block = cipher.decrypt(block);

			assertEquals(block[0], b1);
			assertEquals(block[1], b2);
		}
	}
}
