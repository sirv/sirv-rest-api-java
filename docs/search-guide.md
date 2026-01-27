# Search Guide

This guide covers searching for files with the Sirv Java SDK.

## Basic Search

### Search All Files

```java
Map<String, Object> params = new HashMap<>();
params.put("query", "*");
params.put("size", 100);

SearchResult results = client.searchFiles(params);
System.out.println("Total files: " + results.getTotal());
```

### Search by Extension

```java
Map<String, Object> params = new HashMap<>();
params.put("query", "extension:jpg");
params.put("size", 50);

SearchResult results = client.searchFiles(params);
```

### Search Multiple Extensions

```java
params.put("query", "extension:(jpg OR png OR gif OR webp)");
```

## Advanced Queries

### Search by Filename Pattern

```java
// Files starting with "product"
params.put("query", "basename:product*");

// Files containing "holiday"
params.put("query", "basename:*holiday*");
```

### Search by Directory

```java
// Files in a specific directory
params.put("query", "dirname:/images/2024");

// Files in directory and subdirectories
params.put("query", "dirname:/images/*");
```

### Search by Size

```java
// Files larger than 1MB
params.put("query", "size:>1000000");

// Files between 100KB and 1MB
params.put("query", "size:>100000 AND size:<1000000");
```

### Search by Content Type

```java
// All images
params.put("query", "contentType:image/*");

// Specific type
params.put("query", "contentType:image/jpeg");
```

### Combined Queries

```java
// Large JPG images in a specific folder
params.put("query", "extension:jpg AND size:>500000 AND dirname:/products");
```

## Processing Results

### Extract File Information

```java
SearchResult results = client.searchFiles(params);

for (Map<String, Object> hit : results.getHits()) {
    // Use helper method to extract FileInfo
    FileInfo fileInfo = SearchResult.extractFileInfo(hit);

    System.out.println("Filename: " + fileInfo.getFilename());
    System.out.println("Size: " + fileInfo.getSize());
    System.out.println("Modified: " + fileInfo.getMtime());
    System.out.println("---");
}
```

### Access Raw Hit Data

```java
for (Map<String, Object> hit : results.getHits()) {
    // Access _source directly
    @SuppressWarnings("unchecked")
    Map<String, Object> source = (Map<String, Object>) hit.get("_source");

    String filename = (String) source.get("filename");
    Number size = (Number) source.get("size");

    // Access search metadata
    Double score = (Double) hit.get("_score");
    String id = (String) hit.get("_id");
}
```

## Pagination

### Using Scroll API

For large result sets, use the scroll API:

```java
Map<String, Object> params = new HashMap<>();
params.put("query", "extension:jpg");
params.put("size", 100);

List<FileInfo> allFiles = new ArrayList<>();
SearchResult results = client.searchFiles(params);

// Add first page
for (Map<String, Object> hit : results.getHits()) {
    allFiles.add(SearchResult.extractFileInfo(hit));
}

// Continue scrolling
while (results.getScrollId() != null && !results.getHits().isEmpty()) {
    results = client.scrollSearch(results.getScrollId());

    for (Map<String, Object> hit : results.getHits()) {
        allFiles.add(SearchResult.extractFileInfo(hit));
    }
}

System.out.println("Total files found: " + allFiles.size());
```

### Limiting Results

```java
// Stop after a certain number
int maxResults = 500;
int count = 0;

while (results.getScrollId() != null && !results.getHits().isEmpty() && count < maxResults) {
    results = client.scrollSearch(results.getScrollId());
    count += results.getHits().size();
}
```

## Search Examples

### Find Large Images

```java
Map<String, Object> params = new HashMap<>();
params.put("query", "extension:(jpg OR png) AND size:>1000000");
params.put("size", 20);

SearchResult results = client.searchFiles(params);
System.out.println("Found " + results.getTotal() + " images larger than 1MB");
```

### Find Recently Modified Files

```java
// Files modified in the last 7 days would use date range queries
// Note: Sort by mtime may require different API format
params.put("query", "*");
params.put("size", 10);

SearchResult results = client.searchFiles(params);
for (Map<String, Object> hit : results.getHits()) {
    FileInfo info = SearchResult.extractFileInfo(hit);
    System.out.println(info.getFilename() + " - " + info.getMtime());
}
```

### Find Spin Files

```java
params.put("query", "extension:spin");
params.put("size", 50);

SearchResult results = client.searchFiles(params);
System.out.println("Found " + results.getTotal() + " spin files");
```

### Find Files Without Extension

```java
params.put("query", "NOT _exists_:extension");
```

## Query Syntax Reference

| Operator | Example | Description |
|----------|---------|-------------|
| `*` | `*` | Match all files |
| `AND` | `a AND b` | Both conditions must match |
| `OR` | `a OR b` | Either condition must match |
| `NOT` | `NOT a` | Condition must not match |
| `:` | `field:value` | Field equals value |
| `>` | `size:>1000` | Greater than |
| `<` | `size:<1000` | Less than |
| `>=` | `size:>=1000` | Greater than or equal |
| `<=` | `size:<=1000` | Less than or equal |
| `*` | `basename:photo*` | Wildcard |
| `()` | `(a OR b)` | Grouping |

## Searchable Fields

| Field | Type | Description |
|-------|------|-------------|
| `filename` | string | Full file path |
| `dirname` | string | Directory path |
| `basename` | string | Filename without path |
| `extension` | string | File extension |
| `contentType` | string | MIME type |
| `size` | number | File size in bytes |
| `ctime` | date | Creation time |
| `mtime` | date | Modification time |
