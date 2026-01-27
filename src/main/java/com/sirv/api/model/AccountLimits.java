package com.sirv.api.model;

import java.util.Map;

/**
 * API rate limits for different endpoint types.
 */
public class AccountLimits {
    private Map<String, Object> s3;
    private Map<String, Object> rest;
    private Map<String, Object> ftp;

    public Map<String, Object> getS3() {
        return s3;
    }

    public void setS3(Map<String, Object> s3) {
        this.s3 = s3;
    }

    public Map<String, Object> getRest() {
        return rest;
    }

    public void setRest(Map<String, Object> rest) {
        this.rest = rest;
    }

    public Map<String, Object> getFtp() {
        return ftp;
    }

    public void setFtp(Map<String, Object> ftp) {
        this.ftp = ftp;
    }

    @Override
    public String toString() {
        return "AccountLimits{" +
                "s3=" + s3 +
                ", rest=" + rest +
                ", ftp=" + ftp +
                '}';
    }
}
