package com.winesasfood.admin.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 短链接分组响应DTO
 */
@Data
public class GroupRespDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
