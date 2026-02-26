package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkAccessLogsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}
