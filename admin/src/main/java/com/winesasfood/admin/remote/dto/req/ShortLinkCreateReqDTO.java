package com.winesasfood.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "短链接创建请求")
public class ShortLinkCreateReqDTO {

    @Schema(description = "协议")
    private String domainProtocol;

    @Schema(description = "域名")
    private String domain;

    @Schema(description = "原始链接")
    private String originUrl;

    @Schema(description = "分组标识")
    private String gid;

    @Schema(description = "创建类型 0：接口创建 1：控制台创建")
    private Integer createdType;

    @Schema(description = "有效期类型 0：永久有效 1：自定义")
    private Integer validDateType;

    @Schema(description = "有效期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    @Schema(description = "描述")
    private String describe;
}
