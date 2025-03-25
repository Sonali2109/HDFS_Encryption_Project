package org.example;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HDFSUpload {
    private static final String KEY_FILE = "secret.key";

    public static void uploadFile(String localFilePath, String hdfsFilePath) throws Exception {
        FileSystem fs = HDFSConfig.getFileSystem();
        Path path = new Path(hdfsFilePath);

        if (fs.exists(path)) {
            throw new IOException("File already exists in HDFS: " + hdfsFilePath);
        }

        File file = new File(localFilePath);
        byte[] fileData;

        // Read local file data
        try (FileInputStream fis = new FileInputStream(file)) {
            fileData = fis.readAllBytes();
        }

        // Load or generate the encryption key
        File keyFile = new File(KEY_FILE);
        SecretKey secretKey;
        if (keyFile.exists()) {
            secretKey = EncryptionUtil.loadKey(KEY_FILE);
            System.out.println("Loaded existing encryption key.");
        } else {
            secretKey = EncryptionUtil.generateKey();
            EncryptionUtil.saveKey(secretKey, KEY_FILE);
            System.out.println("🆕 Generated and saved a new encryption key.");
        }

        // Encrypt the file data using AES/CBC/PKCS5Padding
        byte[] encryptedData = EncryptionUtil.encrypt(fileData, secretKey);

        // Upload encrypted data to HDFS
        try (FSDataOutputStream outputStream = fs.create(path, true)) {
            outputStream.write(encryptedData);
            System.out.println("Encrypted file uploaded successfully to HDFS!");
        }

        fs.close();
    }

    public static void main(String[] args) {
        try {
            String localFile = "/home/sonalichaudhari/Hadoop_File_Project/Hadoop_Project/src/input.txt";
            String hdfsFile = "/user/hdfs/encrypted_input_file1.txt";

            uploadFile(localFile, hdfsFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
