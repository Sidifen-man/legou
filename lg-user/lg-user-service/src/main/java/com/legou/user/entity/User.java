package com.legou.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.legou.common.constants.RegexPatterns;
import com.legou.common.entity.BaseEntity;
import com.legou.common.utils.RegexUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@TableName("tb_user")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @TableId
    private Long id;
    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX)
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX, message = "密码格式不正确")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = "手机号格式不正确")
    private String phone;

}
