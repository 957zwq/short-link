package com.winesasfood.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class UserLoginReqDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 16, message = "用户名长度必须在4-16位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
