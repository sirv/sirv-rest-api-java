package com.sirv;

import com.sirv.account.AccountClient;
import com.sirv.files.FilesClient;
import com.sirv.model.ApiTokenResponse;
import com.sirv.statistic.StatisticClient;
import com.sirv.user.UserClient;

import java.util.Map;

public interface SirvClient {
    AccountClient getAccountClient();

    UserClient getUserClient();

    StatisticClient getStatisticClient();

    FilesClient getFilesClient();

    ApiTokenResponse getToken();

    Map<String, String> getRequestHeaders(boolean forceNew);
}
