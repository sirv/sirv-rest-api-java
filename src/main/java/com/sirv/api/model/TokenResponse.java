package com.sirv.api.model;

import java.util.List;

/**
 * Response from the authentication endpoint.
 */
public class TokenResponse {
    private String token;
    private int expiresIn;
    private List<String> scope;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : null) + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope=" + scope +
                '}';
    }
}
