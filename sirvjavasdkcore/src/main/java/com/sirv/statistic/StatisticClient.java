package com.sirv.statistic;

import com.sirv.statistic.model.SpinViewItem;
import com.sirv.statistic.model.StorageStatisticItem;
import com.sirv.statistic.model.TransferStatisticItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticClient {
    Map<String, TransferStatisticItem> getTransferStatistic(ZonedDateTime from, ZonedDateTime to);
    List<SpinViewItem> getSpinViews(ZonedDateTime from, ZonedDateTime to, String alias);
    Map<String, StorageStatisticItem> getStorageStatistic(ZonedDateTime from, ZonedDateTime to);
}
