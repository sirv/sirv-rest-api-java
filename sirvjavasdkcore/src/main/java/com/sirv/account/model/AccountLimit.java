package com.sirv.account.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountLimit {
    private Long count;
    private Long limit;
    private Long remaining;
    private Long reset;
}
