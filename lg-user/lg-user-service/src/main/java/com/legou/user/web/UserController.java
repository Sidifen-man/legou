package com.legou.user.web;

import com.legou.user.entity.User;
import com.legou.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("info")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    /**
     * 校验手机号或用户名是否存在
     * @param data 用户名或手机号
     * @param type 数据类型：1是用户名；2是手机；其它是参数有误
     * @return true：可以使用; false：不可使用
     */

    @GetMapping("/exists/{data}/{type}")
    public ResponseEntity<Boolean> exists(@PathVariable("data") String data,@PathVariable("type") Integer type){
        ResponseEntity<Boolean> callBackData = ResponseEntity.ok(userService.exists(data, type));
        return callBackData;
    }
    @GetMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据手机号和密码查询用户
     * @param username 手机号
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping
    public ResponseEntity<Object> queryUserByPhoneAndPassword(
            @RequestParam("username") String username, @RequestParam("password") String password){
        return ResponseEntity.ok(userService.queryUserByPhoneAndPassword(username, password));
    }
}
