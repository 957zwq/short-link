package com.winesasfood.admin.service;

import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;

/**
 * 短链接分组服务接口
 */
public interface GroupService {

    /**
     * 创建短链接分组
     *
     * @param request 创建分组请求
     * @param username 用户名
     * @return 分组响应DTO
     */
    GroupRespDTO createGroup(GroupCreateReqDTO request, String username);
}
