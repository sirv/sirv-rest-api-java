package com.sirv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiTokenRequest {

    /**
     * API client ID
     * (Required)
     */
    @JsonProperty("clientId")
    @JsonPropertyDescription("API client ID")
    public String clientId;

    /**
     * API client secret
     * (Required)
     */
    @JsonProperty("clientSecret")
    @JsonPropertyDescription("API client secret")
    public String clientSecret;
}