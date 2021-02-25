package com.sirv.user;

import com.sirv.user.model.UserInformation;

public interface UserClient {

    UserInformation getUserInformation(String userId);
}
