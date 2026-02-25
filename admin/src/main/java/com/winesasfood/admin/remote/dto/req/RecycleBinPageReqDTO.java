package com.winesasfood.admin.remote.dto.req;

import lombok.Data;

/**
 * 回收站分页查询请求 DTO
 */
@Data
public class RecycleBinPageReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;
}
