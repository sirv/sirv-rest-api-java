package com.sirv.files.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductMeta {
    private String id;
    private String name;
    private String brand;
    private String category1;
    private String category2;
}
