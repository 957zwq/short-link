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
     * 短链接跳转路由空值缓存 Key（布隆过滤器误判后使用）
     * %s: fullShortUrl（完整短链接）
     */
    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short:link:is-null:goto:%s";

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
     * 短链接 UV 统计 Key
     * %s: fullShortUrl（完整短链接）
     * %s: yyyyMMdd 格式的日期
     */
    public static final String SHORT_LINK_UV_KEY = "short:link:uv:%s:%s";

    /**
     * 短链接 UIP 统计 Key
     * %s: fullShortUrl（完整短链接）
     * %s: yyyyMMdd 格式的日期
     */
    public static final String SHORT_LINK_UIP_KEY = "short:link:uip:%s:%s";

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
     * 获取短链接跳转路由空值缓存 Key
     *
     * @param fullShortUrl 完整短链接
     * @return Redis Key
     */
    public static String getGotoIsNullShortLinkKey(String fullShortUrl) {
        return String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl);
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

    /**
     * 获取短链接 UV 统计 Key
     *
     * @param fullShortUrl 完整短链接
     * @param date         yyyyMMdd 格式的日期
     * @return Redis UV Key
     */
    public static String getShortLinkUvKey(String fullShortUrl, String date) {
        return String.format(SHORT_LINK_UV_KEY, fullShortUrl, date);
    }

    /**
     * 获取短链接 UIP 统计 Key
     *
     * @param fullShortUrl 完整短链接
     * @param date         yyyyMMdd 格式的日期
     * @return Redis UIP Key
     */
    public static String getShortLinkUipKey(String fullShortUrl, String date) {
        return String.format(SHORT_LINK_UIP_KEY, fullShortUrl, date);
    }
}
