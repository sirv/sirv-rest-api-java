package com.sirv.files.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalFlag {
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime datetime;
    private Boolean approved;
    private String comment;
}
