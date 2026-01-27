package com.sirv.api.examples;

import com.sirv.api.client.SirvClient;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File operations example for Sirv REST API Java SDK.
 *
 * <p>This example demonstrates:</p>
 * <ul>
 *   <li>Creating folders</li>
 *   <li>Uploading files</li>
 *   <li>Downloading files</li>
 *   <li>Copying and renaming files</li>
 *   <li>Deleting files and folders</li>
 * </ul>
 */
public class FileOperationsExample {

    public static void main(String[] args) {
        String clientId = System.getenv("SIRV_CLIENT_ID");
        String clientSecret = System.getenv("SIRV_CLIENT_SECRET");

        if (args.length >= 2) {
            clientId = args[0];
            clientSecret = args[1];
        }

        if (clientId == null || clientSecret == null) {
            System.out.println("Please provide credentials via environment variables or command line arguments");
            return;
        }

        try (SirvClient client = new SirvClient(clientId, clientSecret)) {
            client.connect();
            System.out.println("=== File Operations Example ===\n");

            // Create a test folder
            String testFolder = "/sdk-examples-java";
            System.out.println("Creating folder: " + testFolder);
            try {
                client.createFolder(testFolder);
                System.out.println("  Folder created: " + testFolder);
            } catch (SirvApiException e) {
                if (e.getStatusCode() == 409) {
                    System.out.println("  Folder already exists: " + testFolder);
                } else {
                    throw e;
                }
            }
            System.out.println();

            // Upload a file from bytes
            System.out.println("Uploading file from bytes...");
            String testContent = "Hello, Sirv! This is a test file uploaded from Java SDK.";
            String testFilePath = testFolder + "/test-file.txt";
            client.uploadFile(testFilePath, testContent.getBytes(StandardCharsets.UTF_8), "text/plain");
            System.out.println("  Uploaded: " + testFilePath);
            System.out.println();

            // Get file info
            System.out.println("Getting file info...");
            FileInfo info = client.getFileInfo(testFilePath);
            System.out.println("  File: " + testFilePath);
            System.out.println("  Size: " + info.getSize() + " bytes");
            System.out.println("  Content-Type: " + info.getContentType());
            System.out.println("  Modified: " + info.getMtime());
            System.out.println();

            // Download the file
            System.out.println("Downloading file...");
            byte[] content = client.downloadFile(testFilePath);
            System.out.println("  Downloaded " + content.length + " bytes");
            System.out.println("  Content: " + new String(content, StandardCharsets.UTF_8));
            System.out.println();

            // Download to a local file
            System.out.println("Downloading to local file...");
            Path tempFile = Files.createTempFile("sirv-test-", ".txt");
            client.downloadFileTo(testFilePath, tempFile);
            System.out.println("  Downloaded to: " + tempFile);
            System.out.println("  Content: " + Files.readString(tempFile));
            Files.delete(tempFile);
            System.out.println();

            // Copy the file
            System.out.println("Copying file...");
            String copyPath = testFolder + "/test-file-copy.txt";
            client.copyFile(testFilePath, copyPath);
            System.out.println("  Copied: " + testFilePath + " -> " + copyPath);
            System.out.println();

            // Rename the copy
            System.out.println("Renaming file...");
            String renamedPath = testFolder + "/test-file-renamed.txt";
            client.renameFile(copyPath, renamedPath);
            System.out.println("  Renamed: " + copyPath + " -> " + renamedPath);
            System.out.println();

            // List folder contents
            System.out.println("Listing folder contents...");
            FolderContents contents = client.readFolderContents(testFolder);
            for (FileInfo item : contents.getContents()) {
                System.out.println("  - " + item.getFilename());
            }
            System.out.println();

            // Delete the files
            System.out.println("Cleaning up test files...");
            client.deleteFile(testFilePath);
            System.out.println("  Deleted: " + testFilePath);
            client.deleteFile(renamedPath);
            System.out.println("  Deleted: " + renamedPath);
            System.out.println();

            // Delete the folder
            System.out.println("Deleting test folder...");
            try {
                client.deleteFile(testFolder);
                System.out.println("  Deleted: " + testFolder);
            } catch (SirvApiException e) {
                System.out.println("  Could not delete folder: " + e.getMessage());
            }
            System.out.println();

            System.out.println("File operations example completed!");

        } catch (SirvApiException e) {
            System.err.println("API Error: " + e.getMessage() + " (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
