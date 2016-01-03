// Bao-lim Smith
// 43277047
package Ciphers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

/**
 * Implements a birthday attack on the cipher to find any collisions. 
 * 
 * @author bao
 */
public class BirthdayAttack 
{
	public static void main(String[] args) throws FileNotFoundException
	{
		Comp343Cipher cipher = new Comp343Cipher();
		// Use randomised inputs as the Birthday Attack guarantees a good 
		// percentage chance of finding a collision. 
		Random rand = new Random();
		// Output file for the collisions. 
		File file = new File("output.txt");
		FileOutputStream outputStream = new FileOutputStream(file);
		PrintStream printStream = new PrintStream(outputStream);
		// Redirects the System.out to print to a file output. 
		System.setOut(printStream);
		
		// The length is directly proportional to the amount of hashes taken. 
		int length = rand.nextInt(2000) + 1;
		byte[] messages = new byte[2*length]; // Length amount of messages (2 bytes ec). 
		byte[] chains = new byte[2*length]; // Chaining values. 
		byte[] hashes = new byte[2*length];
		
		byte[] message = new byte[2];
		byte[] currentIV = new byte[2];
		byte[] currentKey = new byte[2];

		// Build the array of messages. 
		rand.nextBytes(messages);
		
		// Create the IV. 
		rand.nextBytes(currentIV);
		chains[0] = currentIV[0];
		chains[1] = currentIV[1];
		
		cipher.loadKey(currentIV);

		// Compression function. 
		for (int j = 0; j < messages.length; j += 2)
		{
			message[0] = messages[j];
			message[1] = messages[j + 1];
			
			currentIV = cipher.encrypt(message);

			// Only add chaining values up to the last encryption's key. 
			if (j < messages.length - 2)
			{
				// Adds at position j + 2.
				chains[j + 2] = currentIV[0];
				chains[j + 3] = currentIV[1];
				
				// To prevent currentIV being deleted on the second run of this loop. 
				currentKey[0] = currentIV[0];
				currentKey[1] = currentIV[1];

				cipher.loadKey(currentKey);
			}

			// Adds at j. 
			hashes[j] = currentIV[0];
			hashes[j + 1] = currentIV[1];
		}

		// Check for collisions. 
		for (int a = 0; a < hashes.length; a += 2)
		{
			currentIV[0] = hashes[a];
			currentIV[1] = hashes[a + 1];

			for (int b = a + 2; b < hashes.length; b += 2)
			{
				if (currentIV[0] == hashes[b] && currentIV[1] == hashes[b + 1]) // Collision found. 
				{
					byte[] values = {messages[a], messages[a + 1], chains[a], chains[a + 1],
							         messages[b], messages[b + 1], chains[b], chains[b + 1]};
					printCollisions(values); 
				}
			}
		}	
	}
	
	/**
	 * Print collision of two 32-bit messages with the first four bytes the first message
	 * the second four bytes the next message. 
	 * 
	 * @param values
	 */
	public static void printCollisions(byte[] values)
	{
		String msg1 = Integer.toHexString(((values[0] << 8) & values[1]) & 0xFFFF);
		String chain1 = Integer.toHexString(((values[2] << 8) & values[3]) & 0xFFFF);	
		String msg2 = Integer.toHexString(((values[4] << 8) & values[5]) & 0xFFFF);
		String chain2 = Integer.toHexString(((values[6] << 8) & values[7]) & 0xFFFF);

		System.out.println(msg1 + "  " + chain1);
		System.out.println(msg2 + "  " + chain2);
		System.out.println("Collision");
		System.out.println();
	}

	/**
	 * Print collisions given each value individually as a two byte variables. 
	 * 
	 * @param msg1
	 * @param msg2
	 * @param chain1
	 * @param chain2
	 */
	public static void printCollisions(byte[] msg1, byte[] msg2, byte[] chain1, byte[] chain2)
	{
		System.out.println(Integer.toHexString(((msg1[0] << 8) & msg1[1]) & 0xFFFF) + "  ");
		System.out.print(Integer.toHexString(((chain1[0] << 8) & chain1[1]) & 0xFFFF));
		System.out.println(Integer.toHexString(((msg2[0] << 8) & msg2[1]) & 0xFFFF) + "  ");
		System.out.print(Integer.toHexString(((chain2[0] << 8) & chain2[1]) & 0xFFFF));
		System.out.println("Collision");
	}
}
