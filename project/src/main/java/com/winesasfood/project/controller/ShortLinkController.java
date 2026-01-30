package com.winesasfood.project.controller;

import com.winesasfood.project.common.convention.result.Result;
import com.winesasfood.project.common.convention.result.Results;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.project.service.ShortLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "短链接管理", description = "短链接相关接口")
@RestController
@RequiredArgsConstructor
public class ShortLinkController {


    private final ShortLinkService shortLinkService;
    /**
     * 创建短链接
     */
    @Operation(summary = "创建短链接", description = "根据原始链接生成短链接")
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }
}