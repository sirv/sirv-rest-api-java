package com.sirv.api.model;

import java.util.List;

/**
 * Folder contents response.
 */
public class FolderContents {
    private List<FileInfo> contents;
    private String continuation;

    public List<FileInfo> getContents() {
        return contents;
    }

    public void setContents(List<FileInfo> contents) {
        this.contents = contents;
    }

    public String getContinuation() {
        return continuation;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }

    @Override
    public String toString() {
        return "FolderContents{" +
                "contents=" + (contents != null ? contents.size() + " items" : "null") +
                ", continuation='" + continuation + '\'' +
                '}';
    }
}
