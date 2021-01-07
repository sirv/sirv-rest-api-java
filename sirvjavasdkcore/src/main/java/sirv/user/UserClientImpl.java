package com.sirv.user;

import com.sirv.RestClient;
import com.sirv.SirvClient;
import com.sirv.exception.UnauthorizedRestClientException;
import com.sirv.user.model.UserInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UserClientImpl implements UserClient {
    private static final String USER_ID = "userId";
    private String host;
    private String informationUrl;
    private SirvClient sirvClient;
    private RestClient restClient;

    public UserClientImpl(SirvClient sirvClient, RestClient restClient, String host) {
        this.sirvClient = sirvClient;
        this.restClient = restClient;
        this.host = host;
        initUrls();
    }

    private void initUrls() {
        informationUrl = host + "/v2/user";
    }

    @Override
    public UserInformation getUserInformation(String userId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(USER_ID, userId);

        return doRequestWithRetry((h) -> restClient.doGet(informationUrl,  UserInformation.class, h, queryParams));
    }

    private <T> T doRequestWithRetry(Function<Map<String, String>, T> supplier) {
        try {
            return supplier.apply(sirvClient.getRequestHeaders(false));
        } catch (UnauthorizedRestClientException e) {
            return supplier.apply(sirvClient.getRequestHeaders(true));
        }
    }
}
