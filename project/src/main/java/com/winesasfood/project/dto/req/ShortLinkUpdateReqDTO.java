package com.winesasfood.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 短链接更新请求
 */
@Data
@Schema(description = "短链接更新请求")
public class ShortLinkUpdateReqDTO {

    @Schema(description = "原始链接")
    private String originUrl;

    @Schema(description = "完整短链接")
    private String fullShortUrl;

    @Schema(description = "分组标识")
    private String gid;

    @Schema(description = "有效期类型 0：永久有效 1：自定义")
    private Integer validDateType;

    @Schema(description = "有效期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    @Schema(description = "描述")
    private String describe;
}
