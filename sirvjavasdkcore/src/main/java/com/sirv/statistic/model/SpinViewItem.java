package com.sirv.statistic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class SpinViewItem {
    @JsonProperty("@timestamp")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime timestamp;
    @JsonProperty("user_agent")
    private String userAgent;
    @JsonProperty("user_agent_parsed")
    private Map<String, String> userAgentParsed;
    @JsonProperty("referer")
    private String referrer;
    @JsonProperty("referer_proto")
    private String referrerProto;
    @JsonProperty("referer_host")
    private String referrerHost;
    @JsonProperty("referer_uripath")
    private String referrerUriPath;
    @JsonProperty("event")
    private Map<String, Object> event;
    @JsonProperty("geoip")
    private Map<String, String> geoip;
}
