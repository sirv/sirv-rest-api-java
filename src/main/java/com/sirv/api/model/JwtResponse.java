package com.sirv.api.model;

/**
 * JWT generation response.
 */
public class JwtResponse {
    private String url;
    private String token;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "url='" + url + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : null) + '\'' +
                '}';
    }
}
