package com.clypt.clypt_backend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

	private static final String AES = "AES";
	private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
	private static final int IV_SIZE = 16;
	private static final int KEY_SIZE = 16;

	/**
	 * Encrypts the given data using AES encryption in CBC mode with PKCS5Padding.
	 *
	 * A random IV (initialization vector) is generated and added to the beginning
	 * of the encrypted result to ensure secure encryption.
	 *
	 * @param data      Data to be encrypted, provided as a byte array.
	 * @param secretKey Secret key used for encryption, as a byte array.
	 * @return A byte array containing the IV followed by the encrypted data.
	 * @throws Exception If an error occurs during the encryption process.
	 */

	public static byte[] encrypt(byte data[], byte secretKey[]) throws Exception {
		// generate random Initialisation Vector
		byte iv[] = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		// initialize cipher with AES/CBC/PKCS5Padding.
		Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		// encrypt data
		byte encryptedData[] = cipher.doFinal(data);

		// combine IV and encrypted data into a single byte array
		byte[] ivAndEncryptedData = new byte[IV_SIZE + encryptedData.length];
		
		// Copy IV
		for (int i = 0; i < iv.length; i++)
			ivAndEncryptedData[i] = iv[i];

		// Copy encrypted data
		for (int i = 0; i < encryptedData.length; i++)
			ivAndEncryptedData[iv.length + i] = encryptedData[i];

		return ivAndEncryptedData;

	}
	
	
    /**
     * Decrypts the given byte array (which contains both IV and ciphertext) using AES/CBC/PKCS5Padding.
     *
     * @param encryptedData The byte array containing IV and ciphertext.
     * @param secretKey     The decryption key (as byte array).
     * @return The decrypted data (plaintext).
     * @throws Exception If an error occurs during decryption.
     */
	public static byte[] decrypt(byte[] encryptedData, byte[] secretKey) throws Exception {
	    // Extract IV using loop
	    byte[] iv = new byte[IV_SIZE];
	    for (int i = 0; i < IV_SIZE; i++) {
	        iv[i] = encryptedData[i];
	    }

	    // Extract ciphertext using loop
	    int cipherLength = encryptedData.length - IV_SIZE;
	    byte[] ciphertext = new byte[cipherLength];
	    for (int i = 0; i < cipherLength; i++) {
	        ciphertext[i] = encryptedData[i + IV_SIZE];
	    }

	    // Initialize Cipher with AES/CBC/PKCS5Padding
	    Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
	    SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
	    IvParameterSpec ivSpec = new IvParameterSpec(iv);
	    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

	    // Decrypt the ciphertext
	    return cipher.doFinal(ciphertext);
	}
	
	
	/**
	 * converts the unique code into a 128-bit AES encryption key.
	 *
	 * @param uniqueCode The code to convert into a key.
	 * @return The AES key as bytes.
	 * @throws Exception If something goes wrong while creating the key.
	 */

	
	public static byte[] generateKeyFromUniqueCode(String uniqueCode) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(uniqueCode.getBytes()); //SHA-256 gives message digest of size 32bytes 

        byte[] aesKey = new byte[KEY_SIZE]; 
        for(int i = 0; i < KEY_SIZE; i++) //Taking first 16bytes of messageDigest
        	aesKey[i] = key[i];

        return aesKey;
    }

}
