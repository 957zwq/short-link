package com.winesasfood.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建短链接分组请求DTO
 */
@Data
public class GroupCreateReqDTO {

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    @Size(max = 64, message = "分组名称长度不能超过64个字符")
    private String name;
}
