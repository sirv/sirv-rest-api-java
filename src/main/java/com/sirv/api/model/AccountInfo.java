package com.sirv.api.model;

import java.util.Map;

/**
 * Account information response.
 */
public class AccountInfo {
    private String alias;
    private String cdnURL;
    private String dateCreated;
    private Map<String, Object> fetching;
    private Map<String, Object> minify;
    private Map<String, Object> aliases;
    private Long fileSizeLimit;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCdnURL() {
        return cdnURL;
    }

    public void setCdnURL(String cdnURL) {
        this.cdnURL = cdnURL;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Map<String, Object> getFetching() {
        return fetching;
    }

    public void setFetching(Map<String, Object> fetching) {
        this.fetching = fetching;
    }

    public Map<String, Object> getMinify() {
        return minify;
    }

    public void setMinify(Map<String, Object> minify) {
        this.minify = minify;
    }

    public Map<String, Object> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, Object> aliases) {
        this.aliases = aliases;
    }

    public Long getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setFileSizeLimit(Long fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "alias='" + alias + '\'' +
                ", cdnURL='" + cdnURL + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
