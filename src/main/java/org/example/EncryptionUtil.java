package org.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // Secure AES mode
    private static final String KEY_FILE = "secret.key";  // File to store the key
    private static final byte[] IV = new byte[16]; // Initialization Vector (should be random in production)

    // Generate a new AES key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    // Save the key securely in Base64 format
    public static void saveKey(SecretKey secretKey, String filePath) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(encodedKey.getBytes());
        }
    }

    // Load the key securely from Base64
    public static SecretKey loadKey(String filePath) throws IOException {
        File keyFile = new File(filePath);
        if (!keyFile.exists() || keyFile.length() == 0) {
            throw new IOException("Encryption key file is missing or empty: " + filePath);
        }

        byte[] encodedKey;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            encodedKey = fis.readAllBytes();
        }

        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Encrypt data using AES/CBC/PKCS5Padding
    public static byte[] encrypt(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec iv = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(data);
    }

    // Decrypt data using AES/CBC/PKCS5Padding
    public static byte[] decrypt(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec iv = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(encryptedData);
    }

    public static void main(String[] args) {
        try {
            // Load or generate key
            File keyFile = new File(KEY_FILE);
            SecretKey secretKey;
            if (!keyFile.exists()) {
                secretKey = generateKey();
                saveKey(secretKey, KEY_FILE);
                System.out.println("Generated and saved new key.");
            } else {
                secretKey = loadKey(KEY_FILE);
                System.out.println("Loaded existing key.");
            }

            // Sample text for encryption
            String originalText = "Hello, this is a secret!";
            byte[] encryptedData = encrypt(originalText.getBytes(), secretKey);
            System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(encryptedData));

            // Decrypt the data
            byte[] decryptedData = decrypt(encryptedData, secretKey);
            System.out.println("Decrypted: " + new String(decryptedData));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
