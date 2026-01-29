package com.winesasfood.admin.interceptor;

import com.winesasfood.admin.common.enums.UserErrorCodeEnum;
import com.winesasfood.admin.common.exception.ClientException;
import com.winesasfood.admin.context.UserContext;
import com.winesasfood.admin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户登录拦截器
 */
@Component
@RequiredArgsConstructor
public class UserLoginInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = request.getHeader("username");
        String token = request.getHeader("token");

        // 验证登录状态
        if (!StringUtils.hasText(username) || !StringUtils.hasText(token)) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }

        boolean isLoggedIn = userService.checkLogin(username, token);
        if (!isLoggedIn) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }

        // 将用户信息存入上下文
        UserContext.setUsername(username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
