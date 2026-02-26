package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接访问统计 Mapper
 */
@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录访问统计
     * 使用 INSERT ON DUPLICATE KEY UPDATE 实现增量统计
     *
     * @param linkAccessStatsDO 访问统计数据
     */
    @Insert("INSERT INTO t_link_access_stats (full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{date}, #{pv}, #{uv}, #{uip}, #{hour}, #{weekday}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE pv = pv + #{pv}, uv = uv + #{uv}, uip = uip + #{uip}, update_time = NOW()")
    void shortLinkStats(LinkAccessStatsDO linkAccessStatsDO);
}
