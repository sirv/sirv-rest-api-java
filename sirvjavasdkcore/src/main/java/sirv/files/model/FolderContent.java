package com.sirv.files.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FolderContent {
    List<Content> contents;
}
