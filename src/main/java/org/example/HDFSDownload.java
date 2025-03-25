package org.example;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HDFSDownload {
    private static final String KEY_FILE = "secret.key"; // Encryption key storage

    public static void downloadFile(String hdfsFilePath, String localFilePath) throws Exception {
        FileSystem fs = HDFSConfig.getFileSystem();
        Path path = new Path(hdfsFilePath);

        if (!fs.exists(path)) {
            throw new IOException("File not found in HDFS: " + hdfsFilePath);
        }

        // Read encrypted data from HDFS
        byte[] encryptedData;
        try (FSDataInputStream inputStream = fs.open(path)) {
            encryptedData = inputStream.readAllBytes();
        }

        // Load the encryption key
        File keyFile = new File(KEY_FILE);
        if (!keyFile.exists()) {
            throw new IOException("Encryption key file is missing: " + KEY_FILE);
        }
        SecretKey secretKey = EncryptionUtil.loadKey(KEY_FILE);
        System.out.println("Loaded encryption key for decryption.");

        // Decrypt the data using AES/CBC/PKCS5Padding
        byte[] decryptedData = EncryptionUtil.decrypt(encryptedData, secretKey);

        // Write decrypted data to local file
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            fos.write(decryptedData);
            System.out.println("✅ Decrypted file saved to: " + localFilePath);
        }

        fs.close();
        System.out.println("File downloaded and decrypted successfully!");
    }

    public static void main(String[] args) {
        try {
            String hdfsFile = "/user/hdfs/encrypted_input_file1.txt";
            String localFile = "decrypted_input_file.txt";

            downloadFile(hdfsFile, localFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
