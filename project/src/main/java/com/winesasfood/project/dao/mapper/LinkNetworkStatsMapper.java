package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接网络访问统计 Mapper
 */
@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    /**
     * 记录网络访问统计
     * 使用 INSERT ON DUPLICATE KEY UPDATE 实现增量统计
     *
     * @param linkNetworkStatsDO 网络访问统计数据
     */
    @Insert("INSERT INTO t_link_network_stats (full_short_url, date, cnt, network, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{date}, #{cnt}, #{network}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{cnt}, update_time = NOW()")
    void shortLinkNetworkStats(LinkNetworkStatsDO linkNetworkStatsDO);
}
