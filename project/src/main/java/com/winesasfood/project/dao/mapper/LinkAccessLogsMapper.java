package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkAccessLogsDO;
import com.winesasfood.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 短链接访问日志 Mapper
 */
@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 记录访问日志
     *
     * @param linkAccessLogsDO 访问日志数据
     */
    @Insert("INSERT INTO t_link_access_logs (full_short_url, user, ip, browser, os, network, device, locale, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{user}, #{ip}, #{browser}, #{os}, #{network}, #{device}, #{locale}, NOW(), NOW(), 0)")
    void shortLinkAccessLogs(LinkAccessLogsDO linkAccessLogsDO);

    /**
     * 根据短链接获取指定日期内高频访问IP数据
     */
    @Select("SELECT ip, COUNT(ip) AS cnt " +
            "FROM t_link_access_logs " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND create_time BETWEEN #{param.startDate} AND CONCAT(#{param.endDate}, ' 23:59:59') " +
            "AND del_flag = 0 " +
            "GROUP BY ip " +
            "ORDER BY cnt DESC " +
            "LIMIT 5")
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新老访客数据
     */
    @Select("SELECT user, COUNT(1) AS cnt " +
            "FROM t_link_access_logs " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND create_time BETWEEN #{param.startDate} AND CONCAT(#{param.endDate}, ' 23:59:59') " +
            "AND del_flag = 0 " +
            "GROUP BY user")
    List<HashMap<String, Object>> listUvTypeByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 获取用户首次访问时间
     */
    @Select("SELECT user, MIN(create_time) AS first_access_time " +
            "FROM t_link_access_logs " +
            "WHERE full_short_url = #{fullShortUrl} " +
            "AND del_flag = 0 " +
            "GROUP BY user")
    List<HashMap<String, Object>> listUserFirstAccessTime(@Param("fullShortUrl") String fullShortUrl);
}
