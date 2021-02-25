package com.sirv.user.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sirv.json.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class UserInformation {
    private String email;
    private String firstName;
    private String lastName;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateCreated;
    private String s3Secret;
}
