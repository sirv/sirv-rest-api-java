package com.sirv.statistic;

import com.sirv.RestClient;
import com.sirv.SirvClient;
import com.sirv.exception.UnauthorizedRestClientException;
import com.sirv.statistic.model.SpinViewItem;
import com.sirv.statistic.model.StorageStatisticItem;
import com.sirv.statistic.model.TransferStatisticItem;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StatisticClientImpl implements StatisticClient {
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String ALIAS = "alias";

    private String host;
    private String transferStatisticUrl;
    private String spinViewUrl;
    private String storageStatisticUrl;
    private SirvClient sirvClient;
    private RestClient restClient;

    public StatisticClientImpl(SirvClient sirvClient, RestClient restClient, String host) {
        this.sirvClient = sirvClient;
        this.restClient = restClient;
        this.host = host;
        initUrls();
    }

    private void initUrls() {
        transferStatisticUrl = host + "/v2/stats/http";
        spinViewUrl = host + "/v2/stats/spins/views";
        storageStatisticUrl = host + "/v2/stats/storage";
    }


    @Override
    public Map<String, TransferStatisticItem> getTransferStatistic(ZonedDateTime from, ZonedDateTime to) {
        Map<String, String> queryParams = new HashMap<>();
        Optional.ofNullable(from).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(FROM, s));
        Optional.ofNullable(to).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(TO, s));

        return doRequestWithRetry((h) -> restClient.doGetForMap(transferStatisticUrl, h, queryParams));
    }

    @Override
    public List<SpinViewItem> getSpinViews(ZonedDateTime from, ZonedDateTime to, String alias) {
        Map<String, String> queryParams = new HashMap<>();
        Optional.ofNullable(from).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(FROM, s));
        Optional.ofNullable(to).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(TO, s));
        Optional.ofNullable(alias).ifPresent(s -> queryParams.put(ALIAS, s));


        return doRequestWithRetry((h) -> restClient.doGetForList(spinViewUrl, h, queryParams));
    }

    @Override
    public Map<String, StorageStatisticItem> getStorageStatistic(ZonedDateTime from, ZonedDateTime to) {
        Map<String, String> queryParams = new HashMap<>();
        Optional.ofNullable(from).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(FROM, s));
        Optional.ofNullable(to).map(ZonedDateTime::toOffsetDateTime).map(Object::toString).ifPresent(s -> queryParams.put(TO, s));

        return doRequestWithRetry((h) -> restClient.doGetForMap(storageStatisticUrl, h, queryParams));
    }

    private <T> T doRequestWithRetry(Function<Map<String, String>, T> supplier) {
        try {
            return supplier.apply(sirvClient.getRequestHeaders(false));
        } catch (UnauthorizedRestClientException e) {
            return supplier.apply(sirvClient.getRequestHeaders(true));
        }
    }
}
