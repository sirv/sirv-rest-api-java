package com.sirv.api.examples;

import com.sirv.api.client.SirvClient;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.StorageInfo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Statistics example for Sirv REST API Java SDK.
 *
 * <p>This example demonstrates:</p>
 * <ul>
 *   <li>Getting HTTP transfer statistics</li>
 *   <li>Getting storage statistics</li>
 *   <li>Getting spin views statistics</li>
 * </ul>
 */
public class StatisticsExample {

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
            System.out.println("=== Statistics Example ===\n");

            // Calculate date range (last 30 days)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            String fromDate = startDate.format(DateTimeFormatter.ISO_DATE);
            String toDate = endDate.format(DateTimeFormatter.ISO_DATE);

            System.out.println("Statistics period: " + fromDate + " to " + toDate + "\n");

            // HTTP Transfer Statistics
            System.out.println("HTTP Transfer Statistics (last 30 days):");
            System.out.println("-".repeat(50));
            try {
                Map<String, Object> httpStats = client.getHttpStats(fromDate, toDate);

                if (httpStats != null && !httpStats.isEmpty()) {
                    long totalTransfer = 0;
                    long totalRequests = 0;

                    for (Object value : httpStats.values()) {
                        if (value instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> day = (Map<String, Object>) value;
                            @SuppressWarnings("unchecked")
                            Map<String, Object> total = (Map<String, Object>) day.get("total");
                            if (total != null) {
                                Number size = (Number) total.get("size");
                                Number count = (Number) total.get("count");
                                if (size != null) totalTransfer += size.longValue();
                                if (count != null) totalRequests += count.longValue();
                            }
                        }
                    }

                    System.out.println("  Total data transferred: " + formatBytes(totalTransfer));
                    System.out.printf("  Total requests: %,d%n", totalRequests);
                    System.out.println("  Days with data: " + httpStats.size());

                    // Show last 5 days
                    System.out.println("\n  Last 5 days:");
                    List<Map.Entry<String, Object>> sortedDays = new ArrayList<>(httpStats.entrySet());
                    sortedDays.sort((a, b) -> Long.compare(
                        Long.parseLong(a.getKey()), Long.parseLong(b.getKey())));

                    int start = Math.max(0, sortedDays.size() - 5);
                    for (int i = start; i < sortedDays.size(); i++) {
                        Map.Entry<String, Object> entry = sortedDays.get(i);
                        String date = formatTimestamp(entry.getKey());
                        @SuppressWarnings("unchecked")
                        Map<String, Object> day = (Map<String, Object>) entry.getValue();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> total = (Map<String, Object>) day.get("total");

                        long transfer = 0;
                        long requests = 0;
                        if (total != null) {
                            Number size = (Number) total.get("size");
                            Number count = (Number) total.get("count");
                            if (size != null) transfer = size.longValue();
                            if (count != null) requests = count.longValue();
                        }
                        System.out.printf("    %s: %s (%,d requests)%n", date, formatBytes(transfer), requests);
                    }
                } else {
                    System.out.println("  No HTTP statistics available");
                }
            } catch (SirvApiException e) {
                System.out.println("  Error getting HTTP stats: " + e.getMessage());
            }
            System.out.println();

            // Spin Views Statistics (max 5-day range)
            LocalDate spinStart = endDate.minusDays(5);
            String spinFrom = spinStart.format(DateTimeFormatter.ISO_DATE);
            String spinTo = toDate;

            System.out.println("Spin Views Statistics (" + spinFrom + " to " + spinTo + "):");
            System.out.println("-".repeat(50));
            try {
                Map<String, Object> spinStats = client.getSpinViewsStats(spinFrom, spinTo);

                if (spinStats != null && !spinStats.isEmpty()) {
                    long totalViews = 0;
                    long totalSpins = 0;
                    long totalZooms = 0;

                    for (Object value : spinStats.values()) {
                        if (value instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> day = (Map<String, Object>) value;
                            Number views = (Number) day.get("views");
                            Number spins = (Number) day.get("spins");
                            Number zooms = (Number) day.get("zooms");
                            if (views != null) totalViews += views.longValue();
                            if (spins != null) totalSpins += spins.longValue();
                            if (zooms != null) totalZooms += zooms.longValue();
                        }
                    }

                    System.out.printf("  Total views: %,d%n", totalViews);
                    System.out.printf("  Total spin interactions: %,d%n", totalSpins);
                    System.out.printf("  Total zoom interactions: %,d%n", totalZooms);
                } else {
                    System.out.println("  No spin view statistics available");
                }
            } catch (SirvApiException e) {
                System.out.println("  Error getting spin stats: " + e.getMessage());
            }
            System.out.println();

            // Storage Statistics
            System.out.println("Storage Statistics (last 30 days):");
            System.out.println("-".repeat(50));
            try {
                Map<String, Object> storageStats = client.getStorageStats(fromDate, toDate);

                if (storageStats != null && !storageStats.isEmpty()) {
                    List<Map.Entry<String, Object>> sortedDays = new ArrayList<>(storageStats.entrySet());
                    sortedDays.sort((a, b) -> Long.compare(
                        Long.parseLong(a.getKey()), Long.parseLong(b.getKey())));

                    if (!sortedDays.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> firstDay = (Map<String, Object>) sortedDays.get(0).getValue();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> lastDay = (Map<String, Object>) sortedDays.get(sortedDays.size() - 1).getValue();

                        Number firstUsed = (Number) firstDay.get("used");
                        Number lastUsed = (Number) lastDay.get("used");
                        Number firstFiles = (Number) firstDay.get("files");
                        Number lastFiles = (Number) lastDay.get("files");

                        long storageChange = (lastUsed != null ? lastUsed.longValue() : 0) -
                                           (firstUsed != null ? firstUsed.longValue() : 0);
                        long filesChange = (lastFiles != null ? lastFiles.longValue() : 0) -
                                          (firstFiles != null ? firstFiles.longValue() : 0);

                        System.out.println("  Current storage: " + formatBytes(lastUsed != null ? lastUsed.longValue() : 0));
                        System.out.printf("  Current files: %,d%n", lastFiles != null ? lastFiles.longValue() : 0);
                        System.out.println("  Storage change: " + (storageChange >= 0 ? "+" : "") + formatBytes(storageChange));
                        System.out.printf("  Files change: %+,d%n", filesChange);

                        // Show last 5 days
                        System.out.println("\n  Last 5 days:");
                        int start = Math.max(0, sortedDays.size() - 5);
                        for (int i = start; i < sortedDays.size(); i++) {
                            Map.Entry<String, Object> entry = sortedDays.get(i);
                            String date = formatTimestamp(entry.getKey());
                            @SuppressWarnings("unchecked")
                            Map<String, Object> day = (Map<String, Object>) entry.getValue();

                            Number used = (Number) day.get("used");
                            Number files = (Number) day.get("files");
                            System.out.printf("    %s: %s (%,d files)%n", date,
                                formatBytes(used != null ? used.longValue() : 0),
                                files != null ? files.longValue() : 0);
                        }
                    }
                } else {
                    System.out.println("  No storage statistics available");
                }
            } catch (SirvApiException e) {
                System.out.println("  Error getting storage stats: " + e.getMessage());
            }
            System.out.println();

            // Current storage info
            System.out.println("Current Storage Info:");
            System.out.println("-".repeat(50));
            StorageInfo storageInfo = client.getStorageInfo();
            System.out.println("  Used: " + formatBytes(storageInfo.getUsed() != null ? storageInfo.getUsed() : 0));
            if (storageInfo.getPlan() != null) {
                System.out.println("  Plan allowance: " + formatBytes(storageInfo.getPlan()));
            }
            System.out.printf("  Files: %,d%n", storageInfo.getFiles() != null ? storageInfo.getFiles() : 0);
            System.out.println();

            System.out.println("Statistics example completed!");

        } catch (SirvApiException e) {
            System.err.println("API Error: " + e.getMessage() + " (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static String formatTimestamp(String timestamp) {
        try {
            long millis = Long.parseLong(timestamp);
            return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ISO_DATE);
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
}
