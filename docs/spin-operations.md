# 360 Spin Operations Guide

This guide covers working with 360 spins using the Sirv Java SDK.

## Overview

Sirv supports 360° product spins with features like:
- Converting spins to videos
- Converting videos to spins
- Points of interest
- Exporting to marketplaces

## Finding Spin Files

### Search for Spins

```java
Map<String, Object> params = new HashMap<>();
params.put("query", "extension:spin");
params.put("size", 100);

SearchResult results = client.searchFiles(params);
System.out.println("Found " + results.getTotal() + " spin files");

for (Map<String, Object> hit : results.getHits()) {
    FileInfo info = SearchResult.extractFileInfo(hit);
    System.out.println("  - " + info.getFilename());
}
```

## Points of Interest (POI)

Points of interest are interactive hotspots on spin images.

### Get Points of Interest

```java
List<PointOfInterest> pois = client.getPointsOfInterest("/spins/product.spin");

if (pois != null && !pois.isEmpty()) {
    System.out.println("Points of interest:");
    for (PointOfInterest poi : pois) {
        System.out.printf("  %s: (%.2f, %.2f) frame %d%n",
            poi.getName(),
            poi.getX(),
            poi.getY(),
            poi.getFrame() != null ? poi.getFrame() : 0);
    }
} else {
    System.out.println("No points of interest defined");
}
```

### Create Point of Interest

```java
PointOfInterest poi = new PointOfInterest();
poi.setName("feature-button");
poi.setX(0.5);   // X coordinate (0.0 to 1.0)
poi.setY(0.3);   // Y coordinate (0.0 to 1.0)
poi.setFrame(0); // Frame number (optional)

client.setPointOfInterest("/spins/product.spin", poi);
System.out.println("POI created successfully");
```

### Update Point of Interest

```java
// Setting a POI with the same name updates it
PointOfInterest poi = new PointOfInterest();
poi.setName("feature-button");  // Existing name
poi.setX(0.6);   // New position
poi.setY(0.4);
poi.setFrame(5); // Different frame

client.setPointOfInterest("/spins/product.spin", poi);
```

### Delete Point of Interest

```java
client.deletePointOfInterest("/spins/product.spin", "feature-button");
System.out.println("POI deleted");
```

## Spin to Video Conversion

Convert a 360 spin to a video file.

```java
Map<String, Object> options = new HashMap<>();
options.put("width", 1920);
options.put("height", 1080);
options.put("loops", 2);          // Number of full rotations
options.put("format", "mp4");     // Output format

Map<String, Object> params = new HashMap<>();
params.put("filename", "/spins/product.spin");
params.put("options", options);

Map<String, Object> result = client.spin2video(params);
System.out.println("Video created: " + result.get("filename"));
```

### Video Options

| Option | Type | Description |
|--------|------|-------------|
| `width` | int | Output video width |
| `height` | int | Output video height |
| `loops` | int | Number of rotations |
| `format` | string | Output format (mp4, webm) |
| `fps` | int | Frames per second |
| `quality` | int | Video quality (1-100) |

## Video to Spin Conversion

Convert a turntable video to a 360 spin.

```java
Map<String, Object> options = new HashMap<>();
options.put("frames", 36);        // Number of frames to extract
options.put("start", 0);          // Start time in seconds
options.put("duration", 10);      // Duration in seconds

Map<String, Object> params = new HashMap<>();
params.put("filename", "/videos/turntable.mp4");
params.put("targetFilename", "/spins/new-product.spin");
params.put("options", options);

Map<String, Object> result = client.video2spin(params);
System.out.println("Spin created: " + result.get("filename"));
```

### Video to Spin Options

| Option | Type | Description |
|--------|------|-------------|
| `frames` | int | Number of frames to extract |
| `start` | float | Start time in seconds |
| `duration` | float | Duration to process |

## Marketplace Exports

Export spins directly to e-commerce marketplaces.

### Export to Amazon

```java
client.exportSpinToAmazon("/spins/product.spin", "B08N5WRWNW");
System.out.println("Exported to Amazon");
```

### Export to Walmart

```java
client.exportSpinToWalmart("/spins/product.spin", "123456789");
System.out.println("Exported to Walmart");
```

### Export to Home Depot

```java
client.exportSpinToHomeDepot("/spins/product.spin", "PROD-12345");
System.out.println("Exported to Home Depot");
```

### Export to Lowe's

```java
client.exportSpinToLowes("/spins/product.spin", "ITEM-67890");
System.out.println("Exported to Lowe's");
```

### Export to Grainger

```java
client.exportSpinToGrainger("/spins/product.spin", "GRA-11111");
System.out.println("Exported to Grainger");
```

## Complete Example

```java
public class SpinManager {
    private final SirvClient client;

    public SpinManager(SirvClient client) {
        this.client = client;
    }

    /**
     * Process a spin file: add POIs and export to marketplaces.
     */
    public void processProductSpin(String spinPath, String asin, String walmartId) {
        // Add points of interest
        addStandardPOIs(spinPath);

        // Export to marketplaces
        if (asin != null) {
            try {
                client.exportSpinToAmazon(spinPath, asin);
                System.out.println("Exported to Amazon: " + asin);
            } catch (SirvApiException e) {
                System.err.println("Amazon export failed: " + e.getMessage());
            }
        }

        if (walmartId != null) {
            try {
                client.exportSpinToWalmart(spinPath, walmartId);
                System.out.println("Exported to Walmart: " + walmartId);
            } catch (SirvApiException e) {
                System.err.println("Walmart export failed: " + e.getMessage());
            }
        }
    }

    /**
     * Add standard points of interest to a spin.
     */
    private void addStandardPOIs(String spinPath) {
        // Front view hotspot
        PointOfInterest front = new PointOfInterest();
        front.setName("front-view");
        front.setX(0.5);
        front.setY(0.5);
        front.setFrame(0);

        // Back view hotspot
        PointOfInterest back = new PointOfInterest();
        back.setName("back-view");
        back.setX(0.5);
        back.setY(0.5);
        back.setFrame(18);  // Assuming 36 frames

        try {
            client.setPointOfInterest(spinPath, front);
            client.setPointOfInterest(spinPath, back);
            System.out.println("Added standard POIs");
        } catch (SirvApiException e) {
            System.err.println("Failed to add POIs: " + e.getMessage());
        }
    }

    /**
     * Create video from spin with standard settings.
     */
    public String createVideo(String spinPath) {
        Map<String, Object> options = new HashMap<>();
        options.put("width", 1280);
        options.put("height", 720);
        options.put("loops", 1);
        options.put("format", "mp4");

        Map<String, Object> params = new HashMap<>();
        params.put("filename", spinPath);
        params.put("options", options);

        Map<String, Object> result = client.spin2video(params);
        return (String) result.get("filename");
    }
}
```

## Best Practices

### 1. Check File Existence

```java
try {
    FileInfo info = client.getFileInfo(spinPath);
    if (!spinPath.endsWith(".spin")) {
        throw new IllegalArgumentException("Not a spin file");
    }
} catch (SirvApiException e) {
    if (e.getStatusCode() == 404) {
        throw new IllegalArgumentException("Spin file not found: " + spinPath);
    }
    throw e;
}
```

### 2. Validate POI Coordinates

```java
public void addPOI(String spinPath, String name, double x, double y, int frame) {
    if (x < 0 || x > 1 || y < 0 || y > 1) {
        throw new IllegalArgumentException("Coordinates must be between 0 and 1");
    }
    if (frame < 0) {
        throw new IllegalArgumentException("Frame must be non-negative");
    }

    PointOfInterest poi = new PointOfInterest();
    poi.setName(name);
    poi.setX(x);
    poi.setY(y);
    poi.setFrame(frame);

    client.setPointOfInterest(spinPath, poi);
}
```

### 3. Handle Export Errors Gracefully

```java
public void exportToAllMarketplaces(String spinPath, Map<String, String> ids) {
    List<String> successful = new ArrayList<>();
    List<String> failed = new ArrayList<>();

    if (ids.containsKey("amazon")) {
        try {
            client.exportSpinToAmazon(spinPath, ids.get("amazon"));
            successful.add("Amazon");
        } catch (SirvApiException e) {
            failed.add("Amazon: " + e.getMessage());
        }
    }

    // Repeat for other marketplaces...

    System.out.println("Successful: " + successful);
    if (!failed.isEmpty()) {
        System.out.println("Failed: " + failed);
    }
}
```
