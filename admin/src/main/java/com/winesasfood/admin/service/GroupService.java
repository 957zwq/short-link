package com.winesasfood.admin.service;

import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.req.GroupUpdateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;

import java.util.List;

/**
 * 短链接分组服务接口
 */
public interface GroupService {

    /**
     * 创建短链接分组
     *
     * @param request 创建分组请求
     * @return 分组响应DTO
     */
    GroupRespDTO createGroup(GroupCreateReqDTO request);

    /**
     * 查询用户分组集合
     *
     * @return 分组列表
     */
    List<GroupRespDTO> getGroupsByUsername();

    /**
     * 修改短链接分组
     *
     * @param request 修改分组请求
     */
    void updateGroup(GroupUpdateReqDTO request);
}
