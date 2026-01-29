package com.winesasfood.admin.controller;

import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.common.result.Results;
import com.winesasfood.admin.dto.req.GroupCreateReqDTO;
import com.winesasfood.admin.dto.req.GroupUpdateReqDTO;
import com.winesasfood.admin.dto.resp.GroupRespDTO;
import com.winesasfood.admin.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Result<GroupRespDTO> createGroup(@Valid @RequestBody GroupCreateReqDTO request) {
        GroupRespDTO response = groupService.createGroup(request);
        return Results.success(response);
    }

    @Operation(summary = "查询分组集合")
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupRespDTO>> getGroups() {
        List<GroupRespDTO> response = groupService.getGroupsByUsername();
        return Results.success(response);
    }

    @Operation(summary = "修改短链接分组")
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@Valid @RequestBody GroupUpdateReqDTO request) {
        groupService.updateGroup(request);
        return Results.success();
    }

    @Operation(summary = "删除短链接分组")
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@Parameter(description = "分组标识") @RequestParam("gid") String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }
}
