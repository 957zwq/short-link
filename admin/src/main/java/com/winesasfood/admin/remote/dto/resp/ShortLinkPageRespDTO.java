package com.winesasfood.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description = "描述")
    private String describe;

    @Schema(description = "网站图标")
    private String favicon;
}