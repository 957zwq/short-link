package com.winesasfood.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}