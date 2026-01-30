package com.winesasfood.project.controller;

import com.winesasfood.project.common.convention.result.Result;
import com.winesasfood.project.common.convention.result.Results;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ShortLinkController {

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {

    }
}