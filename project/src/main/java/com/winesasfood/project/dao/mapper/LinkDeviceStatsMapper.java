package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.LinkDeviceStatsDO;
import com.winesasfood.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内设备监控数据
     */
    @Select("SELECT device, SUM(cnt) AS cnt " +
            "FROM t_link_device_stats " +
            "WHERE full_short_url = #{param.fullShortUrl} " +
            "AND date BETWEEN #{param.startDate} AND #{param.endDate} " +
            "AND del_flag = 0 " +
            "GROUP BY device")
    List<HashMap<String, Object>> listDeviceStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
