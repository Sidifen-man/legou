package com.legou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.user.dto.UserDTO;
import com.legou.user.entity.User;

public interface UserService extends IService<User> {
    Boolean exists(String data, Integer type);

    void sendCode(String phone);

    void register(User user, String code);

    UserDTO queryUserByPhoneAndPassword(String username, String password);
}
