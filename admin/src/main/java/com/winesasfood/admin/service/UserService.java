package com.winesasfood.admin.service;

import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.resp.UserActualRespDTO;
import com.winesasfood.admin.dto.resp.UserRespDTO;

public interface UserService {

    UserRespDTO getUserByUsername(String username);

    UserActualRespDTO getActualUserByUsername(String username);

    /**
     * 检查用户名是否存在（布隆过滤器）
     */
    boolean isUsernameExists(String username);

    /**
     * 用户注册
     */
    void register(UserRegisterReqDTO request);
}
