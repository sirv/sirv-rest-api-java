package com.sirv.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.sirv.RestClient;
import com.sirv.SirvClient;
import com.sirv.exception.UnauthorizedRestClientException;
import com.sirv.files.model.ApprovalFlag;
import com.sirv.files.model.Content;
import com.sirv.files.model.ConvertSpinToVideo;
import com.sirv.files.model.ConvertVideoToSpin;
import com.sirv.files.model.Fetch;
import com.sirv.files.model.FilesSearchRequest;
import com.sirv.files.model.FolderContent;
import com.sirv.files.model.FolderOptions;
import com.sirv.files.model.GetJWTProtectedUrlRequest;
import com.sirv.files.model.JwtProtectedUrl;
import com.sirv.files.model.MetaDescription;
import com.sirv.files.model.MetaTags;
import com.sirv.files.model.MetaTitle;
import com.sirv.files.model.ProductMeta;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FilesClientImpl implements FilesClient {
    private static final String FILENAME = "filename";
    private static final String DIRNAME = "dirname";
    private static final String CONTINUATION = "continuation";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String SCROLL_ID = "scrollId";

    private String host;
    private String approvalFlagUrl;
    private String metaDescriptionUrl;
    private String productMetaUrl;
    private String metaTagsUrl;
    private String metaTitleUrl;
    private String readdirUrl;
    private String folderOptionsUrl;
    private String filesStatUrl;
    private String copyUrl;
    private String deleteUrl;
    private String fetchUrl;
    private String jwtProtectedUrl;
    private String searchUrl;
    private String scrollUrl;
    private String convertSpinToVideoUrl;
    private String uploadUrl;
    private String mkdirUrl;
    private String renameUrl;
    private String convertVideoToSpinUrl;

    private SirvClient sirvClient;
    private RestClient restClient;

    public FilesClientImpl(SirvClient sirvClient, RestClient restClient, String host) {
        this.sirvClient = sirvClient;
        this.restClient = restClient;
        this.host = host;
        initUrls();
    }

    private void initUrls() {
        approvalFlagUrl = host + "/v2/files/meta/approval";
        metaDescriptionUrl = host + "/v2/files/meta/description";
        productMetaUrl = host + "/v2/files/meta/product";
        metaTagsUrl = host + "/v2/files/meta/tags";
        metaTitleUrl = host + "/v2/files/meta/title";
        folderOptionsUrl = host + "/v2/files/options";
        readdirUrl = host + "/v2/files/readdir";
        filesStatUrl = host + "/v2/files/stat";
        copyUrl = host + "/v2/files/copy";
        deleteUrl = host + "/v2/files/delete";
        fetchUrl = host + "/v2/files/fetch";
        jwtProtectedUrl = host + "/v2/files/jwt";
        searchUrl = host + "/v2/files/search";
        scrollUrl = host + "/v2/files/search/scroll";
        convertSpinToVideoUrl = host + "/v2/files/spin2video";
        uploadUrl = host + "/v2/files/upload";
        mkdirUrl = host + "/v2/files/mkdir";
        renameUrl = host + "/v2/files/rename";
        convertVideoToSpinUrl = host + "/v2/files/video2spin";
    }


    @Override
    public ApprovalFlag getApprovalFlag(String filename) {
        return getByFileName(approvalFlagUrl, filename, ApprovalFlag.class);
    }

    @Override
    public void setApprovalFlag(String filename, ApprovalFlag approvalFlag) {
        setByFileName(approvalFlagUrl, filename, approvalFlag);
    }

    @Override
    public MetaDescription getMetaDescription(String filename) {
        return getByFileName(metaDescriptionUrl, filename, MetaDescription.class);
    }

    @Override
    public void setMetaDescription(String filename, MetaDescription metaDescription) {
        setByFileName(metaDescriptionUrl, filename, metaDescription);
    }

    @Override
    public ProductMeta setProductMeta(String filename) {
        return getByFileName(productMetaUrl, filename, ProductMeta.class);
    }

    @Override
    public void setProductMeta(String filename, ProductMeta meta) {
        setByFileName(productMetaUrl, filename, meta);
    }

    @Override
    public MetaTags getMetaTags(String filename) {
        return getByFileName(metaTagsUrl, filename, MetaTags.class);
    }

    @Override
    public void setMetaTags(String filename, MetaTags metaTags) {
        setByFileName(metaTagsUrl, filename, metaTags);
    }

    @Override
    public MetaTitle getMetaTitle(String filename) {
        return getByFileName(metaTitleUrl, filename, MetaTitle.class);
    }

    @Override
    public void setMetaTitle(String filename, MetaTitle metaTitle) {
        setByFileName(metaTitleUrl, filename, metaTitle);
    }

    @Override
    public FolderOptions getFolderOptions(String folder) {
        return getByFileName(folderOptionsUrl, folder, FolderOptions.class);
    }

    @Override
    public void setFolderOptions(String folder, FolderOptions folderOptions) {
        setByFileName(folderOptionsUrl, folder, folderOptions);
    }

    @Override
    public FolderContent readFolderContents(String folder, String continuation) {
        return doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(DIRNAME, folder);
            queryParams.put(CONTINUATION, continuation);

            return restClient.doGet(readdirUrl, FolderContent.class, h, queryParams);
        });
    }

    @Override
    public Content getContentInfo(String path) {
        return getByFileName(filesStatUrl, path, Content.class);
    }

    @Override
    public void copy(String from, String to) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FROM, from);
            queryParams.put(TO, to);

            return restClient.doPost(copyUrl, null, String.class, h, queryParams);
        });
    }

    @Override
    public void delete(String path) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FILENAME, path);

            return restClient.doPost(deleteUrl, null, String.class, h, queryParams);
        });
    }

    @Override
    public void fetch(List<Fetch> fetches) {
        doRequestWithRetry((h) -> restClient.doPost(fetchUrl, fetches, String.class, h));
    }

    @Override
    public JwtProtectedUrl getJwtProtectedUrl(GetJWTProtectedUrlRequest request) {
        return doRequestWithRetry((h) -> restClient.doPost(jwtProtectedUrl, request, JwtProtectedUrl.class, h));
    }

    @Override
    public JsonNode searchFiles(FilesSearchRequest request) {
        return doRequestWithRetry((h) -> restClient.doPost(searchUrl, request, JsonNode.class, h));
    }

    @Override
    public JsonNode scroll(String scrollId) {
        return doRequestWithRetry((h) -> {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put(SCROLL_ID, scrollId);

            return restClient.doPost(scrollUrl, requestBody, JsonNode.class, h);
        });
    }

    @Override
    public String convertSpinToVideo(ConvertSpinToVideo convertSpinToVideo) {
        return doRequestWithRetry((h) -> Optional.ofNullable(restClient.doPost(convertSpinToVideoUrl, convertSpinToVideo, JsonNode.class, h))
                .map(n -> n.get(FILENAME)).map(JsonNode::textValue).orElse(null));
    }

    @Override
    public void upload(String filename, InputStream inputStream) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(FILENAME, filename);

        byte[] data = new byte[inputStream.available()];
        inputStream.read(data);

        doRequestWithRetry((h) -> restClient.doPost(uploadUrl, data, String.class, h, queryParams));
    }

    @Override
    public void mkdir(String dirname) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(DIRNAME, dirname);

            return restClient.doPost(mkdirUrl, null, String.class, h, queryParams);
        });
    }

    @Override
    public void rename(String from, String to) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FROM, from);
            queryParams.put(TO, to);

            return restClient.doPost(renameUrl, null, String.class, h, queryParams);
        });
    }

    @Override
    public String convertVideoToSpin(ConvertVideoToSpin convertVideoToSpin) {
        return doRequestWithRetry((h) -> Optional.ofNullable(restClient.doPost(convertVideoToSpinUrl, convertVideoToSpin, JsonNode.class, h))
                .map(n -> n.get(FILENAME)).map(JsonNode::textValue).orElse(null));
    }

    @Override
    public void deleteMetaTags(String filename, MetaTags metaTags) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FILENAME, filename);

            return restClient.doDelete(metaTagsUrl, metaTags, String.class, h, queryParams);
        });
    }

    private <T> T getByFileName(String url, String filename, Class<T> responseType) {
        return doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FILENAME, filename);

            return restClient.doGet(url, responseType, h, queryParams);
        });
    }


    private <T> void setByFileName(String url, String filename, T requestBody) {
        doRequestWithRetry((h) -> {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(FILENAME, filename);

            return restClient.doPost(url, requestBody, String.class, h, queryParams);
        });
    }

    private <T> T doRequestWithRetry(Function<Map<String, String>, T> supplier) {
        try {
            return supplier.apply(sirvClient.getRequestHeaders(false));
        } catch (UnauthorizedRestClientException e) {
            return supplier.apply(sirvClient.getRequestHeaders(true));
        }
    }
}
