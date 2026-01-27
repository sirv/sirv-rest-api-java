# Getting Started with Sirv Java SDK

This guide will help you get started with the Sirv REST API Java SDK.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher (for building)
- A Sirv account with API credentials

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

### Building from Source

```bash
git clone https://github.com/sirv/sirv-rest-api-java.git
cd sirv-rest-api-java
mvn clean install
```

## Getting API Credentials

1. Log in to your [Sirv account](https://my.sirv.com/)
2. Navigate to **Settings** > **API**
3. Click **Add new** to create a new API client
4. Copy your **Client ID** and **Client Secret**

## Quick Start

### Basic Connection

```java
import com.sirv.api.client.SirvClient;
import com.sirv.api.model.*;

public class QuickStart {
    public static void main(String[] args) {
        // Create client
        SirvClient client = new SirvClient(
            "your-client-id",
            "your-client-secret"
        );

        try {
            // Connect (authenticate)
            TokenResponse token = client.connect();
            System.out.println("Connected! Token expires in: " + token.getExpiresIn() + " seconds");

            // Get account info
            AccountInfo account = client.getAccountInfo();
            System.out.println("CDN URL: " + account.getCdnURL());

            // Get storage info
            StorageInfo storage = client.getStorageInfo();
            System.out.printf("Used: %.2f GB%n", storage.getUsed() / 1e9);

        } finally {
            client.close();
        }
    }
}
```

### Using Try-With-Resources (Recommended)

```java
try (SirvClient client = new SirvClient("your-client-id", "your-client-secret")) {
    client.connect();

    AccountInfo account = client.getAccountInfo();
    System.out.println("Account: " + account.getAlias());

} // Client is automatically closed
```

### Custom Token Expiry

Request a token with custom expiration (5 to 604800 seconds):

```java
try (SirvClient client = new SirvClient(clientId, clientSecret)) {
    // Request a token valid for 1 hour
    TokenResponse token = client.connect(3600);
    System.out.println("Token expires in: " + token.getExpiresIn() + " seconds");
}
```

## Configuration Options

The `SirvClient` constructor accepts several configuration options:

```java
SirvClient client = new SirvClient(
    "your-client-id",       // Required: API client ID
    "your-client-secret",   // Required: API client secret
    "https://api.sirv.com", // Base URL (default)
    30,                     // Request timeout in seconds (default: 30)
    60,                     // Token refresh buffer in seconds (default: 60)
    3,                      // Max retries for failed requests (default: 3)
    true                    // Auto-refresh token before expiry (default: true)
);
```

## Error Handling

The SDK throws `SirvApiException` for API errors:

```java
import com.sirv.api.exception.SirvApiException;

try {
    FileInfo info = client.getFileInfo("/nonexistent-file.jpg");
} catch (SirvApiException e) {
    System.err.println("Error: " + e.getMessage());
    System.err.println("Status Code: " + e.getStatusCode());
    System.err.println("Error Code: " + e.getErrorCode());
}
```

## Environment Variables

You can use environment variables for credentials:

```java
String clientId = System.getenv("SIRV_CLIENT_ID");
String clientSecret = System.getenv("SIRV_CLIENT_SECRET");

try (SirvClient client = new SirvClient(clientId, clientSecret)) {
    client.connect();
    // ...
}
```

Set them in your shell:

```bash
export SIRV_CLIENT_ID="your-client-id"
export SIRV_CLIENT_SECRET="your-client-secret"
```

## Next Steps

- [File Operations Guide](file-operations.md) - Upload, download, and manage files
- [Search Guide](search-guide.md) - Search and filter files
- [Metadata Guide](metadata-guide.md) - Manage file metadata
- [Statistics Guide](statistics-guide.md) - Access usage statistics
- [360 Spin Guide](spin-operations.md) - Work with 360 spins
