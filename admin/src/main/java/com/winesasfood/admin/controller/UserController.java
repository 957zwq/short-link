package com.winesasfood.admin.controller;

import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.common.result.Results;
import com.winesasfood.admin.dto.req.UserLoginReqDTO;
import com.winesasfood.admin.dto.req.UserRegisterReqDTO;
import com.winesasfood.admin.dto.req.UserUpdateReqDTO;
import com.winesasfood.admin.dto.resp.UserActualRespDTO;
import com.winesasfood.admin.dto.resp.UserLoginRespDTO;
import com.winesasfood.admin.dto.resp.UserRespDTO;
import com.winesasfood.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Operation(summary = "用户修改")
    @PutMapping("/api/shortlink/v1/user")
    public Result<Void> update(@Valid @RequestBody UserUpdateReqDTO request) {
        userService.update(request);
        return Results.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@Valid @RequestBody UserLoginReqDTO request) {
        UserLoginRespDTO response = userService.login(request);
        return Results.success(response);
    }

    @Operation(summary = "检查用户登录状态")
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(
            @Parameter(description = "用户名") @RequestHeader("username") String username,
            @Parameter(description = "登录令牌") @RequestHeader("token") String token) {
        boolean result = userService.checkLogin(username, token);
        return Results.success(result);
    }

    @Operation(summary = "用户登出")
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(
            @Parameter(description = "用户名") @RequestHeader("username") String username,
            @Parameter(description = "登录令牌") @RequestHeader("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }

}
