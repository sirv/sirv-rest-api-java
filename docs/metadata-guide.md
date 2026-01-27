# Metadata Guide

This guide covers managing file metadata with the Sirv Java SDK.

## File Metadata Overview

Sirv supports several types of metadata:
- **Basic metadata**: title, description, tags, approval status
- **Product metadata**: id, name, brand, category, SKU

## Basic Metadata

### Get All Metadata

```java
FileMeta meta = client.getFileMeta("/images/photo.jpg");

System.out.println("Title: " + meta.getTitle());
System.out.println("Description: " + meta.getDescription());
System.out.println("Tags: " + meta.getTags());
System.out.println("Approved: " + meta.getApproved());
```

### Set All Metadata

```java
FileMeta meta = new FileMeta();
meta.setTitle("Beautiful Sunset");
meta.setDescription("Photo taken at the beach during sunset");
meta.setTags(Arrays.asList("sunset", "beach", "nature", "landscape"));
meta.setApproved(true);

client.setFileMeta("/images/photo.jpg", meta);
```

## Individual Metadata Fields

### Title

```java
// Get title
Map<String, String> result = client.getFileTitle("/images/photo.jpg");
String title = result.get("title");
System.out.println("Current title: " + title);

// Set title
client.setFileTitle("/images/photo.jpg", "My Beautiful Photo");
```

### Description

```java
// Get description
Map<String, String> result = client.getFileDescription("/images/photo.jpg");
String description = result.get("description");
System.out.println("Current description: " + description);

// Set description
client.setFileDescription("/images/photo.jpg", "A stunning landscape photograph taken in Hawaii");
```

### Approval Flag

```java
// Get approval status
Map<String, Boolean> result = client.getApprovalFlag("/images/photo.jpg");
boolean approved = result.getOrDefault("approved", false);
System.out.println("Is approved: " + approved);

// Set approval flag
client.setApprovalFlag("/images/photo.jpg", true);

// Remove approval
client.setApprovalFlag("/images/photo.jpg", false);
```

## Tags

### Get Tags

```java
Map<String, List<String>> result = client.getFileTags("/images/photo.jpg");
List<String> tags = result.getOrDefault("tags", Collections.emptyList());

System.out.println("Current tags:");
for (String tag : tags) {
    System.out.println("  - " + tag);
}
```

### Add Tags

```java
// Add single tag
client.addFileTags("/images/photo.jpg", Arrays.asList("nature"));

// Add multiple tags
client.addFileTags("/images/photo.jpg", Arrays.asList("sunset", "beach", "vacation"));
```

### Remove Tags

```java
// Remove single tag
client.removeFileTags("/images/photo.jpg", Arrays.asList("old-tag"));

// Remove multiple tags
client.removeFileTags("/images/photo.jpg", Arrays.asList("tag1", "tag2", "tag3"));
```

### Replace All Tags

```java
// First remove existing tags, then add new ones
List<String> existingTags = client.getFileTags("/images/photo.jpg")
    .getOrDefault("tags", Collections.emptyList());

if (!existingTags.isEmpty()) {
    client.removeFileTags("/images/photo.jpg", existingTags);
}

client.addFileTags("/images/photo.jpg", Arrays.asList("new-tag1", "new-tag2"));
```

## Product Metadata

Product metadata is useful for e-commerce and product images.

### Get Product Metadata

```java
ProductMeta product = client.getProductMeta("/products/item.jpg");

System.out.println("Product ID: " + product.getId());
System.out.println("Name: " + product.getName());
System.out.println("Brand: " + product.getBrand());
System.out.println("Category: " + product.getCategory());
System.out.println("SKU: " + product.getSku());
```

### Set Product Metadata

```java
ProductMeta product = new ProductMeta();
product.setId("PROD-12345");
product.setName("Wireless Bluetooth Headphones");
product.setBrand("AudioTech");
product.setCategory("Electronics > Audio > Headphones");
product.setSku("ATH-BT-100");

client.setProductMeta("/products/headphones.jpg", product);
```

### Partial Update

```java
// Only set specific fields
ProductMeta product = new ProductMeta();
product.setSku("NEW-SKU-001");  // Only update SKU

client.setProductMeta("/products/item.jpg", product);
```

## Bulk Metadata Operations

### Set Metadata for Multiple Files

```java
List<String> files = Arrays.asList(
    "/products/item1.jpg",
    "/products/item2.jpg",
    "/products/item3.jpg"
);

for (String file : files) {
    FileMeta meta = new FileMeta();
    meta.setApproved(true);
    client.setFileMeta(file, meta);
}
```

### Tag Multiple Files

```java
List<String> files = findProductImages(); // Your method to get file list
List<String> tags = Arrays.asList("2024-collection", "new-arrivals");

for (String file : files) {
    try {
        client.addFileTags(file, tags);
    } catch (SirvApiException e) {
        System.err.println("Failed to tag " + file + ": " + e.getMessage());
    }
}
```

## Best Practices

### 1. Use Meaningful Tags

```java
// Good - specific and searchable
client.addFileTags(file, Arrays.asList(
    "product-photo",
    "electronics",
    "2024-q1",
    "high-resolution"
));

// Avoid - too generic
client.addFileTags(file, Arrays.asList("image", "file", "good"));
```

### 2. Consistent Naming Convention

```java
// Use consistent tag format
String category = "electronics";
String year = "2024";
String status = "approved";

List<String> tags = Arrays.asList(
    "cat:" + category,
    "year:" + year,
    "status:" + status
);
```

### 3. Validate Before Setting

```java
public void setProductMetaSafely(SirvClient client, String file, ProductMeta meta) {
    // Validate SKU format
    if (meta.getSku() != null && !meta.getSku().matches("[A-Z0-9-]+")) {
        throw new IllegalArgumentException("Invalid SKU format");
    }

    // Validate required fields
    if (meta.getName() == null || meta.getName().isEmpty()) {
        throw new IllegalArgumentException("Product name is required");
    }

    client.setProductMeta(file, meta);
}
```

### 4. Handle Missing Metadata Gracefully

```java
FileMeta meta = client.getFileMeta(file);

String title = meta.getTitle();
if (title == null || title.isEmpty()) {
    // Generate title from filename
    title = extractTitleFromFilename(file);
}

String description = meta.getDescription();
if (description == null) {
    description = "No description available";
}
```

## Error Handling

```java
try {
    FileMeta meta = client.getFileMeta("/nonexistent/file.jpg");
} catch (SirvApiException e) {
    if (e.getStatusCode() == 404) {
        System.out.println("File not found");
    } else {
        throw e;
    }
}
```
