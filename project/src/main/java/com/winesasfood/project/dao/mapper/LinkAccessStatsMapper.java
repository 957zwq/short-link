package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkAccessStatsDO;
import com.winesasfood.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT date, SUM(pv) AS pv, SUM(uv) AS uv, SUM(uip) AS uip " +
            "FROM t_link_access_stats " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND date BETWEEN #{param.startDate} AND #{param.endDate} " +
            "AND del_flag = 0 " +
            "GROUP BY date " +
            "ORDER BY date")
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT hour, SUM(pv) AS pv " +
            "FROM t_link_access_stats " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND date BETWEEN #{param.startDate} AND #{param.endDate} " +
            "AND del_flag = 0 " +
            "GROUP BY hour " +
            "ORDER BY hour")
    List<HashMap<String, Object>> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内周几基础监控数据
     */
    @Select("SELECT weekday, SUM(pv) AS pv " +
            "FROM t_link_access_stats " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND date BETWEEN #{param.startDate} AND #{param.endDate} " +
            "AND del_flag = 0 " +
            "GROUP BY weekday " +
            "ORDER BY weekday")
    List<HashMap<String, Object>> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
