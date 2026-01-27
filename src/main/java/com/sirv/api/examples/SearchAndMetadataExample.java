package com.sirv.api.examples;

import com.sirv.api.client.SirvClient;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.*;

import java.util.*;

/**
 * Search and metadata example for Sirv REST API Java SDK.
 *
 * <p>This example demonstrates:</p>
 * <ul>
 *   <li>Searching for files with various queries</li>
 *   <li>Paginated search with scroll</li>
 *   <li>Getting and setting file metadata</li>
 *   <li>Working with tags</li>
 *   <li>Product metadata management</li>
 * </ul>
 */
public class SearchAndMetadataExample {

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
            System.out.println("=== Search and Metadata Example ===\n");

            // Search for all files
            System.out.println("Searching for all files...");
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("query", "*");
            searchParams.put("size", 10);

            SearchResult results = client.searchFiles(searchParams);
            System.out.println("  Total files: " + results.getTotal());
            System.out.println();

            // Search with size filter in query
            System.out.println("Searching for image files larger than 100KB...");
            searchParams.clear();
            searchParams.put("query", "extension:(jpg OR png OR gif) AND size:>100000");
            searchParams.put("size", 5);

            results = client.searchFiles(searchParams);
            System.out.println("  Found " + results.getTotal() + " files matching criteria");
            for (Map<String, Object> hit : results.getHits()) {
                FileInfo fileInfo = SearchResult.extractFileInfo(hit);
                double sizeKb = fileInfo.getSize() != null ? fileInfo.getSize() / 1000.0 : 0;
                System.out.printf("  - %s (%.1f KB)%n", fileInfo.getFilename(), sizeKb);
            }
            System.out.println();

            // Search for files
            System.out.println("Searching for recently modified files...");
            searchParams.clear();
            searchParams.put("query", "*");
            searchParams.put("size", 5);

            results = client.searchFiles(searchParams);
            for (Map<String, Object> hit : results.getHits()) {
                FileInfo fileInfo = SearchResult.extractFileInfo(hit);
                System.out.println("  - " + fileInfo.getFilename() +
                    " (modified: " + fileInfo.getMtime() + ")");
            }
            System.out.println();

            // Counting files with pagination
            System.out.println("Counting all JPG files using pagination...");
            searchParams.clear();
            searchParams.put("query", "extension:jpg");
            searchParams.put("size", 100);

            results = client.searchFiles(searchParams);
            int count = results.getHits().size();

            while (results.getScrollId() != null && !results.getHits().isEmpty() && count < 1000) {
                results = client.scrollSearch(results.getScrollId());
                count += results.getHits().size();
            }
            if (count >= 1000) {
                System.out.println("  (stopping at 1000 files)");
            }
            System.out.println("  Found at least " + count + " JPG files");
            System.out.println();

            // Get metadata for a file (if any files exist)
            searchParams.clear();
            searchParams.put("query", "*");
            searchParams.put("size", 1);
            results = client.searchFiles(searchParams);

            if (results.getHits() != null && !results.getHits().isEmpty()) {
                FileInfo firstFile = SearchResult.extractFileInfo(results.getHits().get(0));
                String testFile = firstFile.getFilename();
                System.out.println("Getting metadata for: " + testFile);

                try {
                    FileMeta meta = client.getFileMeta(testFile);
                    System.out.println("  Title: " + (meta.getTitle() != null ? meta.getTitle() : "(not set)"));
                    System.out.println("  Description: " + (meta.getDescription() != null ? meta.getDescription() : "(not set)"));
                    System.out.println("  Tags: " + (meta.getTags() != null ? meta.getTags() : "[]"));
                    System.out.println("  Approved: " + (meta.getApproved() != null ? meta.getApproved() : false));
                } catch (SirvApiException e) {
                    System.out.println("  (No metadata available)");
                }
                System.out.println();

                // Get individual metadata fields
                System.out.println("Getting individual metadata fields...");
                try {
                    Map<String, String> titleResult = client.getFileTitle(testFile);
                    System.out.println("  Title: " + titleResult.getOrDefault("title", "(not set)"));
                } catch (SirvApiException e) {
                    System.out.println("  Title: (not available)");
                }

                try {
                    Map<String, List<String>> tagsResult = client.getFileTags(testFile);
                    System.out.println("  Tags: " + tagsResult.getOrDefault("tags", Collections.emptyList()));
                } catch (SirvApiException e) {
                    System.out.println("  Tags: (not available)");
                }
                System.out.println();
            }

            System.out.println("Search and metadata example completed!");

        } catch (SirvApiException e) {
            System.err.println("API Error: " + e.getMessage() + " (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
