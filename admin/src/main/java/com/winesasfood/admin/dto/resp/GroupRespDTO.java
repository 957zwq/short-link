package com.winesasfood.admin.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "短链接分组响应")
public class GroupRespDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分组标识")
    private String gid;

    @Schema(description = "分组名称")
    private String name;

    @Schema(description = "创建分组用户名")
    private String username;

    @Schema(description = "分组排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
