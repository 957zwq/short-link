package com.winesasfood.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.winesasfood.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接操作系统访问统计 DO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;
}
