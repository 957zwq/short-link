package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接操作系统访问统计 Mapper
 */
@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 记录操作系统访问统计
     * 使用 INSERT ON DUPLICATE KEY UPDATE 实现增量统计
     *
     * @param linkOsStatsDO 操作系统访问统计数据
     */
    @Insert("INSERT INTO t_link_os_stats (full_short_url, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{date}, #{cnt}, #{os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{cnt}, update_time = NOW()")
    void shortLinkOsStats(LinkOsStatsDO linkOsStatsDO);
}
