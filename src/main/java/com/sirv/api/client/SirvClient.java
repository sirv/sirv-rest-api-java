package com.sirv.api.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sirv.api.exception.SirvApiException;
import com.sirv.api.model.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Sirv REST API Client.
 *
 * <p>A comprehensive Java client for the Sirv REST API, providing access to
 * all API endpoints for managing images, 360 spins, and digital assets.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SirvClient client = new SirvClient("your-client-id", "your-client-secret");
 * client.connect();
 *
 * AccountInfo account = client.getAccountInfo();
 * System.out.println("CDN URL: " + account.getCdnURL());
 *
 * client.close();
 * }</pre>
 *
 * <p>Or with try-with-resources:</p>
 * <pre>{@code
 * try (SirvClient client = new SirvClient("your-client-id", "your-client-secret")) {
 *     client.connect();
 *     AccountInfo account = client.getAccountInfo();
 * }
 * }</pre>
 */
public class SirvClient implements AutoCloseable {

    private static final String DEFAULT_BASE_URL = "https://api.sirv.com";
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int DEFAULT_TOKEN_REFRESH_BUFFER = 60;
    private static final int DEFAULT_MAX_RETRIES = 3;

    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;
    private final int timeout;
    private final int tokenRefreshBuffer;
    private final int maxRetries;
    private final boolean autoRefreshToken;

    private final OkHttpClient httpClient;
    private final Gson gson;

    private String token;
    private long tokenExpiry;

    /**
     * Creates a new SirvClient with default settings.
     *
     * @param clientId     Your Sirv API client ID
     * @param clientSecret Your Sirv API client secret
     */
    public SirvClient(String clientId, String clientSecret) {
        this(clientId, clientSecret, DEFAULT_BASE_URL, DEFAULT_TIMEOUT,
                DEFAULT_TOKEN_REFRESH_BUFFER, DEFAULT_MAX_RETRIES, true);
    }

    /**
     * Creates a new SirvClient with custom settings.
     *
     * @param clientId           Your Sirv API client ID
     * @param clientSecret       Your Sirv API client secret
     * @param baseUrl            Base URL for the API
     * @param timeout            Request timeout in seconds
     * @param tokenRefreshBuffer Seconds before expiry to refresh token
     * @param maxRetries         Maximum retries for failed requests
     * @param autoRefreshToken   Whether to auto-refresh tokens
     */
    public SirvClient(String clientId, String clientSecret, String baseUrl,
                      int timeout, int tokenRefreshBuffer, int maxRetries,
                      boolean autoRefreshToken) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("clientId is required");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalArgumentException("clientSecret is required");
        }

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.timeout = timeout;
        this.tokenRefreshBuffer = tokenRefreshBuffer;
        this.maxRetries = maxRetries;
        this.autoRefreshToken = autoRefreshToken;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        this.gson = new GsonBuilder().create();
    }

    // ============================================================================
    // Authentication
    // ============================================================================

    /**
     * Authenticates and obtains a bearer token.
     *
     * @return Token response with token, expiration, and scopes
     * @throws SirvApiException If authentication fails
     */
    public TokenResponse connect() {
        return connect(null);
    }

    /**
     * Authenticates and obtains a bearer token with custom expiry.
     *
     * @param expiresIn Optional token expiry time in seconds (5-604800)
     * @return Token response with token, expiration, and scopes
     * @throws SirvApiException If authentication fails
     */
    public TokenResponse connect(Integer expiresIn) {
        if (expiresIn != null && (expiresIn < 5 || expiresIn > 604800)) {
            throw new IllegalArgumentException("expiresIn must be between 5 and 604800 seconds");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("clientId", clientId);
        payload.put("clientSecret", clientSecret);
        if (expiresIn != null) {
            payload.put("expiresIn", expiresIn);
        }

        String json = gson.toJson(payload);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + "/v2/token")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new SirvApiException("Authentication failed", response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            TokenResponse tokenResponse = gson.fromJson(responseBody, TokenResponse.class);

            this.token = tokenResponse.getToken();
            this.tokenExpiry = System.currentTimeMillis() / 1000 + tokenResponse.getExpiresIn();

            return tokenResponse;
        } catch (IOException e) {
            throw new SirvApiException("Authentication request failed", 0, e);
        }
    }

    /**
     * Checks if the client is connected with a valid token.
     *
     * @return true if connected with a valid token
     */
    public boolean isConnected() {
        return token != null && !isTokenExpired();
    }

    /**
     * Gets the current access token.
     *
     * @return The current token or null if not connected
     */
    public String getAccessToken() {
        return token;
    }

    private boolean isTokenExpired() {
        int buffer = autoRefreshToken ? tokenRefreshBuffer : 0;
        return System.currentTimeMillis() / 1000 >= (tokenExpiry - buffer);
    }

    private String getValidToken() {
        if (token == null || isTokenExpired()) {
            connect();
        }
        return token;
    }

    // ============================================================================
    // HTTP Request Helpers
    // ============================================================================

    private <T> T request(String method, String endpoint, Map<String, String> queryParams,
                          Object requestBody, Class<T> responseType) {
        return request(method, endpoint, queryParams, requestBody, responseType, 0);
    }

    private <T> T request(String method, String endpoint, Map<String, String> queryParams,
                          Object requestBody, Class<T> responseType, int retryCount) {
        String validToken = getValidToken();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Authorization", "Bearer " + validToken)
                .addHeader("Content-Type", "application/json");

        RequestBody body = null;
        if (requestBody != null) {
            String json = gson.toJson(requestBody);
            body = RequestBody.create(json, MediaType.parse("application/json"));
        }

        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(body != null ? body : RequestBody.create("", null));
                break;
            case "DELETE":
                if (body != null) {
                    requestBuilder.delete(body);
                } else {
                    requestBuilder.delete();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                String errorMessage = responseBody;
                String errorCode = null;
                try {
                    Map<String, Object> errorData = gson.fromJson(responseBody,
                            new TypeToken<Map<String, Object>>() {}.getType());
                    if (errorData != null) {
                        errorMessage = (String) errorData.getOrDefault("message", responseBody);
                        errorCode = (String) errorData.get("error");
                    }
                } catch (Exception ignored) {}

                throw new SirvApiException(errorMessage, response.code(), errorCode);
            }

            if (responseType == null || responseType == Void.class) {
                return null;
            }

            if (responseType == String.class) {
                return responseType.cast(responseBody);
            }

            return gson.fromJson(responseBody, responseType);
        } catch (SirvApiException e) {
            throw e;
        } catch (IOException e) {
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return request(method, endpoint, queryParams, requestBody, responseType, retryCount + 1);
            }
            throw new SirvApiException("Request failed: " + e.getMessage(), 0, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T requestWithType(String method, String endpoint, Map<String, String> queryParams,
                                   Object requestBody, Type responseType) {
        String validToken = getValidToken();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Authorization", "Bearer " + validToken)
                .addHeader("Content-Type", "application/json");

        RequestBody body = null;
        if (requestBody != null) {
            String json = gson.toJson(requestBody);
            body = RequestBody.create(json, MediaType.parse("application/json"));
        }

        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(body != null ? body : RequestBody.create("", null));
                break;
            case "DELETE":
                if (body != null) {
                    requestBuilder.delete(body);
                } else {
                    requestBuilder.delete();
                }
                break;
        }

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                String errorMessage = responseBody;
                try {
                    Map<String, Object> errorData = gson.fromJson(responseBody,
                            new TypeToken<Map<String, Object>>() {}.getType());
                    if (errorData != null && errorData.containsKey("message")) {
                        errorMessage = (String) errorData.get("message");
                    }
                } catch (Exception ignored) {}

                throw new SirvApiException(errorMessage, response.code());
            }

            return gson.fromJson(responseBody, responseType);
        } catch (SirvApiException e) {
            throw e;
        } catch (IOException e) {
            throw new SirvApiException("Request failed: " + e.getMessage(), 0, e);
        }
    }

    // ============================================================================
    // Account API
    // ============================================================================

    /**
     * Gets account information.
     *
     * @return Account information including CDN URL, alias, and settings
     */
    public AccountInfo getAccountInfo() {
        return request("GET", "/v2/account", null, null, AccountInfo.class);
    }

    /**
     * Updates account settings.
     *
     * @param options Account settings to update
     */
    public void updateAccount(Map<String, Object> options) {
        request("POST", "/v2/account", null, options, Void.class);
    }

    /**
     * Gets API rate limits.
     *
     * @return Rate limit information for S3, REST, and FTP APIs
     */
    public AccountLimits getAccountLimits() {
        return request("GET", "/v2/account/limits", null, null, AccountLimits.class);
    }

    /**
     * Gets storage usage information.
     *
     * @return Storage usage details
     */
    public StorageInfo getStorageInfo() {
        return request("GET", "/v2/account/storage", null, null, StorageInfo.class);
    }

    /**
     * Gets all account users.
     *
     * @return List of account users
     */
    public List<UserInfo> getAccountUsers() {
        Type listType = new TypeToken<List<UserInfo>>() {}.getType();
        return requestWithType("GET", "/v2/account/users", null, null, listType);
    }

    /**
     * Gets billing plan details.
     *
     * @return Current billing plan information
     */
    public BillingPlan getBillingPlan() {
        return request("GET", "/v2/billing/plan", null, null, BillingPlan.class);
    }

    /**
     * Searches account events.
     *
     * @param params Search parameters
     * @return List of matching account events
     */
    public List<Map<String, Object>> searchEvents(Map<String, Object> params) {
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        return requestWithType("POST", "/v2/account/events/search", null, params, listType);
    }

    /**
     * Marks events as seen.
     *
     * @param eventIds List of event IDs to mark as seen
     */
    public void markEventsSeen(List<String> eventIds) {
        request("POST", "/v2/account/events/seen", null, eventIds, Void.class);
    }

    // ============================================================================
    // User API
    // ============================================================================

    /**
     * Gets user information.
     *
     * @return Current user information
     */
    public UserInfo getUserInfo() {
        return getUserInfo(null);
    }

    /**
     * Gets user information for a specific user.
     *
     * @param userId User ID (optional, defaults to current user)
     * @return User information
     */
    public UserInfo getUserInfo(String userId) {
        Map<String, String> params = userId != null ? Collections.singletonMap("userId", userId) : null;
        return request("GET", "/v2/user", params, null, UserInfo.class);
    }

    // ============================================================================
    // Files API - Reading
    // ============================================================================

    /**
     * Gets file information.
     *
     * @param filename Path to the file on Sirv
     * @return File information
     */
    public FileInfo getFileInfo(String filename) {
        return request("GET", "/v2/files/stat",
                Collections.singletonMap("filename", filename), null, FileInfo.class);
    }

    /**
     * Reads folder contents.
     *
     * @param dirname Path to the directory
     * @return Folder contents
     */
    public FolderContents readFolderContents(String dirname) {
        return readFolderContents(dirname, null);
    }

    /**
     * Reads folder contents with pagination.
     *
     * @param dirname      Path to the directory
     * @param continuation Continuation token for pagination
     * @return Folder contents
     */
    public FolderContents readFolderContents(String dirname, String continuation) {
        Map<String, String> params = new HashMap<>();
        params.put("dirname", dirname);
        if (continuation != null) {
            params.put("continuation", continuation);
        }
        return request("GET", "/v2/files/readdir", params, null, FolderContents.class);
    }

    /**
     * Gets folder options.
     *
     * @param dirname Path to the directory
     * @return Folder options
     */
    public Map<String, Object> getFolderOptions(String dirname) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("GET", "/v2/files/options",
                Collections.singletonMap("dirname", dirname), null, mapType);
    }

    /**
     * Sets folder options.
     *
     * @param dirname Path to the directory
     * @param options Folder options to set
     */
    public void setFolderOptions(String dirname, Map<String, Object> options) {
        Map<String, Object> data = new HashMap<>(options);
        data.put("dirname", dirname);
        request("POST", "/v2/files/options", null, data, Void.class);
    }

    /**
     * Searches files.
     *
     * @param params Search parameters
     * @return Search results
     */
    public SearchResult searchFiles(Map<String, Object> params) {
        return request("POST", "/v2/files/search", null, params, SearchResult.class);
    }

    /**
     * Continues paginated search.
     *
     * @param scrollId Scroll ID from previous search
     * @return Next page of search results
     */
    public SearchResult scrollSearch(String scrollId) {
        return request("POST", "/v2/files/search/scroll", null,
                Collections.singletonMap("scrollId", scrollId), SearchResult.class);
    }

    /**
     * Downloads a file.
     *
     * @param filename Path to the file on Sirv
     * @return File content as bytes
     */
    public byte[] downloadFile(String filename) {
        String validToken = getValidToken();

        HttpUrl url = HttpUrl.parse(baseUrl + "/v2/files/download").newBuilder()
                .addQueryParameter("filename", filename)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + validToken)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new SirvApiException("Download failed", response.code());
            }
            return response.body() != null ? response.body().bytes() : new byte[0];
        } catch (IOException e) {
            throw new SirvApiException("Download failed: " + e.getMessage(), 0, e);
        }
    }

    /**
     * Downloads a file to a local path.
     *
     * @param filename  Path to the file on Sirv
     * @param localPath Local path to save the file
     */
    public void downloadFileTo(String filename, Path localPath) {
        byte[] content = downloadFile(filename);
        try {
            Files.write(localPath, content);
        } catch (IOException e) {
            throw new SirvApiException("Failed to write file: " + e.getMessage(), 0, e);
        }
    }

    // ============================================================================
    // Files API - Writing
    // ============================================================================

    /**
     * Uploads a file from bytes.
     *
     * @param targetPath  Target path on Sirv
     * @param content     File content
     * @param contentType Content type (e.g., "image/jpeg")
     */
    public void uploadFile(String targetPath, byte[] content, String contentType) {
        String validToken = getValidToken();

        HttpUrl url = HttpUrl.parse(baseUrl + "/v2/files/upload").newBuilder()
                .addQueryParameter("filename", targetPath)
                .build();

        RequestBody body = RequestBody.create(content,
                MediaType.parse(contentType != null ? contentType : "application/octet-stream"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + validToken)
                .addHeader("Content-Type", contentType != null ? contentType : "application/octet-stream")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new SirvApiException("Upload failed: " + errorBody, response.code());
            }
        } catch (IOException e) {
            throw new SirvApiException("Upload failed: " + e.getMessage(), 0, e);
        }
    }

    /**
     * Uploads a file from a local path.
     *
     * @param targetPath Target path on Sirv
     * @param localPath  Local file path
     */
    public void uploadFile(String targetPath, Path localPath) {
        try {
            byte[] content = Files.readAllBytes(localPath);
            String contentType = Files.probeContentType(localPath);
            uploadFile(targetPath, content, contentType);
        } catch (IOException e) {
            throw new SirvApiException("Failed to read file: " + e.getMessage(), 0, e);
        }
    }

    /**
     * Creates a new folder.
     *
     * @param dirname Path for the new directory
     */
    public void createFolder(String dirname) {
        request("POST", "/v2/files/mkdir",
                Collections.singletonMap("dirname", dirname), null, Void.class);
    }

    /**
     * Deletes a file or empty folder.
     *
     * @param filename Path to the file or folder
     */
    public void deleteFile(String filename) {
        request("POST", "/v2/files/delete",
                Collections.singletonMap("filename", filename), null, Void.class);
    }

    /**
     * Deletes multiple files/folders.
     *
     * @param filenames List of file paths to delete
     * @return Batch delete result
     */
    public BatchResult batchDelete(List<String> filenames) {
        return request("POST", "/v2/files/delete", null, filenames, BatchResult.class);
    }

    /**
     * Gets batch delete job status.
     *
     * @param jobId Job ID from batch delete operation
     * @return Current status of the batch delete job
     */
    public BatchResult getBatchDeleteStatus(String jobId) {
        return request("GET", "/v2/files/delete/" + jobId, null, null, BatchResult.class);
    }

    /**
     * Copies a file.
     *
     * @param fromPath Source file path
     * @param toPath   Destination file path
     */
    public void copyFile(String fromPath, String toPath) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromPath);
        params.put("to", toPath);
        request("POST", "/v2/files/copy", params, null, Void.class);
    }

    /**
     * Renames or moves a file/folder.
     *
     * @param fromPath Current file path
     * @param toPath   New file path
     */
    public void renameFile(String fromPath, String toPath) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromPath);
        params.put("to", toPath);
        request("POST", "/v2/files/rename", params, null, Void.class);
    }

    /**
     * Fetches file from external URL.
     *
     * @param url      Source URL to fetch from
     * @param filename Target filename on Sirv
     * @param wait     Whether to wait for completion
     */
    public void fetchUrl(String url, String filename, boolean wait) {
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("filename", filename);
        data.put("wait", wait);
        request("POST", "/v2/files/fetch", null, data, Void.class);
    }

    /**
     * Creates ZIP archive from multiple files.
     *
     * @param filenames List of files to include
     * @param zipPath   Output ZIP filename
     * @return Batch ZIP result
     */
    public BatchResult batchZip(List<String> filenames, String zipPath) {
        Map<String, Object> data = new HashMap<>();
        data.put("filenames", filenames);
        data.put("filename", zipPath);
        return request("POST", "/v2/files/zip", null, data, BatchResult.class);
    }

    /**
     * Gets ZIP job status.
     *
     * @param jobId Job ID from batch ZIP operation
     * @return Current status of the ZIP job
     */
    public BatchResult getZipStatus(String jobId) {
        return request("GET", "/v2/files/zip/" + jobId, null, null, BatchResult.class);
    }

    // ============================================================================
    // Metadata API
    // ============================================================================

    /**
     * Gets all file metadata.
     *
     * @param filename Path to the file
     * @return File metadata
     */
    public FileMeta getFileMeta(String filename) {
        return request("GET", "/v2/files/meta",
                Collections.singletonMap("filename", filename), null, FileMeta.class);
    }

    /**
     * Sets file metadata.
     *
     * @param filename Path to the file
     * @param meta     Metadata to set
     */
    public void setFileMeta(String filename, FileMeta meta) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        if (meta.getTitle() != null) data.put("title", meta.getTitle());
        if (meta.getDescription() != null) data.put("description", meta.getDescription());
        if (meta.getTags() != null) data.put("tags", meta.getTags());
        if (meta.getApproved() != null) data.put("approved", meta.getApproved());
        request("POST", "/v2/files/meta", null, data, Void.class);
    }

    /**
     * Gets file title.
     *
     * @param filename Path to the file
     * @return Map containing the title
     */
    public Map<String, String> getFileTitle(String filename) {
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return requestWithType("GET", "/v2/files/meta/title",
                Collections.singletonMap("filename", filename), null, mapType);
    }

    /**
     * Sets file title.
     *
     * @param filename Path to the file
     * @param title    New title
     */
    public void setFileTitle(String filename, String title) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("title", title);
        request("POST", "/v2/files/meta/title", null, data, Void.class);
    }

    /**
     * Gets file description.
     *
     * @param filename Path to the file
     * @return Map containing the description
     */
    public Map<String, String> getFileDescription(String filename) {
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return requestWithType("GET", "/v2/files/meta/description",
                Collections.singletonMap("filename", filename), null, mapType);
    }

    /**
     * Sets file description.
     *
     * @param filename    Path to the file
     * @param description New description
     */
    public void setFileDescription(String filename, String description) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("description", description);
        request("POST", "/v2/files/meta/description", null, data, Void.class);
    }

    /**
     * Gets file tags.
     *
     * @param filename Path to the file
     * @return Map containing the tags list
     */
    public Map<String, List<String>> getFileTags(String filename) {
        Type mapType = new TypeToken<Map<String, List<String>>>() {}.getType();
        return requestWithType("GET", "/v2/files/meta/tags",
                Collections.singletonMap("filename", filename), null, mapType);
    }

    /**
     * Adds tags to file.
     *
     * @param filename Path to the file
     * @param tags     Tags to add
     */
    public void addFileTags(String filename, List<String> tags) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        data.put("tags", tags);
        request("POST", "/v2/files/meta/tags", null, data, Void.class);
    }

    /**
     * Removes tags from file.
     *
     * @param filename Path to the file
     * @param tags     Tags to remove
     */
    public void removeFileTags(String filename, List<String> tags) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        data.put("tags", tags);
        request("DELETE", "/v2/files/meta/tags", null, data, Void.class);
    }

    /**
     * Gets product metadata.
     *
     * @param filename Path to the file
     * @return Product metadata
     */
    public ProductMeta getProductMeta(String filename) {
        return request("GET", "/v2/files/meta/product",
                Collections.singletonMap("filename", filename), null, ProductMeta.class);
    }

    /**
     * Sets product metadata.
     *
     * @param filename Path to the file
     * @param meta     Product metadata to set
     */
    public void setProductMeta(String filename, ProductMeta meta) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        if (meta.getId() != null) data.put("id", meta.getId());
        if (meta.getName() != null) data.put("name", meta.getName());
        if (meta.getBrand() != null) data.put("brand", meta.getBrand());
        if (meta.getCategory() != null) data.put("category", meta.getCategory());
        if (meta.getSku() != null) data.put("sku", meta.getSku());
        request("POST", "/v2/files/meta/product", null, data, Void.class);
    }

    /**
     * Gets approval flag.
     *
     * @param filename Path to the file
     * @return Map containing the approved status
     */
    public Map<String, Boolean> getApprovalFlag(String filename) {
        Type mapType = new TypeToken<Map<String, Boolean>>() {}.getType();
        return requestWithType("GET", "/v2/files/meta/approval",
                Collections.singletonMap("filename", filename), null, mapType);
    }

    /**
     * Sets approval flag.
     *
     * @param filename Path to the file
     * @param approved Approval status
     */
    public void setApprovalFlag(String filename, boolean approved) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        data.put("approved", approved);
        request("POST", "/v2/files/meta/approval", null, data, Void.class);
    }

    // ============================================================================
    // JWT API
    // ============================================================================

    /**
     * Generates JWT protected URL.
     *
     * @param params JWT parameters
     * @return JWT response with URL and token
     */
    public JwtResponse generateJwt(Map<String, Object> params) {
        return request("POST", "/v2/files/jwt", null, params, JwtResponse.class);
    }

    // ============================================================================
    // Spins/360 API
    // ============================================================================

    /**
     * Converts spin to video.
     *
     * @param params Conversion parameters
     * @return Result with output filename
     */
    public Map<String, Object> spin2video(Map<String, Object> params) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("POST", "/v2/files/spin2video", null, params, mapType);
    }

    /**
     * Converts video to spin.
     *
     * @param params Conversion parameters
     * @return Result with output filename
     */
    public Map<String, Object> video2spin(Map<String, Object> params) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("POST", "/v2/files/video2spin", null, params, mapType);
    }

    /**
     * Exports spin to Amazon.
     *
     * @param filename Spin filename
     * @param asin     Amazon ASIN
     */
    public void exportSpinToAmazon(String filename, String asin) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("asin", asin);
        request("POST", "/v2/files/spin/export/amazon", null, data, Void.class);
    }

    /**
     * Exports spin to Walmart.
     *
     * @param filename  Spin filename
     * @param productId Walmart product ID
     */
    public void exportSpinToWalmart(String filename, String productId) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("productId", productId);
        request("POST", "/v2/files/spin/export/walmart", null, data, Void.class);
    }

    /**
     * Exports spin to Home Depot.
     *
     * @param filename  Spin filename
     * @param productId Home Depot product ID
     */
    public void exportSpinToHomeDepot(String filename, String productId) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("productId", productId);
        request("POST", "/v2/files/spin/export/homedepot", null, data, Void.class);
    }

    /**
     * Exports spin to Lowe's.
     *
     * @param filename  Spin filename
     * @param productId Lowe's product ID
     */
    public void exportSpinToLowes(String filename, String productId) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("productId", productId);
        request("POST", "/v2/files/spin/export/lowes", null, data, Void.class);
    }

    /**
     * Exports spin to Grainger.
     *
     * @param filename  Spin filename
     * @param productId Grainger product ID
     */
    public void exportSpinToGrainger(String filename, String productId) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("productId", productId);
        request("POST", "/v2/files/spin/export/grainger", null, data, Void.class);
    }

    // ============================================================================
    // Points of Interest API
    // ============================================================================

    /**
     * Gets points of interest for a file.
     *
     * @param filename Path to the spin file
     * @return List of points of interest
     */
    public List<PointOfInterest> getPointsOfInterest(String filename) {
        Type listType = new TypeToken<List<PointOfInterest>>() {}.getType();
        return requestWithType("GET", "/v2/files/poi",
                Collections.singletonMap("filename", filename), null, listType);
    }

    /**
     * Sets point of interest.
     *
     * @param filename Path to the spin file
     * @param poi      Point of interest data
     */
    public void setPointOfInterest(String filename, PointOfInterest poi) {
        Map<String, Object> data = new HashMap<>();
        data.put("filename", filename);
        data.put("name", poi.getName());
        data.put("x", poi.getX());
        data.put("y", poi.getY());
        if (poi.getFrame() != null) data.put("frame", poi.getFrame());
        request("POST", "/v2/files/poi", null, data, Void.class);
    }

    /**
     * Deletes point of interest.
     *
     * @param filename Path to the spin file
     * @param name     Name of the POI to delete
     */
    public void deletePointOfInterest(String filename, String name) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("name", name);
        request("DELETE", "/v2/files/poi", null, data, Void.class);
    }

    // ============================================================================
    // Statistics API
    // ============================================================================

    /**
     * Gets HTTP transfer statistics.
     *
     * @param fromDate Start date (ISO format, e.g., "2024-01-01")
     * @param toDate   End date (ISO format)
     * @return HTTP statistics as a map with timestamps as keys
     */
    public Map<String, Object> getHttpStats(String fromDate, String toDate) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromDate);
        params.put("to", toDate);
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("GET", "/v2/stats/http", params, null, mapType);
    }

    /**
     * Gets spin views statistics (max 5-day range).
     *
     * @param fromDate Start date (ISO format)
     * @param toDate   End date (ISO format)
     * @return Spin view statistics
     */
    public Map<String, Object> getSpinViewsStats(String fromDate, String toDate) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromDate);
        params.put("to", toDate);
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("GET", "/v2/stats/spins/views", params, null, mapType);
    }

    /**
     * Gets storage statistics.
     *
     * @param fromDate Start date (ISO format)
     * @param toDate   End date (ISO format)
     * @return Storage statistics as a map with timestamps as keys
     */
    public Map<String, Object> getStorageStats(String fromDate, String toDate) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromDate);
        params.put("to", toDate);
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return requestWithType("GET", "/v2/stats/storage", params, null, mapType);
    }

    // ============================================================================
    // Resource Management
    // ============================================================================

    /**
     * Closes the client and releases resources.
     */
    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
