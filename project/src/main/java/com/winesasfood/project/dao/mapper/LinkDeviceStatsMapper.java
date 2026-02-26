package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接设备访问统计 Mapper
 */
@Mapper
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    /**
     * 记录设备访问统计
     * 使用 INSERT ON DUPLICATE KEY UPDATE 实现增量统计
     *
     * @param linkDeviceStatsDO 设备访问统计数据
     */
    @Insert("INSERT INTO t_link_device_stats (full_short_url, date, cnt, device, create_time, update_time, del_flag) " +
            "VALUES (#{fullShortUrl}, #{date}, #{cnt}, #{device}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{cnt}, update_time = NOW()")
    void shortLinkDeviceStats(LinkDeviceStatsDO linkDeviceStatsDO);
}
