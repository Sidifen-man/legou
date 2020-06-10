package com.legou.auth.web;

import com.legou.auth.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("user")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    /**
     * 登录接口
     * @param username 用户名
     * @param password 密码
     * @return 无
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response
    ){
    userAuthService.login(username,password,response);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(HttpServletRequest request) {
        // 成功后直接返回
        return ResponseEntity.ok(userAuthService.verifyUser(request));
    }
    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        userAuthService.logout(request,response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
