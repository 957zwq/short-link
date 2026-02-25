package com.winesasfood.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.remote.ShortLinkRemoteService;
import com.winesasfood.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkPageRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制器 - Admin
 */
@Tag(name = "回收站管理-Admin", description = "回收站相关接口（调用 project 模块）")
@RestController
public class RecycleBinController implements ShortLinkRemoteService {

    /**
     * 保存到回收站
     */
    @Operation(summary = "保存到回收站", description = "调用 project 模块将短链接移入回收站")
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    @Override
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        return ShortLinkRemoteService.super.saveRecycleBin(requestParam);
    }

    /**
     * 分页查询回收站短链接
     */
    @Operation(summary = "分页查询回收站", description = "调用 project 模块分页查询回收站短链接")
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO requestParam) {
        return ShortLinkRemoteService.super.pageRecycleBin(requestParam);
    }

    /**
     * 恢复短链接
     */
    @Operation(summary = "恢复短链接", description = "调用 project 模块从回收站恢复短链接")
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    @Override
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        return ShortLinkRemoteService.super.recoverRecycleBin(requestParam);
    }

    /**
     * 移除短链接
     */
    @Operation(summary = "移除短链接", description = "调用 project 模块从回收站彻底删除短链接")
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    @Override
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        return ShortLinkRemoteService.super.removeRecycleBin(requestParam);
    }
}
