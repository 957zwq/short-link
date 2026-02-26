package com.winesasfood.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.winesasfood.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接访问统计 DO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量 (PV)
     */
    private Integer pv;

    /**
     * 独立访客数 (UV)
     */
    private Integer uv;

    /**
     * 独立IP数 (UIP)
     */
    private Integer uip;

    /**
     * 小时 (0-23)
     */
    private Integer hour;

    /**
     * 星期 (1-7, ISO标准)
     */
    private Integer weekday;
}
