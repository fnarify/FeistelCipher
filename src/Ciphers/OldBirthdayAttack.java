package Ciphers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.HashMultimap;

public class OldBirthdayAttack
{
	/**
	 * Original birthday attack, with just hashFunction() in Comp343Cipher. 
	 */
	public static void main(String[] args) 
	{
		/*
		Comp343Cipher cipher = new Comp343Cipher();
		Random rand = new Random(); 
		HashMultimap<byte[], byte[]> hashedMessages = HashMultimap.create();
		Set<byte[]> set = new HashSet<byte[]>();
		ArrayList<byte[]> list = new ArrayList<byte[]>();

		int numRounds = 100000;
		int range = 0xFF + 1; 
		byte[] message = new byte[4];
		byte[][] hashes = new byte[2][numRounds];
		byte[] tempHash = new byte[2];

		// Create the Hash Multimap such that each hash (value), it has
		// associated message(s) (key). 
		for (int i = 0; i < numRounds; i++)
		{
			for (int j = 0; j < message.length; j++)
				message[j] = (byte) rand.nextInt(range);

			tempHash = cipher.hashMessage(message);
			hashes[0][i] = tempHash[0];
			hashes[1][i] = tempHash[1];

			hashedMessages.put(tempHash, message);
		}

		// Search the array to determine for collisions by comparing to the Hash Map. 
		for (int k = 0; k < numRounds; k++)
		{
			tempHash[0] = hashes[0][k];
			tempHash[1] = hashes[1][k];

			set = hashedMessages.get(tempHash);

			// Only do if there is a collision. 
			if (set.size() > 1)
			{
				list.addAll(set);

				// Print all collisions for the one hash. 
				for (int a = 0; a < list.size(); a++)
				{
					message = list.get(a);
					printCollision(message);
				}

				System.out.println("collision");

				list.clear();
			}
		}
		*/

		Comp343Cipher cipher = new Comp343Cipher();
		Random rand = new Random();
		// First input. 
		byte key1[] = new byte[2];
		byte message1[] = new byte[2];
		// Second input. 
		byte key2[] = new byte[2];
		byte message2[] = new byte[2];
		// Array of original values. 
		byte values[] = new byte[8];
		int range = 0xFF + 1;
		int numRounds = 100000;

		// Choose two random values for the key and block.
		// Then check if these two values are the same. 
		for (int i = 0; i < numRounds; i++)
		{
			for (int j = 0; j < values.length; j++)
				values[j] = (byte) rand.nextInt(range);

			// Assign the required values as shown. 
			key1[0] = values[0];
			key1[1] = values[1];
			message1[0] = values[2];
			message1[1] = values[3];
			key2[0] = values[4];
			key2[1] = values[5];
			message2[0] = values[6];
			message2[1] = values[7];

			cipher.loadKey(key1);
			message1 = cipher.encrypt(message1);
			cipher.loadKey(key2);
			message2 = cipher.encrypt(message2);

			// Print out any collisions if found. 
			if (message1[0] == message2[0] && message1[1] == message2[1])
				printCollisions(values);
		}
	}
	
	public static void printCollisions(byte[] values)
	{
		String chain1 = Integer.toHexString(((values[0] << 8) & values[1]) & 0xFFFF);
		String msg1 = Integer.toHexString(((values[2] << 8) & values[3]) & 0xFFFF);	
		String chain2 = Integer.toHexString(((values[4] << 8) & values[5]) & 0xFFFF);
		String msg2 = Integer.toHexString(((values[6] << 8) & values[7]) & 0xFFFF);

		System.out.println(msg1 + "  " + chain1 + "\n" + msg2 + "  " + chain2 + "\nCollision");
	}

	/**
	 * Print the formatted values. 
	 * @param values
	 */
	public static void printCollision(byte[] values)
	{
		String msg = Integer.toHexString(((values[0] << 8) & values[1]) & 0xFFFF);
		String chain = Integer.toHexString(((values[2] << 8) & values[3]) & 0xFFFF);	

		System.out.println(msg + "  " + chain);
	}

	/**
	 * Uses the Comp343Cipher compression function to find collisions.
	 */
	public static void birthdayAttackCompression()
	{
		Comp343Cipher cipher = new Comp343Cipher();
		Random rand = new Random();
		HashMultimap<byte[], byte[]> hashedMessages = HashMultimap.create();
		ArrayList<byte []> messages = new ArrayList<byte[]>();

		int length = rand.nextInt(100) + 1;
		int range = 0xFF + 1; 
		int numRounds = 10000;
		byte[] chain = new byte[2];
		byte[] message = new byte[2];
		byte[] digest = new byte[2];
		byte[][] hashes = new byte[2][numRounds];
		byte[] values = new byte[4];

		for (int i = 0; i < numRounds; i++)
		{
			chain[0] = (byte) rand.nextInt(range);
			chain[1] = (byte) rand.nextInt(range);		
			
			// Build up the message of arbitrary length. 	
			for (int j = 0; j < length; j++)
			{
				message[0] = (byte) rand.nextInt(range);
				message[1] = (byte) rand.nextInt(range);
				messages.add(message);
			}
			
			// Hash the message. 
			digest = cipher.compressFunc(messages, chain);
			
			hashes[0][i] = digest[0];
			hashes[1][i] = digest[1];
			
			// The starting 32 bit message plus chaining variable. 
			values[0] = messages.get(0)[0];
			values[1] = messages.get(0)[1];
			values[2] = chain[1];
			values[3] = chain[2];
			
			hashedMessages.put(digest, values);
		}
	}
}
