package com.sirv.api.model;

import java.util.List;
import java.util.Map;

/**
 * Search results response.
 */
public class SearchResult {
    private List<Map<String, Object>> hits;
    private Long total;
    private String scrollId;

    public List<Map<String, Object>> getHits() {
        return hits;
    }

    public void setHits(List<Map<String, Object>> hits) {
        this.hits = hits;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    /**
     * Extracts FileInfo from a search hit.
     * Search results wrap file data in _source field.
     *
     * @param hit The search hit map
     * @return FileInfo extracted from the hit
     */
    @SuppressWarnings("unchecked")
    public static FileInfo extractFileInfo(Map<String, Object> hit) {
        Map<String, Object> source = (Map<String, Object>) hit.getOrDefault("_source", hit);

        FileInfo info = new FileInfo();
        info.setFilename((String) source.get("filename"));
        info.setDirname((String) source.get("dirname"));
        info.setBasename((String) source.get("basename"));
        info.setExtension((String) source.get("extension"));
        info.setContentType((String) source.get("contentType"));
        info.setCtime((String) source.get("ctime"));
        info.setMtime((String) source.get("mtime"));

        Object size = source.get("size");
        if (size instanceof Number) {
            info.setSize(((Number) size).longValue());
        }

        Object isDir = source.get("isDirectory");
        if (isDir instanceof Boolean) {
            info.setIsDirectory((Boolean) isDir);
        }

        return info;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "hits=" + (hits != null ? hits.size() + " results" : "null") +
                ", total=" + total +
                ", scrollId='" + scrollId + '\'' +
                '}';
    }
}
