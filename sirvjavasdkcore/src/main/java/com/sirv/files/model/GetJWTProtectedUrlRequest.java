package com.sirv.files.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetJWTProtectedUrlRequest {
    private String filename;
    private String key;
    private String alias;
    private SecureParams secureParams;
    private Long expiresIn;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SecureParams {
        private Long w;
        private Long h;
    }
}
