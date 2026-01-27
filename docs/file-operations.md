# File Operations Guide

This guide covers file and folder operations with the Sirv Java SDK.

## Uploading Files

### Upload from Bytes

```java
byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
client.uploadFile("/documents/hello.txt", content, "text/plain");
```

### Upload from Local File

```java
import java.nio.file.Path;

Path localFile = Path.of("./local/image.jpg");
client.uploadFile("/images/photo.jpg", localFile);
```

### Upload with Content Type

```java
byte[] imageBytes = Files.readAllBytes(Path.of("photo.jpg"));
client.uploadFile("/images/photo.jpg", imageBytes, "image/jpeg");
```

## Downloading Files

### Download to Bytes

```java
byte[] content = client.downloadFile("/images/photo.jpg");
System.out.println("Downloaded " + content.length + " bytes");
```

### Download to Local File

```java
import java.nio.file.Path;

Path destination = Path.of("./downloads/photo.jpg");
client.downloadFileTo("/images/photo.jpg", destination);
```

## File Information

### Get File Info

```java
FileInfo info = client.getFileInfo("/images/photo.jpg");

System.out.println("Filename: " + info.getFilename());
System.out.println("Size: " + info.getSize() + " bytes");
System.out.println("Content-Type: " + info.getContentType());
System.out.println("Created: " + info.getCtime());
System.out.println("Modified: " + info.getMtime());
System.out.println("Is Directory: " + info.getIsDirectory());
```

## Folder Operations

### Create Folder

```java
client.createFolder("/images/2024");
client.createFolder("/images/2024/january");
```

### List Folder Contents

```java
FolderContents contents = client.readFolderContents("/images");

for (FileInfo item : contents.getContents()) {
    if (Boolean.TRUE.equals(item.getIsDirectory())) {
        System.out.println("[DIR]  " + item.getFilename());
    } else {
        System.out.printf("[FILE] %s (%d bytes)%n",
            item.getFilename(), item.getSize());
    }
}
```

### Paginated Listing

For folders with many items, use pagination:

```java
FolderContents contents = client.readFolderContents("/images");
List<FileInfo> allItems = new ArrayList<>(contents.getContents());

while (contents.getContinuation() != null) {
    contents = client.readFolderContents("/images", contents.getContinuation());
    allItems.addAll(contents.getContents());
}

System.out.println("Total items: " + allItems.size());
```

### Folder Options

```java
// Get folder options
Map<String, Object> options = client.getFolderOptions("/spins");
System.out.println("Scan spins: " + options.get("scanSpins"));

// Set folder options
client.setFolderOptions("/spins", Map.of("scanSpins", true));
```

## Copy, Rename, and Delete

### Copy File

```java
client.copyFile("/images/original.jpg", "/backup/original.jpg");
```

### Rename/Move File

```java
// Rename
client.renameFile("/images/old-name.jpg", "/images/new-name.jpg");

// Move to different folder
client.renameFile("/images/photo.jpg", "/archive/photo.jpg");
```

### Delete File

```java
client.deleteFile("/images/old-photo.jpg");
```

### Delete Empty Folder

```java
client.deleteFile("/images/empty-folder");
```

### Batch Delete

```java
List<String> filesToDelete = Arrays.asList(
    "/old/file1.jpg",
    "/old/file2.jpg",
    "/old/file3.jpg"
);

BatchResult result = client.batchDelete(filesToDelete);
System.out.println("Job ID: " + result.getJobId());

// Check status
BatchResult status = client.getBatchDeleteStatus(result.getJobId());
System.out.println("Status: " + status.getStatus());
```

## Fetch from URL

Download a file from an external URL and store it on Sirv:

```java
// Async (returns immediately)
client.fetchUrl("https://example.com/image.jpg", "/fetched/image.jpg", false);

// Sync (waits for completion)
client.fetchUrl("https://example.com/image.jpg", "/fetched/image.jpg", true);
```

## Create ZIP Archive

```java
List<String> filesToZip = Arrays.asList(
    "/images/photo1.jpg",
    "/images/photo2.jpg",
    "/images/photo3.jpg"
);

BatchResult result = client.batchZip(filesToZip, "/archives/photos.zip");
System.out.println("Job ID: " + result.getJobId());

// Check status
BatchResult status = client.getZipStatus(result.getJobId());
if ("completed".equals(status.getStatus())) {
    System.out.println("ZIP created: " + status.getFilename());
}
```

## Error Handling

```java
import com.sirv.api.exception.SirvApiException;

try {
    client.uploadFile("/images/photo.jpg", content, "image/jpeg");
} catch (SirvApiException e) {
    if (e.getStatusCode() == 409) {
        System.out.println("File already exists");
    } else if (e.getStatusCode() == 403) {
        System.out.println("Permission denied");
    } else {
        throw e;
    }
}
```

## Best Practices

1. **Use try-with-resources** for automatic cleanup
2. **Check file existence** before operations to avoid errors
3. **Use pagination** for large folder listings
4. **Handle errors gracefully** with try-catch blocks
5. **Set appropriate content types** when uploading files
