package com.winesasfood.admin.controller;

import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.common.result.Results;
import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.resp.UserActualRespDTO;
import com.winesasfood.admin.dto.resp.UserRespDTO;
import com.winesasfood.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "根据用户名查询用户（脱敏）")
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@Parameter(description = "用户名") @PathVariable("username") String username) {
        UserRespDTO user = userService.getUserByUsername(username);
        return Results.success(user);
    }

    @Operation(summary = "根据用户名查询用户（未脱敏）")
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@Parameter(description = "用户名") @PathVariable("username") String username) {
        UserActualRespDTO user = userService.getActualUserByUsername(username);
        return Results.success(user);
    }

    @Operation(summary = "检查用户名是否存在")
    @GetMapping("/api/shortlink/v1/user/username/{username}/exists")
    public Result<Boolean> checkUsernameExists(@Parameter(description = "用户名") @PathVariable("username") String username) {
        boolean exists = userService.isUsernameExists(username);
        return Results.success(exists);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/api/shortlink/v1/user/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterReqDTO request) {
        userService.register(request);
        return Results.success();
    }
}
