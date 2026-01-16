package com.winesasfood.admin.service;

import com.winesasfood.admin.dto.resp.UserRespDTO;

public interface UserService {

    UserRespDTO getUserByUsername(String username);
}
