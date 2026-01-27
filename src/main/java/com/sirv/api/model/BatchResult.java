package com.sirv.api.model;

import java.util.List;
import java.util.Map;

/**
 * Batch operation result (delete, zip).
 */
public class BatchResult {
    private String jobId;
    private String status;
    private String filename;
    private List<Map<String, Object>> results;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
