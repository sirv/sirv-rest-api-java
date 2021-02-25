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
public class StorageInformation {
    private Long plan;
    private Long burstable;
    private Long extra;
    private Long used;
    private Long files;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime quotaExceededDate;
}
