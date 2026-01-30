package com.winesasfood.project.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短链接分页查询请求
 */
@Data
@Schema(description = "短链接分页查询请求")
public class ShortLinkPageReqDTO {

    @Schema(description = "当前页码", example = "1")
    private Long current;

    @Schema(description = "每页条数", example = "10")
    private Long size;

    @Schema(description = "分组标识", example = "default")
    private String gid;
}
