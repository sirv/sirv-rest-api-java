package com.sirv.files.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class Content {
    private String filename;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime mtime;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime ctime;
    private String contentType;
    private Long size;
    private Boolean isDirectory;
    private Meta meta;

    @Getter
    @Setter
    @ToString
    public static class Meta {
        private Long width;
        private Long height;
        private Long duration;
    }
}
