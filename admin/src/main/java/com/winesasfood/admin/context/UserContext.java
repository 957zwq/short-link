package com.winesasfood.admin.context;

import java.util.Optional;

/**
 * 用户上下文
 */
public final class UserContext {

    private static final ThreadLocal<String> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前登录用户名
     */
    public static void setUsername(String username) {
        USER_THREAD_LOCAL.set(username);
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清除当前登录用户信息
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
