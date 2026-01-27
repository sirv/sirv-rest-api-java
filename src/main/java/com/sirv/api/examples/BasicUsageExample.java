package com.sirv.api.examples;

import com.sirv.api.client.SirvClient;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic usage example for Sirv REST API Java SDK.
 *
 * <p>This example demonstrates:</p>
 * <ul>
 *   <li>Authentication</li>
 *   <li>Getting account information</li>
 *   <li>Getting storage usage</li>
 *   <li>Listing folder contents</li>
 *   <li>Searching for files</li>
 * </ul>
 */
public class BasicUsageExample {

    public static void main(String[] args) {
        // Get credentials from environment variables or command line
        String clientId = System.getenv("SIRV_CLIENT_ID");
        String clientSecret = System.getenv("SIRV_CLIENT_SECRET");

        if (args.length >= 2) {
            clientId = args[0];
            clientSecret = args[1];
        }

        if (clientId == null || clientSecret == null) {
            System.out.println("Please provide credentials via environment variables or command line arguments:");
            System.out.println("  Environment: SIRV_CLIENT_ID and SIRV_CLIENT_SECRET");
            System.out.println("  Command line: java BasicUsageExample <clientId> <clientSecret>");
            return;
        }

        // Use try-with-resources for automatic cleanup
        try (SirvClient client = new SirvClient(clientId, clientSecret)) {

            // Connect and get token info
            System.out.println("Connecting to Sirv API...");
            TokenResponse tokenInfo = client.connect();
            System.out.println("Connected! Token expires in " + tokenInfo.getExpiresIn() + " seconds");
            System.out.println("Scopes: " + String.join(", ", tokenInfo.getScope()));
            System.out.println();

            // Get account information
            System.out.println("Getting account information...");
            AccountInfo account = client.getAccountInfo();
            System.out.println("  CDN URL: " + account.getCdnURL());
            System.out.println("  Alias: " + account.getAlias());
            System.out.println("  Created: " + account.getDateCreated());
            System.out.println();

            // Get storage usage
            System.out.println("Getting storage usage...");
            StorageInfo storage = client.getStorageInfo();
            double usedGb = storage.getUsed() != null ? storage.getUsed() / 1e9 : 0;
            System.out.printf("  Used: %.2f GB%n", usedGb);
            System.out.println("  Files: " + storage.getFiles());
            System.out.println();

            // Get API rate limits
            System.out.println("Getting API rate limits...");
            AccountLimits limits = client.getAccountLimits();
            if (limits.getRest() != null) {
                System.out.println("  REST API: " + limits.getRest());
            }
            System.out.println();

            // List root folder contents
            System.out.println("Listing root folder contents...");
            FolderContents contents = client.readFolderContents("/");
            int count = 0;
            for (FileInfo item : contents.getContents()) {
                if (count >= 10) {
                    System.out.println("  ... and " + (contents.getContents().size() - 10) + " more items");
                    break;
                }
                if (Boolean.TRUE.equals(item.getIsDirectory())) {
                    System.out.println("  [DIR] " + item.getFilename());
                } else {
                    System.out.printf("  [FILE] %s - %,d bytes%n", item.getFilename(),
                            item.getSize() != null ? item.getSize() : 0);
                }
                count++;
            }
            System.out.println();

            // Search for image files
            System.out.println("Searching for image files...");
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("query", "extension:(jpg OR png OR gif OR webp)");
            searchParams.put("size", 5);

            SearchResult results = client.searchFiles(searchParams);
            System.out.println("  Found " + results.getTotal() + " image files");
            for (Map<String, Object> hit : results.getHits()) {
                FileInfo fileInfo = SearchResult.extractFileInfo(hit);
                System.out.println("  - " + fileInfo.getFilename());
            }
            System.out.println();

            System.out.println("Basic usage example completed successfully!");

        } catch (SirvApiException e) {
            System.err.println("API Error: " + e.getMessage());
            System.err.println("Status Code: " + e.getStatusCode());
            if (e.getErrorCode() != null) {
                System.err.println("Error Code: " + e.getErrorCode());
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
