package com.winesasfood.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.common.result.Results;
import com.winesasfood.admin.remote.ShortLinkRemoteService;
import com.winesasfood.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkPageRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "短链接管理-Admin", description = "短链接管理相关接口（调用 project 模块）")
@RestController
public class ShortLinkController implements ShortLinkRemoteService {

    /**
     * 创建短链接
     */
    @Operation(summary = "创建短链接", description = "调用 project 模块创建短链接")
    @PostMapping("/api/short-link/admin/v1/create")
    @Override
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return ShortLinkRemoteService.super.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @Operation(summary = "分页查询短链接", description = "调用 project 模块分页查询短链接")
    @GetMapping("/api/short-link/admin/v1/page")
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return ShortLinkRemoteService.super.pageShortLink(requestParam);
    }

    /**
     * 更新短链接
     */
    @Operation(summary = "更新短链接", description = "调用 project 模块更新短链接")
    @PutMapping("/api/short-link/admin/v1/update")
    @Override
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        return ShortLinkRemoteService.super.updateShortLink(requestParam);
    }
}