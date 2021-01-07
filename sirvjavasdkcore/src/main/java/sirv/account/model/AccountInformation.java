package com.sirv.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInformation {
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateCreated;
    private String alias;
    private Fetching fetching;
    private Minify minify;
    private String cdnTempURL;
    private String cdnURL;
    private Map<String, Alias> aliases;

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fetching {
        private Boolean enabled;
        private String type;
        private Http http;
        private S3 s3;
        private Long maxFileSize;

        @Getter
        @Setter
        @ToString
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Http {
            private Auth auth;
            private String url;

            @Getter
            @Setter
            @ToString
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Auth {
                private String username;
                private String password;
                private Boolean enabled;
            }
        }


        @Getter
        @Setter
        @ToString
        public static class S3 {
            private String endpoint;
            private String accessKeyId;
            private String secretAccessKey;
            private String bucket;
            private String prefix;
            private Boolean forcePathStyle;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class Minify {
        private Boolean enabled;
    }

    @Getter
    @Setter
    @ToString
    public static class Alias {
        private String prefix;
        private Boolean cdn;
    }
}
