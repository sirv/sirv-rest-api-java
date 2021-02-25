package com.sirv.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.sirv.account.model.*;

import java.util.List;
import java.util.Map;

public interface AccountClient {

    AccountInformation getInformation();

    Map<String, AccountLimit> getLimits();

    StorageInformation getStorageInformation();

    List<User> getUsers();

    BillingInformation getBillingInformation();

    void updateInformation(AccountInformation accountInformation);

    JsonNode searchEvents(SearchAccountEventsRequest request);

    void markEventsAsSeen(List<String> eventIds);
}
