package com.winesasfood.admin.remote.dto.req;

import lombok.Data;

/**
 * 回收站保存请求 DTO
 */
@Data
public class RecycleBinSaveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
