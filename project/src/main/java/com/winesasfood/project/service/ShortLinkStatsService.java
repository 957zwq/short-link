package com.winesasfood.project.service;

import com.winesasfood.project.dto.req.ShortLinkStatsReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控统计服务接口
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接的监控统计数据
     *
     * @param requestParam 统计请求参数
     * @return 短链接统计数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);
}
