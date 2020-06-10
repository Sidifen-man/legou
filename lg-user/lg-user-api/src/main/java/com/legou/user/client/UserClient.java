package com.legou.user.client;

import com.legou.user.dto.AddressDTO;
import com.legou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping("info")
    UserDTO queryUserByPhoneAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);
    /**
     * 根据id查询地址
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/address/{id}")
    AddressDTO queryAddressById(@PathVariable("id") Long id);

}
