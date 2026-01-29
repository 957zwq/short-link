package com.winesasfood.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.winesasfood.admin.common.enums.GroupErrorCodeEnum;
import com.winesasfood.admin.common.exception.ClientException;
import com.winesasfood.admin.context.UserContext;
import com.winesasfood.admin.dao.entity.GroupDO;
import com.winesasfood.admin.dao.entity.UserDO;
import com.winesasfood.admin.dao.mapper.GroupMapper;
import com.winesasfood.admin.dao.mapper.UserMapper;
import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;
import com.winesasfood.admin.service.GroupService;
import com.winesasfood.admin.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 短链接分组服务实现
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;

    @Override
    public GroupRespDTO createGroup(GroupCreateReqDTO request) {
        String username = UserContext.getUsername();

        // 检查用户是否存在
        LambdaQueryWrapper<UserDO> userQuery = Wrappers.lambdaQuery();
        userQuery.eq(UserDO::getUsername, username);
        UserDO user = userMapper.selectOne(userQuery);
        if (user == null) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_USER_NOT_EXIST);
        }

        // 生成gid：随机生成6位，循环直到不重复
        String gid;
        while (true) {
            gid = generateGid();
            LambdaQueryWrapper<GroupDO> gidQuery = Wrappers.lambdaQuery();
            gidQuery.eq(GroupDO::getGid, gid);
            GroupDO existingGid = groupMapper.selectOne(gidQuery);
            if (existingGid == null) {
                break;
            }
        }

        // 检查该用户下是否存在相同名称的分组
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroupDO::getUsername, username)
                .eq(GroupDO::getName, request.getName())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO existingGroup = groupMapper.selectOne(queryWrapper);
        if (existingGroup != null) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NAME_EXIST);
        }

        // 创建分组
        GroupDO group = new GroupDO();
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
    private String generateGid() {
        return RandomGenerator.generateRandom();
    }

    @Override
    public List<GroupRespDTO> getGroupsByUsername() {
        String username = UserContext.getUsername();

        // 查询用户未删除的分组，按sort_order升序
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .orderByAsc(GroupDO::getSortOrder);
        List<GroupDO> groupList = groupMapper.selectList(queryWrapper);

        // 转换为DTO
        List<GroupRespDTO> resultList = new ArrayList<>();
        for (GroupDO group : groupList) {
            GroupRespDTO respDTO = new GroupRespDTO();
            respDTO.setId(group.getId());
            respDTO.setGid(group.getGid());
            respDTO.setName(group.getName());
            respDTO.setUsername(group.getUsername());
            respDTO.setSortOrder(group.getSortOrder());
            respDTO.setCreateTime(group.getCreateTime());
            respDTO.setUpdateTime(group.getUpdateTime());
            resultList.add(respDTO);
        }

        return resultList;
    }
}
