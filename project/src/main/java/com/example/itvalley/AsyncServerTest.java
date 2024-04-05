package com.example.itvalley;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AsyncServerTest {
    public static void main(String[] args) {
        String[] serverFiles = {"CentralServer", "EditingDepartmentServer", "ProcessingCenterServer", "AccountsDepartmentServer", "MarketingDepartmentServer", "ClientsAsyncServer"};
        
        try {
            // Get the current working directory
            String currentDirectory = System.getProperty("user.dir");

            // Loop through each server file and start them sequentially
            for (String serverFile : serverFiles) {
                // Create a process builder for the server 
                Path classpathFilePath = Paths.get("classpath.txt");
                String classpath = new String(Files.readAllBytes(classpathFilePath), StandardCharsets.UTF_8);

                ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "target/project-1.0-SNAPSHOT.jar", "com.example.itvalley." + serverFile);
                                
                // Set the working directory to the current directory
                processBuilder.directory(new File(currentDirectory));

                // Redirect standard output and error to Java program's standard output and error
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                
                // Print current directory before starting each process
                System.out.println("Current directory: " + currentDirectory);

                // Start the process
                Process process = processBuilder.start();
                
                // Wait for the process to complete
                int exitCode = process.waitFor();
                
                // Print exit code of the process
                System.out.println(serverFile + " execution completed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
