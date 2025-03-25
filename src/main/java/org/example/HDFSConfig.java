package org.example;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import java.net.URI;

public class HDFSConfig {
    public static FileSystem getFileSystem() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        return FileSystem.get(URI.create("hdfs://localhost:9000"), conf);
    }

    public static void main(String[] args) throws IOException {
        FileSystem fs = getFileSystem();
        System.out.println("Connected to HDFS successfully!");
        fs.close();
    }
}
