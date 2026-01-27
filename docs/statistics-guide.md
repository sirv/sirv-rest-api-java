# Statistics Guide

This guide covers accessing usage statistics with the Sirv Java SDK.

## Overview

Sirv provides statistics for:
- **HTTP Transfer**: Bandwidth and request counts
- **Storage**: Storage usage over time
- **Spin Views**: 360 spin viewer analytics

## HTTP Transfer Statistics

Get daily transfer statistics for a date range:

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

LocalDate endDate = LocalDate.now();
LocalDate startDate = endDate.minusDays(30);

String from = startDate.format(DateTimeFormatter.ISO_DATE);
String to = endDate.format(DateTimeFormatter.ISO_DATE);

Map<String, Object> stats = client.getHttpStats(from, to);
```

### Processing HTTP Stats

The API returns a map with timestamps as keys:

```java
long totalTransfer = 0;
long totalRequests = 0;

for (Map.Entry<String, Object> entry : stats.entrySet()) {
    String timestamp = entry.getKey();

    @SuppressWarnings("unchecked")
    Map<String, Object> dayData = (Map<String, Object>) entry.getValue();

    @SuppressWarnings("unchecked")
    Map<String, Object> total = (Map<String, Object>) dayData.get("total");

    if (total != null) {
        Number size = (Number) total.get("size");
        Number count = (Number) total.get("count");

        if (size != null) totalTransfer += size.longValue();
        if (count != null) totalRequests += count.longValue();
    }
}

System.out.printf("Total transfer: %.2f GB%n", totalTransfer / 1e9);
System.out.printf("Total requests: %,d%n", totalRequests);
```

### Converting Timestamps to Dates

```java
import java.time.Instant;
import java.time.ZoneId;

private String formatTimestamp(String timestamp) {
    long millis = Long.parseLong(timestamp);
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ISO_DATE);
}

// Usage
for (String timestamp : stats.keySet()) {
    String date = formatTimestamp(timestamp);
    System.out.println("Date: " + date);
}
```

## Storage Statistics

Get daily storage usage:

```java
Map<String, Object> stats = client.getStorageStats(from, to);

// Sort by timestamp
List<Map.Entry<String, Object>> sortedDays = new ArrayList<>(stats.entrySet());
sortedDays.sort((a, b) -> Long.compare(
    Long.parseLong(a.getKey()),
    Long.parseLong(b.getKey())
));

// Get first and last day for comparison
@SuppressWarnings("unchecked")
Map<String, Object> firstDay = (Map<String, Object>) sortedDays.get(0).getValue();
@SuppressWarnings("unchecked")
Map<String, Object> lastDay = (Map<String, Object>) sortedDays.get(sortedDays.size() - 1).getValue();

Number firstUsed = (Number) firstDay.get("used");
Number lastUsed = (Number) lastDay.get("used");

long storageChange = lastUsed.longValue() - firstUsed.longValue();
System.out.printf("Storage change: %+.2f MB%n", storageChange / 1e6);
```

### Storage Stats Fields

Each day's data includes:
- `plan`: Storage plan allowance in bytes
- `burstable`: Burstable storage allowance
- `extra`: Extra storage purchased
- `used`: Storage currently used in bytes
- `files`: Number of files
- `quotaExceededDate`: Date when quota was exceeded (if any)

```java
@SuppressWarnings("unchecked")
Map<String, Object> day = (Map<String, Object>) stats.values().iterator().next();

System.out.println("Plan: " + formatBytes((Number) day.get("plan")));
System.out.println("Used: " + formatBytes((Number) day.get("used")));
System.out.println("Files: " + day.get("files"));
```

## Spin Views Statistics

Get spin viewer analytics (maximum 5-day range):

```java
LocalDate endDate = LocalDate.now();
LocalDate startDate = endDate.minusDays(5);  // Max 5 days

String from = startDate.format(DateTimeFormatter.ISO_DATE);
String to = endDate.format(DateTimeFormatter.ISO_DATE);

try {
    Map<String, Object> stats = client.getSpinViewsStats(from, to);

    long totalViews = 0;
    long totalSpins = 0;
    long totalZooms = 0;

    for (Object value : stats.values()) {
        @SuppressWarnings("unchecked")
        Map<String, Object> day = (Map<String, Object>) value;

        Number views = (Number) day.get("views");
        Number spins = (Number) day.get("spins");
        Number zooms = (Number) day.get("zooms");

        if (views != null) totalViews += views.longValue();
        if (spins != null) totalSpins += spins.longValue();
        if (zooms != null) totalZooms += zooms.longValue();
    }

    System.out.printf("Total views: %,d%n", totalViews);
    System.out.printf("Total spin interactions: %,d%n", totalSpins);
    System.out.printf("Total zoom interactions: %,d%n", totalZooms);

} catch (SirvApiException e) {
    System.out.println("Error getting spin stats: " + e.getMessage());
}
```

## Current Storage Info

Get current storage usage (not historical):

```java
StorageInfo storage = client.getStorageInfo();

System.out.println("Plan allowance: " + formatBytes(storage.getPlan()));
System.out.println("Currently used: " + formatBytes(storage.getUsed()));
System.out.println("Files: " + storage.getFiles());

if (storage.getPlan() != null && storage.getUsed() != null) {
    double usagePercent = (storage.getUsed() * 100.0) / storage.getPlan();
    System.out.printf("Usage: %.1f%%%n", usagePercent);
}
```

## Helper Functions

### Format Bytes

```java
private static String formatBytes(Number bytes) {
    if (bytes == null) return "0 B";
    long b = bytes.longValue();

    if (b < 1024) return b + " B";
    if (b < 1024 * 1024) return String.format("%.2f KB", b / 1024.0);
    if (b < 1024 * 1024 * 1024) return String.format("%.2f MB", b / (1024.0 * 1024));
    return String.format("%.2f GB", b / (1024.0 * 1024 * 1024));
}
```

### Get Daily Breakdown

```java
public void printDailyBreakdown(Map<String, Object> stats) {
    List<Map.Entry<String, Object>> sorted = new ArrayList<>(stats.entrySet());
    sorted.sort((a, b) -> Long.compare(
        Long.parseLong(a.getKey()),
        Long.parseLong(b.getKey())
    ));

    System.out.println("Daily breakdown:");
    for (Map.Entry<String, Object> entry : sorted) {
        String date = formatTimestamp(entry.getKey());

        @SuppressWarnings("unchecked")
        Map<String, Object> day = (Map<String, Object>) entry.getValue();

        // Access fields based on stat type
        System.out.println("  " + date + ": " + day);
    }
}
```

## Rate Limits

Check current API rate limits:

```java
AccountLimits limits = client.getAccountLimits();

if (limits.getRest() != null) {
    Map<String, Object> rest = limits.getRest();
    System.out.println("REST API Limits:");
    System.out.println("  Limit: " + rest.get("limit"));
    System.out.println("  Remaining: " + rest.get("remaining"));
    System.out.println("  Reset: " + rest.get("reset"));
}

if (limits.getS3() != null) {
    System.out.println("S3 API Limits: " + limits.getS3());
}
```

## Example: Monthly Report

```java
public void generateMonthlyReport(SirvClient client) {
    LocalDate now = LocalDate.now();
    LocalDate firstOfMonth = now.withDayOfMonth(1);

    String from = firstOfMonth.format(DateTimeFormatter.ISO_DATE);
    String to = now.format(DateTimeFormatter.ISO_DATE);

    System.out.println("=== Monthly Usage Report ===");
    System.out.println("Period: " + from + " to " + to);
    System.out.println();

    // HTTP Stats
    Map<String, Object> httpStats = client.getHttpStats(from, to);
    long totalTransfer = 0;
    long totalRequests = 0;

    for (Object value : httpStats.values()) {
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

    System.out.println("HTTP Transfer:");
    System.out.printf("  Total data: %s%n", formatBytes(totalTransfer));
    System.out.printf("  Total requests: %,d%n", totalRequests);
    System.out.println();

    // Storage Stats
    Map<String, Object> storageStats = client.getStorageStats(from, to);
    if (!storageStats.isEmpty()) {
        List<Long> timestamps = storageStats.keySet().stream()
            .map(Long::parseLong)
            .sorted()
            .toList();

        @SuppressWarnings("unchecked")
        Map<String, Object> latest = (Map<String, Object>)
            storageStats.get(String.valueOf(timestamps.get(timestamps.size() - 1)));

        System.out.println("Current Storage:");
        System.out.printf("  Used: %s%n", formatBytes((Number) latest.get("used")));
        System.out.printf("  Files: %,d%n", ((Number) latest.get("files")).longValue());
    }
}
```
