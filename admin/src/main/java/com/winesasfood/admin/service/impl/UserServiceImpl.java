package com.winesasfood.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.winesasfood.admin.common.errorcode.BaseErrorCode;
import com.winesasfood.admin.common.exception.ClientException;
import com.winesasfood.admin.dto.resp.UserRespDTO;
import com.winesasfood.admin.dao.entity.User;
import com.winesasfood.admin.dao.mapper.UserMapper;
import com.winesasfood.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ClientException("用户不存在");
        }
        UserRespDTO dto = new UserRespDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setMail(user.getMail());
        return dto;
    }
}
