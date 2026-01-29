package com.winesasfood.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改短链接分组请求DTO
 */
@Data
public class GroupUpdateReqDTO {

    /**
     * 分组标识
     */
    @NotBlank(message = "分组标识不能为空")
    private String gid;

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    @Size(max = 64, message = "分组名称长度不能超过64个字符")
    private String name;
}
