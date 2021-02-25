package com.sirv.model;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiTokenResponse {
    /**
     * API token
     */
    @JsonProperty("token")
    @JsonPropertyDescription("API token")
    private String token;

    /**
     * API token expiration time
     */
    @JsonProperty("expiresIn")
    @JsonPropertyDescription("API token expiration time")
    private long expiresIn;

    /**
     * API scopes
     */
    @JsonProperty("scope")
    @JsonPropertyDescription("API scopes")
    private List<String> scope;

    public boolean isAlive() {
        return JWT.decode(token).getExpiresAt().after(new Date());
    }
}
