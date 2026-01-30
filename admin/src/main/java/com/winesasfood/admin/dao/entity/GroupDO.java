package com.winesasfood.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winesasfood.admin.common.database.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短链接分组实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_group")
public class GroupDO extends BaseDO {

    @TableId(type = IdType.AUTO)
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
}
