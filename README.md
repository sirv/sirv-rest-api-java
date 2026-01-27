# Sirv REST API Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/com.sirv/sirv-rest-api.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.sirv%22%20AND%20a:%22sirv-rest-api%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://www.oracle.com/java/)

Official Java SDK for the [Sirv REST API](https://apidocs.sirv.com/) - Image CDN, 360 Spins, and Media Management.

## Features

- **Full API Coverage**: All Sirv REST API endpoints implemented
- **Automatic Token Management**: Auto-refresh tokens before expiry
- **Custom Token Expiry**: Support for custom token expiration times (5-604800 seconds)
- **Retry Logic**: Built-in retry mechanism for failed requests
- **AutoCloseable**: Use with try-with-resources for automatic cleanup

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.sirv</groupId>
    <artifactId>sirv-rest-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```groovy
implementation 'com.sirv:sirv-rest-api:1.0.0'
```

Or for Kotlin DSL (`build.gradle.kts`):

```kotlin
implementation("com.sirv:sirv-rest-api:1.0.0")
```

### Building from Source

```bash
git clone https://github.com/sirv/sirv-rest-api-java.git
cd sirv-rest-api-java
mvn clean install
```

## Quick Start

```java
import com.sirv.api.client.SirvClient;
import com.sirv.api.model.*;

// Initialize the client
SirvClient client = new SirvClient(
    "your-client-id",
    "your-client-secret"
);

// Connect to get access token
client.connect();

// Get account information
AccountInfo account = client.getAccountInfo();
System.out.println("CDN URL: " + account.getCdnURL());

// Upload a file
client.uploadFile("/images/photo.jpg", photoBytes, "image/jpeg");

// Search for files
Map<String, Object> params = new HashMap<>();
params.put("query", "*.jpg");
params.put("size", 100);
SearchResult results = client.searchFiles(params);

// Close the client
client.close();
```

## Using Try-With-Resources

```java
try (SirvClient client = new SirvClient("your-client-id", "your-client-secret")) {
    client.connect();
    AccountInfo account = client.getAccountInfo();
    System.out.println("Account: " + account.getAlias());
} // Session automatically closed
```

## Custom Token Expiry

You can request a token with a custom expiration time (5-604800 seconds):

```java
// Request a token valid for 1 hour
TokenResponse token = client.connect(3600);
System.out.println("Token expires in: " + token.getExpiresIn() + " seconds");
```

## API Reference

### Authentication

#### Connect

```java
// Default expiry (1200 seconds / 20 minutes)
TokenResponse token = client.connect();

// Custom expiry (1 hour)
TokenResponse token = client.connect(3600);
```

### Account Management

```java
// Get account info
AccountInfo account = client.getAccountInfo();

// Get storage info
StorageInfo storage = client.getStorageInfo();

// Get API rate limits
AccountLimits limits = client.getAccountLimits();

// Get billing plan
BillingPlan plan = client.getBillingPlan();

// Get account users
List<UserInfo> users = client.getAccountUsers();
```

### File Operations

```java
// Upload file from bytes
client.uploadFile("/images/photo.jpg", imageBytes, "image/jpeg");

// Upload file from local path
client.uploadFile("/images/photo.jpg", Path.of("./local/photo.jpg"));

// Download file
byte[] content = client.downloadFile("/images/photo.jpg");

// Download to local file
client.downloadFileTo("/images/photo.jpg", Path.of("./downloads/photo.jpg"));

// Get file info
FileInfo info = client.getFileInfo("/images/photo.jpg");

// Copy file
client.copyFile("/images/photo.jpg", "/backup/photo.jpg");

// Rename/move file
client.renameFile("/images/old-name.jpg", "/images/new-name.jpg");

// Delete file
client.deleteFile("/images/old-photo.jpg");

// Create folder
client.createFolder("/images/2024");
```

### Folder Operations

```java
// Read folder contents
FolderContents contents = client.readFolderContents("/images");
for (FileInfo item : contents.getContents()) {
    System.out.println(item.getFilename());
}

// With pagination
FolderContents contents = client.readFolderContents("/images");
while (contents.getContinuation() != null) {
    contents = client.readFolderContents("/images", contents.getContinuation());
    // Process contents
}

// Get/set folder options
Map<String, Object> options = client.getFolderOptions("/spins");
client.setFolderOptions("/spins", Map.of("scanSpins", true));
```

### Search

```java
// Search files
Map<String, Object> params = new HashMap<>();
params.put("query", "extension:(jpg OR png)");
params.put("size", 100);
SearchResult results = client.searchFiles(params);

// Paginated search
while (results.getScrollId() != null && !results.getHits().isEmpty()) {
    results = client.scrollSearch(results.getScrollId());
    // Process results
}
```

### Metadata

```java
// Get/set file metadata
FileMeta meta = client.getFileMeta("/images/photo.jpg");
meta.setTitle("Beautiful Sunset");
meta.setDescription("Photo taken at the beach");
client.setFileMeta("/images/photo.jpg", meta);

// Tags
client.addFileTags("/images/photo.jpg", Arrays.asList("nature", "sunset"));
client.removeFileTags("/images/photo.jpg", Arrays.asList("old-tag"));

// Product metadata
ProductMeta product = new ProductMeta();
product.setId("PROD-123");
product.setName("Blue Widget");
product.setSku("BW-001");
client.setProductMeta("/products/item.jpg", product);
```

### JWT Protected URLs

```java
Map<String, Object> params = new HashMap<>();
params.put("filename", "/protected/image.jpg");
params.put("expiresIn", 3600);
JwtResponse jwt = client.generateJwt(params);
System.out.println("Protected URL: " + jwt.getUrl());
```

### 360 Spin Operations

```java
// Convert spin to video
Map<String, Object> params = new HashMap<>();
params.put("filename", "/spins/product.spin");
params.put("options", Map.of("width", 1920, "height", 1080, "format", "mp4"));
Map<String, Object> result = client.spin2video(params);

// Export to marketplaces
client.exportSpinToAmazon("/spins/product.spin", "B08N5WRWNW");
client.exportSpinToWalmart("/spins/product.spin", "123456789");
```

### Statistics

```java
// HTTP transfer stats
Map<String, Object> httpStats = client.getHttpStats("2024-01-01", "2024-01-31");

// Storage stats
Map<String, Object> storageStats = client.getStorageStats("2024-01-01", "2024-01-31");

// Spin views stats (max 5-day range)
Map<String, Object> spinStats = client.getSpinViewsStats("2024-01-01", "2024-01-05");
```

## Error Handling

```java
import com.sirv.api.exception.SirvApiException;

try {
    client.connect();
    FileInfo info = client.getFileInfo("/nonexistent/file.jpg");
} catch (SirvApiException e) {
    System.err.println("API Error: " + e.getMessage());
    System.err.println("Status Code: " + e.getStatusCode());
    System.err.println("Error Code: " + e.getErrorCode());
}
```

## Configuration Options

```java
SirvClient client = new SirvClient(
    "your-client-id",       // Client ID
    "your-client-secret",   // Client Secret
    "https://api.sirv.com", // Base URL (default)
    30,                     // Timeout in seconds (default: 30)
    60,                     // Token refresh buffer in seconds (default: 60)
    3,                      // Max retries (default: 3)
    true                    // Auto-refresh token (default: true)
);
```

## Getting Your API Credentials

1. Log in to your [Sirv account](https://my.sirv.com/)
2. Go to **Settings** > **API**
3. Create a new API client or use existing credentials
4. Copy your `Client ID` and `Client Secret`

## Running Examples

```bash
# Set environment variables
export SIRV_CLIENT_ID="your-client-id"
export SIRV_CLIENT_SECRET="your-client-secret"

# Compile
mvn compile

# Run examples
mvn exec:java -Dexec.mainClass="com.sirv.api.examples.BasicUsageExample"
mvn exec:java -Dexec.mainClass="com.sirv.api.examples.FileOperationsExample"

# Or pass credentials as arguments
mvn exec:java -Dexec.mainClass="com.sirv.api.examples.BasicUsageExample" \
    -Dexec.args="your-client-id your-client-secret"
```

## Links

- [Sirv Website](https://sirv.com)
- [API Documentation](https://apidocs.sirv.com/)
- [Token Expiry Documentation](https://sirv.com/help/articles/sirv-rest-api/#change-token-expiry)
- [GitHub Repository](https://github.com/sirv/sirv-rest-api-java)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Support

- Email: support@sirv.com
- Documentation: https://sirv.com/help/
- API Docs: https://apidocs.sirv.com/
