package com.winesasfood.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.winesasfood.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接跳转路由表
 */
@Data
@TableName("t_link_goto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGotoDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
