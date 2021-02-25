package com.sirv.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.sirv.RestClient;
import com.sirv.SirvClient;
import com.sirv.account.model.AccountInformation;
import com.sirv.account.model.AccountLimit;
import com.sirv.account.model.BillingInformation;
import com.sirv.account.model.SearchAccountEventsRequest;
import com.sirv.account.model.StorageInformation;
import com.sirv.account.model.User;
import com.sirv.exception.UnauthorizedRestClientException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AccountClientImpl implements AccountClient {
    private String host;
    private String informationUrl;
    private String limitsUrl;
    private String storageInformationUrl;
    private String usersUrl;
    private String billingInformationUrl;
    private String eventsSearchUrl;
    private String markEventsAsSeenUrl;
    private SirvClient sirvClient;
    private RestClient restClient;

    public AccountClientImpl(SirvClient sirvClient, RestClient restClient, String host) {
        this.sirvClient = sirvClient;
        this.restClient = restClient;
        this.host = host;
        initUrls();
    }

    private void initUrls() {
        informationUrl = host + "/v2/account";
        limitsUrl = host + "/v2/account/limits";
        storageInformationUrl = host + "/v2/account/storage";
        usersUrl = host + "/v2/account/users";
        billingInformationUrl = host + "/v2/billing/plan";
        eventsSearchUrl = host + "/v2/account/events/search";
        markEventsAsSeenUrl = host + "/v2/account/events/seen";
    }

    @Override
    public AccountInformation getInformation() {
        return doRequestWithRetry((h) -> restClient.doGet(informationUrl, AccountInformation.class, h));
    }

    @Override
    public Map<String, AccountLimit> getLimits() {
        return doRequestWithRetry((h) -> restClient.doGetForMap(limitsUrl, h));
    }

    @Override
    public StorageInformation getStorageInformation() {
        return doRequestWithRetry((h) -> restClient.doGet(storageInformationUrl, StorageInformation.class, h));
    }

    @Override
    public List<User> getUsers() {
        return doRequestWithRetry((h) -> restClient.doGetForList(usersUrl, h));
    }

    @Override
    public BillingInformation getBillingInformation() {
        return doRequestWithRetry((h) -> restClient.doGet(billingInformationUrl, BillingInformation.class, h));
    }

    @Override
    public void updateInformation(AccountInformation accountInformation) {
        doRequestWithRetry((h) -> restClient.doPost(informationUrl, accountInformation, String.class, h));
    }

    @Override
    public JsonNode searchEvents(SearchAccountEventsRequest request) {
        return doRequestWithRetry((h) -> restClient.doPost(eventsSearchUrl, request, JsonNode.class, h));
    }

    @Override
    public void markEventsAsSeen(List<String> eventIds) {
        doRequestWithRetry((h) -> restClient.doPost(markEventsAsSeenUrl, eventIds, String.class, h));
    }

    private <T> T doRequestWithRetry(Function<Map<String, String>, T> supplier) {
        try {
            return supplier.apply(sirvClient.getRequestHeaders(false));
        } catch (UnauthorizedRestClientException e) {
            return supplier.apply(sirvClient.getRequestHeaders(true));
        }
    }
}
