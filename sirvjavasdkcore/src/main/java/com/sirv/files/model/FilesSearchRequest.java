package com.sirv.files.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilesSearchRequest {
    private String query;
    private Map<String, String> sort;
    private Long from;
    private Long size;
}
