package com.winesasfood.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接分页查询响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接分页查询响应")
public class ShortLinkPageRespDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "域名")
    private String domain;

    @Schema(description = "短链接后缀")
    private String shortUri;

    @Schema(description = "完整短链接")
    private String fullShortUrl;

    @Schema(description = "原始长链接")
    private String originUrl;

    @Schema(description = "分组标识")
    private String gid;

    @Schema(description = "有效期类型 0：永久 1：自定义")
    private Integer validDateType;

    @Schema(description = "有效期")
    private String validDate;

    @Schema(description = "描述")
    private String describe;

    @Schema(description = "网站图标")
    private String favicon;
}
