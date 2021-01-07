package com.sirv.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class BillingInformation {
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateActive;
    private String id;
    private String period;
    private String name;
    private String description;
    private Price price;
    private Long storage;
    private Long burstableStorage;

    @Getter
    @Setter
    @ToString
    public static class Price {
        private Integer month;
        private Integer year;
        private Integer quarter;
    }
}
