package com.winesasfood.project.common.constant;

/**
 * Redis Key 常量类
 */
public class RedisKeyConstant {

    /**
     * 短链接跳转路由缓存 Key
     * %s: fullShortUrl（完整短链接，如 suo.im/abc123）
     */
    public static final String GOTO_SHORT_LINK_KEY = "short:link:goto:%s";

    /**
     * 短链接跳转路由分布式锁 Key
     * %s: fullShortUrl（完整短链接）
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "lock:goto:%s";

    /**
     * 短链接创建布隆过滤器名称
     */
    public static final String SHORT_URI_CREATE_BLOOM_FILTER = "shortUriCreateCachePenetrationBloomFilter";

    /**
     * 获取短链接跳转路由缓存 Key
     *
     * @param fullShortUrl 完整短链接
     * @return Redis Key
     */
    public static String getGotoShortLinkKey(String fullShortUrl) {
        return String.format(GOTO_SHORT_LINK_KEY, fullShortUrl);
    }

    /**
     * 获取短链接跳转路由分布式锁 Key
     *
     * @param fullShortUrl 完整短链接
     * @return Redis Lock Key
     */
    public static String getLockGotoShortLinkKey(String fullShortUrl) {
        return String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl);
    }
}
