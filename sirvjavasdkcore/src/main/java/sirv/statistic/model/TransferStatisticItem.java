package com.sirv.statistic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransferStatisticItem {
    private Total total;

    @Getter
    @Setter
    @ToString
    public static class Total {
        private Long count;
        private Long size;
    }
}
