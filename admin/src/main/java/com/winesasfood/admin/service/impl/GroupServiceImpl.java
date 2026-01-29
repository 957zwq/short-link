package com.winesasfood.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.winesasfood.admin.common.enums.GroupErrorCodeEnum;
import com.winesasfood.admin.common.exception.ClientException;
import com.winesasfood.admin.dao.entity.Group;
import com.winesasfood.admin.dao.entity.User;
import com.winesasfood.admin.dao.mapper.GroupMapper;
import com.winesasfood.admin.dao.mapper.UserMapper;
import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;
import com.winesasfood.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 短链接分组服务实现
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;

    @Override
    public GroupRespDTO createGroup(GroupCreateReqDTO request, String username) {
        // 检查用户是否存在
        LambdaQueryWrapper<User> userQuery = Wrappers.lambdaQuery();
        userQuery.eq(User::getUsername, username);
        User user = userMapper.selectOne(userQuery);
        if (user == null) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_USER_NOT_EXIST);
        }

        // 生成gid：使用用户名+时间戳后6位
        String gid = generateGid(username);

        // 检查该用户下是否存在相同名称的分组
        LambdaQueryWrapper<Group> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Group::getUsername, username)
                .eq(Group::getName, request.getName())
                .eq(Group::getDelFlag, 0);
        Group existingGroup = groupMapper.selectOne(queryWrapper);
        if (existingGroup != null) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NAME_EXIST);
        }

        // 创建分组
        Group group = new Group();
        group.setGid(gid);
        group.setName(request.getName());
        group.setUsername(username);
        group.setSortOrder(0);
        group.setCreateTime(new Date());
        group.setUpdateTime(new Date());
        group.setDelFlag(0);

        int inserted = groupMapper.insert(group);
        if (inserted <= 0) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_SAVE_ERROR);
        }

        // 构造返回DTO
        GroupRespDTO respDTO = new GroupRespDTO();
        respDTO.setId(group.getId());
        respDTO.setGid(group.getGid());
        respDTO.setName(group.getName());
        respDTO.setUsername(group.getUsername());
        respDTO.setSortOrder(group.getSortOrder());
        respDTO.setCreateTime(group.getCreateTime());
        respDTO.setUpdateTime(group.getUpdateTime());

        return respDTO;
    }

    /**
     * 生成分组标识
     */
    private String generateGid(String username) {
        // 使用用户名哈希值的后8位 + 时间戳后6位
        long timestamp = System.currentTimeMillis();
        int hash = username.hashCode();
        String hashPart = String.format("%08x", Math.abs(hash)).substring(0, 8);
        String timePart = String.valueOf(timestamp).substring(String.valueOf(timestamp).length() - 6);
        return hashPart + timePart;
    }
}
