package org.example.service;

import org.example.model.UserDtls;

public interface UserService {

    public UserDtls createUser(UserDtls user, String url);

    public boolean checkEmail(String email);

    public boolean verifyAccount (String code);

    public void forgottenPass(UserDtls user);

}
