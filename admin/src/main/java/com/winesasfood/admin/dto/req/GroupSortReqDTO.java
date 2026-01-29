package com.winesasfood.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分组排序请求DTO
 */
@Data
public class GroupSortReqDTO {

    /**
     * 分组标识
     */
    @NotBlank(message = "分组标识不能为空")
    private String gid;

    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;
}
