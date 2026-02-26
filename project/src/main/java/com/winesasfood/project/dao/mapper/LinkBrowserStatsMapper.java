package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接浏览器访问统计 Mapper
 */
@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {

    /**
     * 记录浏览器访问统计
     * 使用 INSERT ON DUPLICATE KEY UPDATE 实现增量统计
     *
     * @param linkBrowserStatsDO 浏览器访问统计数据
     */
    @Insert("INSERT INTO t_link_browser_stats (full_short_url, date, cnt, browser, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{date}, #{cnt}, #{browser}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{cnt}, update_time = NOW()")
    void shortLinkBrowserStats(LinkBrowserStatsDO linkBrowserStatsDO);
}
