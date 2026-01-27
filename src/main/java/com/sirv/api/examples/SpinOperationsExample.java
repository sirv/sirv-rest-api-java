package com.sirv.api.examples;

import com.sirv.api.client.SirvClient;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.*;

import java.util.*;

/**
 * 360 Spin operations example for Sirv REST API Java SDK.
 *
 * <p>This example demonstrates:</p>
 * <ul>
 *   <li>Searching for spin files</li>
 *   <li>Getting points of interest</li>
 *   <li>Converting spins to videos</li>
 *   <li>Exporting spins to marketplaces</li>
 * </ul>
 */
public class SpinOperationsExample {

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
            System.out.println("=== 360 Spin Operations Example ===\n");

            // Search for spin files
            System.out.println("Searching for spin files...");
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("query", "extension:spin");
            searchParams.put("size", 10);

            SearchResult results = client.searchFiles(searchParams);
            System.out.println("  Found " + results.getTotal() + " spin files");

            if (results.getHits() == null || results.getHits().isEmpty()) {
                System.out.println("\nNo spin files found in your account.");
                System.out.println("Upload some 360 spins to test these features!");
                return;
            }

            for (int i = 0; i < Math.min(5, results.getHits().size()); i++) {
                FileInfo fileInfo = SearchResult.extractFileInfo(results.getHits().get(i));
                System.out.println("  - " + fileInfo.getFilename());
            }
            System.out.println();

            // Get the first spin file for demonstration
            FileInfo firstSpin = SearchResult.extractFileInfo(results.getHits().get(0));
            String testSpin = firstSpin.getFilename();
            System.out.println("Using spin file: " + testSpin + "\n");

            // Get points of interest
            System.out.println("Getting points of interest...");
            try {
                List<PointOfInterest> pois = client.getPointsOfInterest(testSpin);
                if (pois != null && !pois.isEmpty()) {
                    System.out.println("  Found " + pois.size() + " points of interest:");
                    for (PointOfInterest poi : pois) {
                        System.out.printf("    - %s: (%.2f, %.2f) frame %d%n",
                            poi.getName(), poi.getX(), poi.getY(),
                            poi.getFrame() != null ? poi.getFrame() : 0);
                    }
                } else {
                    System.out.println("  No points of interest defined");
                }
            } catch (SirvApiException e) {
                System.out.println("  Could not get POIs: " + e.getMessage());
            }
            System.out.println();

            // Example: Convert spin to video (commented out to avoid creating files)
            System.out.println("Spin to Video conversion example:");
            System.out.println("  // Uncomment below to convert a spin to video");
            System.out.println("  // Map<String, Object> options = new HashMap<>();");
            System.out.println("  // options.put(\"width\", 1920);");
            System.out.println("  // options.put(\"height\", 1080);");
            System.out.println("  // options.put(\"loops\", 2);");
            System.out.println("  // options.put(\"format\", \"mp4\");");
            System.out.println("  //");
            System.out.println("  // Map<String, Object> params = new HashMap<>();");
            System.out.println("  // params.put(\"filename\", testSpin);");
            System.out.println("  // params.put(\"options\", options);");
            System.out.println("  //");
            System.out.println("  // Map<String, Object> result = client.spin2video(params);");
            System.out.println("  // System.out.println(\"Video created: \" + result.get(\"filename\"));");
            System.out.println();

            // Example: Video to spin conversion
            System.out.println("Video to Spin conversion example:");
            System.out.println("  // Uncomment below to convert a video to spin");
            System.out.println("  // Map<String, Object> options = new HashMap<>();");
            System.out.println("  // options.put(\"frames\", 36);");
            System.out.println("  // options.put(\"start\", 0);");
            System.out.println("  // options.put(\"duration\", 10);");
            System.out.println("  //");
            System.out.println("  // Map<String, Object> params = new HashMap<>();");
            System.out.println("  // params.put(\"filename\", \"/videos/turntable.mp4\");");
            System.out.println("  // params.put(\"targetFilename\", \"/spins/new-product.spin\");");
            System.out.println("  // params.put(\"options\", options);");
            System.out.println("  //");
            System.out.println("  // Map<String, Object> result = client.video2spin(params);");
            System.out.println("  // System.out.println(\"Spin created: \" + result.get(\"filename\"));");
            System.out.println();

            // Example: Set point of interest
            System.out.println("Setting a point of interest example:");
            System.out.println("  // Uncomment below to set a POI");
            System.out.println("  // PointOfInterest poi = new PointOfInterest();");
            System.out.println("  // poi.setName(\"feature-highlight\");");
            System.out.println("  // poi.setX(0.5);");
            System.out.println("  // poi.setY(0.3);");
            System.out.println("  // poi.setFrame(0);");
            System.out.println("  //");
            System.out.println("  // client.setPointOfInterest(testSpin, poi);");
            System.out.println("  // System.out.println(\"POI set successfully\");");
            System.out.println();

            // Example: Export to marketplace
            System.out.println("Marketplace export examples:");
            System.out.println("  // Uncomment below to export to marketplaces");
            System.out.println("  // // Export to Amazon");
            System.out.println("  // client.exportSpinToAmazon(testSpin, \"B08N5WRWNW\");");
            System.out.println("  //");
            System.out.println("  // // Export to Walmart");
            System.out.println("  // client.exportSpinToWalmart(testSpin, \"123456789\");");
            System.out.println("  //");
            System.out.println("  // // Also available:");
            System.out.println("  // // - client.exportSpinToHomeDepot(filename, productId)");
            System.out.println("  // // - client.exportSpinToLowes(filename, productId)");
            System.out.println("  // // - client.exportSpinToGrainger(filename, productId)");
            System.out.println();

            System.out.println("360 Spin operations example completed!");
            System.out.println("\nNote: Conversion and export operations are commented out");
            System.out.println("to avoid creating files. Uncomment them to test.");

        } catch (SirvApiException e) {
            System.err.println("API Error: " + e.getMessage() + " (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
