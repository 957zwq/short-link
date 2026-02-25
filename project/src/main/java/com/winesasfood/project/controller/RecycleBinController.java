package com.winesasfood.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winesasfood.project.common.convention.result.Result;
import com.winesasfood.project.common.convention.result.Results;
import com.winesasfood.project.dto.req.RecycleBinPageReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRecoverReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRemoveReqDTO;
import com.winesasfood.project.dto.req.RecycleBinSaveReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;
import com.winesasfood.project.service.RecycleBinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制器
 */
@Tag(name = "回收站", description = "回收站相关接口")
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 保存到回收站（软删除）
     */
    @Operation(summary = "保存到回收站", description = "将短链接移入回收站")
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @Operation(summary = "分页查询回收站", description = "分页查询回收站中的短链接")
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(RecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }

    /**
     * 恢复短链接
     */
    @Operation(summary = "恢复短链接", description = "从回收站恢复短链接")
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 移除短链接
     */
    @Operation(summary = "移除短链接", description = "从回收站彻底删除短链接")
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleBinService.removeRecycleBin(requestParam);
        return Results.success();
    }
}
