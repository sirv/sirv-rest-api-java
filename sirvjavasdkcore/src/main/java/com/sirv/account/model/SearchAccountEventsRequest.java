package com.sirv.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchAccountEventsRequest {

    private String module;
    private String type;
    private String filename;
    private Level level;

    public enum Level {
        error, warn, info;
    }
}
