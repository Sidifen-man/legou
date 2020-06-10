package com.legou.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserAuthService {
    void login(String username, String password, HttpServletResponse response);

    String verifyUser(HttpServletRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
