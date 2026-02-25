package com.winesasfood.project.controller;

import com.winesasfood.project.common.convention.result.Result;
import com.winesasfood.project.common.convention.result.Results;
import com.winesasfood.project.service.UrlTitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL 标题控制器
 * 用于获取目标网站的标题
 */
@Tag(name = "URL标题", description = "获取目标网站标题相关接口")
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final UrlTitleService urlTitleService;

    /**
     * 根据 URL 获取网站标题
     *
     * @param url 目标网站 URL
     * @return 网站标题
     */
    @Operation(summary = "获取网站标题", description = "根据URL获取目标网站的标题")
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }
}
