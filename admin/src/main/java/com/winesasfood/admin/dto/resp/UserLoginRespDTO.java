package com.winesasfood.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRespDTO {

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 用户名
     */
    private String username;
}
