package com.winesasfood.admin.controller;

import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.common.result.Results;
import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;
import com.winesasfood.admin.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接分组管理控制器
 */
@Tag(name = "短链接分组管理", description = "短链接分组相关接口")
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "新增短链接分组")
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<GroupRespDTO> createGroup(
            @Parameter(description = "用户名") @RequestHeader("username") String username,
            @Parameter(description = "登录令牌") @RequestHeader("token") String token,
            @Valid @RequestBody GroupCreateReqDTO request) {
        GroupRespDTO response = groupService.createGroup(request, username);
        return Results.success(response);
    }
}
