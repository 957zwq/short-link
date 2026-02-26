package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winesasfood.project.common.constant.RedisKeyConstant;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.winesasfood.project.common.convention.exception.ServiceException;
import com.winesasfood.project.common.enums.VailDateTypeEnum;
import com.winesasfood.project.dao.entity.LinkAccessStatsDO;
import com.winesasfood.project.dao.entity.LinkBrowserStatsDO;
import com.winesasfood.project.dao.entity.LinkDeviceStatsDO;
import com.winesasfood.project.dao.entity.LinkLocaleStatsDO;
import com.winesasfood.project.dao.entity.LinkOsStatsDO;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.entity.ShortLinkGotoDO;
import com.winesasfood.project.dao.mapper.LinkAccessStatsMapper;
import com.winesasfood.project.dao.mapper.LinkBrowserStatsMapper;
import com.winesasfood.project.dao.mapper.LinkDeviceStatsMapper;
import com.winesasfood.project.dao.mapper.LinkLocaleStatsMapper;
import com.winesasfood.project.dao.mapper.LinkOsStatsMapper;
import com.winesasfood.project.dao.mapper.ShortLinkGotoMapper;
import com.winesasfood.project.dao.mapper.ShortLinkMapper;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.project.dto.req.ShortLinkUpdateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;
import com.winesasfood.project.service.ShortLinkService;
import com.winesasfood.project.toolkit.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Autowired
    private RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Autowired
    private ShortLinkGotoMapper shortLinkGotoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private LinkAccessStatsMapper linkAccessStatsMapper;

    @Autowired
    private LinkOsStatsMapper linkOsStatsMapper;

    @Autowired
    private LinkLocaleStatsMapper linkLocaleStatsMapper;

    @Autowired
    private LinkBrowserStatsMapper linkBrowserStatsMapper;

    @Autowired
    private LinkDeviceStatsMapper linkDeviceStatsMapper;

    @org.springframework.beans.factory.annotation.Value("${amap.key}")
    private String amapKey;

    @org.springframework.beans.factory.annotation.Value("${amap.ip-url}")
    private String amapIpUrl;

    private static final int MAX_RETRY = 10;

    // 空值缓存过期时间（分钟）
    private static final long NULL_CACHE_EXPIRE = 5;
    // 正常缓存过期时间（分钟）
    private static final long CACHE_EXPIRE = 30;
    // 分布式锁超时时间（秒）
    private static final long LOCK_WAIT_TIME = 10;
    // 分布式锁自动释放时间（秒）
    private static final long LOCK_LEASE_TIME = 5;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        // 参数校验：自定义有效期时必须填写有效期
        if (requestParam.getValidDateType() != null && VailDateTypeEnum.isCustom(requestParam.getValidDateType())) {
            if (requestParam.getValidDate() == null) {
                throw new ServiceException("自定义有效期时，有效期不能为空");
            }
        }

        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;

        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));

        // 创建路由表数据
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();

        // 插入两张表（事务保证一致性）
        baseMapper.insert(shortLinkDO);
        shortLinkGotoMapper.insert(linkGotoDO);

        // 缓存预热：写入 Redis 缓存和布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        String redisKey = RedisKeyConstant.getGotoShortLinkKey(fullShortUrl);
        stringRedisTemplate.opsForValue().set(redisKey, requestParam.getGid(), CACHE_EXPIRE, TimeUnit.MINUTES);
        log.info("[缓存预热] 短链接创建成功，已写入缓存: {}", fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        String shortUri = HashUtil.hashToBase62(originUrl);
        String fullShortUrl = requestParam.getDomain() + "/" + shortUri;

        int retry = 0;
        while (shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            if (retry >= MAX_RETRY) {
                throw new RuntimeException("短链接生成失败，请重试");
            }
            // 原始后缀 + 随机2位字符（数据库short_uri字段只有8位，HashUtil生成4-6位）
            String randomSuffix = IdUtil.randomUUID().substring(0, 2).toLowerCase();
            shortUri = HashUtil.hashToBase62(originUrl) + randomSuffix;
            fullShortUrl = requestParam.getDomain() + "/" + shortUri;
            retry++;
        }
        return shortUri;
    }

    @Override
    public Page<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);

        Page<ShortLinkDO> page = baseMapper.selectPage(new Page<>(requestParam.getCurrent(), requestParam.getSize()), queryWrapper);

        // 转换为响应 DTO
        Page<ShortLinkPageRespDTO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(page.getRecords().stream()
                .map(linkDO -> ShortLinkPageRespDTO.builder()
                        .id(linkDO.getId())
                        .fullShortUrl("http://" + linkDO.getFullShortUrl())
                        .domain(linkDO.getDomain())
                        .shortUri(linkDO.getShortUri())
                        .originUrl(linkDO.getOriginUrl())
                        .gid(linkDO.getGid())
                        .validDateType(linkDO.getValidDateType())
                        .validDate(linkDO.getValidDate())
                        .createTime(linkDO.getCreateTime())
                        .describe(linkDO.getDescribe())
                        .favicon(linkDO.getFavicon())
                        .build())
                .collect(Collectors.toList()));

        return resultPage;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        List<ShortLinkGroupCountQueryRespDTO> resultList = new ArrayList<>();
        
        for (String gid : requestParam) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(ShortLinkDO::getGid, gid)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            Long count = baseMapper.selectCount(queryWrapper);
            
            ShortLinkGroupCountQueryRespDTO dto = new ShortLinkGroupCountQueryRespDTO();
            dto.setGid(gid);
            dto.setShortLinkCount(count.intValue());
            resultList.add(dto);
        }
        
        return resultList;
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 根据完整短链接查询
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
        if (shortLinkDO == null) {
            throw new ServiceException("短链接不存在");
        }

        // 参数校验：自定义有效期时必须填写有效期
        if (requestParam.getValidDateType() != null && VailDateTypeEnum.isCustom(requestParam.getValidDateType())) {
            if (requestParam.getValidDate() == null) {
                throw new ServiceException("自定义有效期时，有效期不能为空");
            }
        }

        // 更新字段
        if (requestParam.getOriginUrl() != null) {
            shortLinkDO.setOriginUrl(requestParam.getOriginUrl());
        }
        if (requestParam.getGid() != null) {
            shortLinkDO.setGid(requestParam.getGid());
        }
        if (requestParam.getValidDateType() != null) {
            shortLinkDO.setValidDateType(requestParam.getValidDateType());
        }
        if (requestParam.getValidDate() != null) {
            shortLinkDO.setValidDate(requestParam.getValidDate());
        }
        if (requestParam.getDescribe() != null) {
            shortLinkDO.setDescribe(requestParam.getDescribe());
        }

        baseMapper.updateById(shortLinkDO);
    }

    /**
     * 尝试短链接跳转
     */
    public boolean tryRedirect(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 构建完整短链接（不带协议）
            String serverName = request.getServerName();
            String fullShortUrl = serverName + "/s/" + shortUri;

            // 1. 布隆过滤器检查 - 防止缓存穿透
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                log.warn("[缓存穿透防护] 布隆过滤器确认不存在: {}", fullShortUrl);
                return false;
            }

            // 2. 查询 Redis 缓存
            String redisKey = RedisKeyConstant.getGotoShortLinkKey(fullShortUrl);
            String cachedGid = stringRedisTemplate.opsForValue().get(redisKey);

            // 检查布隆过滤器误判缓存
            String nullCacheKey = RedisKeyConstant.getGotoIsNullShortLinkKey(fullShortUrl);
            String nullCache = stringRedisTemplate.opsForValue().get(nullCacheKey);
            if (StrUtil.isNotBlank(nullCache)) {
                log.debug("[布隆过滤器误判缓存命中] shortUrl: {}", fullShortUrl);
                return false;
            }

            if (StrUtil.isNotBlank(cachedGid)) {
                // 缓存命中，直接获取 gid
                log.debug("[缓存命中] shortUrl: {}, gid: {}", fullShortUrl, cachedGid);
                return redirectToOriginUrlWithResult(cachedGid, fullShortUrl, request, response, nullCacheKey);
            }

            // 3. 缓存未命中，使用分布式锁防止缓存击穿
            String lockKey = RedisKeyConstant.getLockGotoShortLinkKey(fullShortUrl);
            RLock lock = redissonClient.getLock(lockKey);

            try {
                // 尝试获取锁
                boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
                if (!isLocked) {
                    log.warn("[获取锁失败] shortUrl: {}", fullShortUrl);
                    response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "系统繁忙，请稍后重试");
                    return true; // 已处理响应
                }

                // 双重检查 - 获取锁后再次检查缓存
                cachedGid = stringRedisTemplate.opsForValue().get(redisKey);
                if (StrUtil.isNotBlank(cachedGid)) {
                    if ("null".equals(cachedGid)) {
                        return false;
                    }
                    return redirectToOriginUrlWithResult(cachedGid, fullShortUrl, request, response, nullCacheKey);
                }

                // 4. 查询数据库（goto 表）
                LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);

                if (shortLinkGotoDO == null) {
                    // 布隆过滤器误判：说存在但实际不存在
                    log.warn("[布隆过滤器误判] 实际不存在，写入空值缓存: {}", fullShortUrl);
                    // 写入空值缓存，防止下次误判再次查数据库
                    stringRedisTemplate.opsForValue().set(nullCacheKey, "1", NULL_CACHE_EXPIRE, TimeUnit.MINUTES);
                    return false;
                }

                // 写入 Redis 缓存
                String gid = shortLinkGotoDO.getGid();
                stringRedisTemplate.opsForValue().set(redisKey, gid, CACHE_EXPIRE, TimeUnit.MINUTES);
                log.debug("[缓存写入] shortUrl: {}, gid: {}", fullShortUrl, gid);

                // 5. 查询短链接表并跳转
                return redirectToOriginUrlWithResult(gid, fullShortUrl, request, response, nullCacheKey);

            } finally {
                // 释放锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("[短链接跳转异常] shortUri: {}", shortUri, e);
            return false;
        }
    }

    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        boolean success = tryRedirect(shortUri, (HttpServletRequest) request, (HttpServletResponse) response);
        if (!success) {
            // 跳转失败，重定向到不存在提示页面
            try {
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
            } catch (Exception e) {
                log.error("[重定向失败] shortUri: {}", shortUri, e);
            }
        }
    }

    /**
     * 查询短链接表并重定向到原始链接（返回结果版本）
     * @return true-跳转成功, false-跳转失败
     */
    @SneakyThrows
    private boolean redirectToOriginUrlWithResult(String gid, String fullShortUrl, HttpServletRequest request, HttpServletResponse response, String nullCacheKey) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

        if (shortLinkDO == null) {
            // 短链接已失效或不存在
            return false;
        }

        // 检查有效期
        if (shortLinkDO.getValidDateType() != null && VailDateTypeEnum.isCustom(shortLinkDO.getValidDateType())) {
            if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new java.util.Date())) {
                // 短链接已过期，写入空值缓存，避免下次再次查询数据库
                log.warn("[短链接过期] 写入空值缓存: {}", fullShortUrl);
                stringRedisTemplate.opsForValue().set(nullCacheKey, "1", NULL_CACHE_EXPIRE, TimeUnit.MINUTES);
                return false;
            }
        }

        // 记录访问统计
        shortLinkStats(gid, fullShortUrl, request);

        // 302 跳转到原始链接
        response.sendRedirect(shortLinkDO.getOriginUrl());
        return true;
    }

    /**
     * 记录短链接访问统计
     *
     * @param gid          分组标识
     * @param fullShortUrl 完整短链接
     * @param request      HTTP请求
     */
    private void shortLinkStats(String gid, String fullShortUrl, HttpServletRequest request) {
        try {
            // 获取当前时间信息
            java.util.Date now = new java.util.Date();
            int hour = DateUtil.hour(now, true);
            int weekday = DateUtil.dayOfWeekEnum(now).getIso8601Value();
            String dateStr = DateUtil.format(now, "yyyyMMdd");

            // 获取客户端 IP
            String clientIp = getClientIp(request);

            // 使用 Redis 判断 UV 和 UIP
            String uvKey = RedisKeyConstant.getShortLinkUvKey(fullShortUrl, dateStr);
            String uipKey = RedisKeyConstant.getShortLinkUipKey(fullShortUrl, dateStr);

            // 判断是否为新访客（UV）
            Boolean isNewUv = stringRedisTemplate.opsForSet().add(uvKey, getClientIdentifier(request)) == 1;
            if (isNewUv) {
                stringRedisTemplate.expire(uvKey, 1, TimeUnit.DAYS);
            }

            // 判断是否为新 IP（UIP）
            Boolean isNewUip = stringRedisTemplate.opsForSet().add(uipKey, clientIp) == 1;
            if (isNewUip) {
                stringRedisTemplate.expire(uipKey, 1, TimeUnit.DAYS);
            }

            // 构建统计数据
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .pv(1)
                    .uv(isNewUv ? 1 : 0)
                    .uip(isNewUip ? 1 : 0)
                    .hour(hour)
                    .weekday(weekday)
                    .build();

            // 写入数据库（使用 ON DUPLICATE KEY UPDATE）
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            // 记录操作系统访问统计
            String userAgent = request.getHeader("User-Agent");
            String os = parseOs(userAgent);
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .cnt(1)
                    .os(os)
                    .build();
            linkOsStatsMapper.shortLinkOsStats(linkOsStatsDO);

            // 记录浏览器访问统计
            String browser = parseBrowser(userAgent);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .cnt(1)
                    .browser(browser)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserStats(linkBrowserStatsDO);

            // 记录设备访问统计
            String device = parseDevice(userAgent);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .cnt(1)
                    .device(device)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceStats(linkDeviceStatsDO);

            // 记录地区访问统计
            LinkLocaleStatsDO localeStats = getLocaleByIp(clientIp);
            if (localeStats != null) {
                localeStats.setFullShortUrl(fullShortUrl);
                localeStats.setGid(gid);
                localeStats.setDate(now);
                localeStats.setCnt(1);
                linkLocaleStatsMapper.shortLinkLocaleState(localeStats);
            }

            // 更新短链接总点击量
            baseMapper.incrementClickNum(fullShortUrl);

            log.debug("[访问统计] shortUrl: {}, pv=1, uv={}, uip={}, os={}, browser={}, device={}, province={}, city={}",
                    fullShortUrl, isNewUv ? 1 : 0, isNewUip ? 1 : 0, os, browser, device,
                    localeStats != null ? localeStats.getProvince() : "unknown",
                    localeStats != null ? localeStats.getCity() : "unknown");
        } catch (Exception e) {
            log.error("[访问统计异常] shortUrl: {}", fullShortUrl, e);
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取客户端唯一标识（用于 UV 统计）
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // 优先使用 Cookie 中的标识
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("sl_visitor".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 没有则使用 IP + User-Agent 生成标识
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return cn.hutool.crypto.SecureUtil.md5(ip + userAgent);
    }

    /**
     * 解析 User-Agent 中的操作系统信息
     *
     * @param userAgent User-Agent 字符串
     * @return 操作系统名称
     */
    private String parseOs(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();

        // Windows
        if (userAgent.contains("windows nt 10")) {
            return "Windows 10";
        } else if (userAgent.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("windows nt 6.1")) {
            return "Windows 7";
        } else if (userAgent.contains("windows nt 6.0")) {
            return "Windows Vista";
        } else if (userAgent.contains("windows nt 5.1") || userAgent.contains("windows xp")) {
            return "Windows XP";
        } else if (userAgent.contains("windows")) {
            return "Windows";
        }

        // macOS
        if (userAgent.contains("mac os x")) {
            return "macOS";
        } else if (userAgent.contains("macintosh")) {
            return "Macintosh";
        }

        // Linux
        if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        }

        // 其他
        if (userAgent.contains("crayos")) {
            return "CrayOS";
        } else if (userAgent.contains("solaris")) {
            return "Solaris";
        } else if (userAgent.contains("freebsd")) {
            return "FreeBSD";
        } else if (userAgent.contains("aix")) {
            return "AIX";
        }

        return "Unknown";
    }

    /**
     * 解析 User-Agent 中的浏览器信息
     *
     * @param userAgent User-Agent 字符串
     * @return 浏览器名称
     */
    private String parseBrowser(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();

        // Edge (Chromium)
        if (userAgent.contains("edg/")) {
            return "Edge";
        }

        // Chrome (需要在 Safari 之前判断，因为 Chrome UA 也包含 Safari)
        if (userAgent.contains("chrome/") && !userAgent.contains("chromium")) {
            return "Chrome";
        }

        // Chromium
        if (userAgent.contains("chromium")) {
            return "Chromium";
        }

        // Firefox
        if (userAgent.contains("firefox/")) {
            return "Firefox";
        }

        // Safari (需要在 Chrome 之后判断，因为 Chrome UA 也包含 Safari)
        if (userAgent.contains("safari/") && !userAgent.contains("chrome/")) {
            return "Safari";
        }

        // Opera
        if (userAgent.contains("opr/") || userAgent.contains("opera/")) {
            return "Opera";
        }

        // IE
        if (userAgent.contains("msie") || userAgent.contains("trident/")) {
            return "IE";
        }

        // 360浏览器
        if (userAgent.contains("360se") || userAgent.contains("360ee")) {
            return "360";
        }

        // QQ浏览器
        if (userAgent.contains("qqbrowser/")) {
            return "QQBrowser";
        }

        // UC浏览器
        if (userAgent.contains("ucbrowser/") || userAgent.contains("ubrowser/")) {
            return "UC";
        }

        // 微信内置浏览器
        if (userAgent.contains("micromessenger")) {
            return "WeChat";
        }

        // 微博内置浏览器
        if (userAgent.contains("weibo")) {
            return "Weibo";
        }

        return "Unknown";
    }

    /**
     * 解析 User-Agent 中的设备信息
     *
     * @param userAgent User-Agent 字符串
     * @return 设备类型
     */
    private String parseDevice(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();

        // 平板设备
        if (userAgent.contains("ipad") || userAgent.contains("tablet") || userAgent.contains("playbook")) {
            return "Tablet";
        }

        // 移动设备
        if (userAgent.contains("mobile") || userAgent.contains("iphone") || userAgent.contains("android") ||
                userAgent.contains("phone") || userAgent.contains("ipod") || userAgent.contains("blackberry") ||
                userAgent.contains("windows phone") || userAgent.contains("webos")) {
            return "Mobile";
        }

        // 智能电视
        if (userAgent.contains("smart-tv") || userAgent.contains("smarttv") || userAgent.contains("tv") ||
                userAgent.contains("appletv") || userAgent.contains("roku")) {
            return "TV";
        }

        // 游戏机
        if (userAgent.contains("playstation") || userAgent.contains("xbox") || userAgent.contains("nintendo")) {
            return "GameConsole";
        }

        // 爬虫/机器人
        if (userAgent.contains("bot") || userAgent.contains("spider") || userAgent.contains("crawler") ||
                userAgent.contains("slurp") || userAgent.contains("googlebot")) {
            return "Bot";
        }

        // 默认为PC
        return "PC";
    }

    /**
     * 根据IP地址获取地区信息（调用高德API）
     *
     * @param ip IP地址
     * @return 地区统计信息，失败返回null
     */
    private LinkLocaleStatsDO getLocaleByIp(String ip) {
        try {
            // 跳过局域网IP和本地IP
            if (StrUtil.isBlank(ip) || isLocalIp(ip)) {
                return LinkLocaleStatsDO.builder()
                        .country("中国")
                        .province("局域网")
                        .city("局域网")
                        .adcode("")
                        .build();
            }

            // 构建高德API请求URL
            String requestUrl = String.format("%s?key=%s&ip=%s", amapIpUrl, amapKey, ip);

            // 使用 Hutool 发送HTTP请求
            cn.hutool.http.HttpResponse response = cn.hutool.http.HttpUtil.createGet(requestUrl)
                    .timeout(3000)
                    .execute();

            if (!response.isOk()) {
                log.warn("[高德API请求失败] ip: {}, status: {}", ip, response.getStatus());
                return null;
            }

            String body = response.body();
            cn.hutool.json.JSONObject json = cn.hutool.json.JSONUtil.parseObj(body);

            // 检查返回状态
            String status = json.getStr("status");
            if (!"1".equals(status)) {
                log.warn("[高德API返回错误] ip: {}, info: {}", ip, json.getStr("info"));
                return null;
            }

            String province = json.getStr("province");
            String city = json.getStr("city");
            String adcode = json.getStr("adcode");

            // 处理空值情况（国外IP或非法IP）
            if (StrUtil.isBlank(province)) {
                return LinkLocaleStatsDO.builder()
                        .country("未知")
                        .province("未知")
                        .city("未知")
                        .adcode("")
                        .build();
            }

            // 处理直辖市情况（city可能为空）
            if (StrUtil.isBlank(city) || "[]".equals(city)) {
                city = province;
            }

            return LinkLocaleStatsDO.builder()
                    .country("中国")
                    .province(province)
                    .city(city)
                    .adcode(adcode != null ? adcode : "")
                    .build();

        } catch (Exception e) {
            log.warn("[获取地区信息失败] ip: {}, error: {}", ip, e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否为局域网IP或本地IP
     */
    private boolean isLocalIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return true;
        }
        return ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("172.16.")
                || ip.startsWith("172.17.")
                || ip.startsWith("172.18.")
                || ip.startsWith("172.19.")
                || ip.startsWith("172.20.")
                || ip.startsWith("172.21.")
                || ip.startsWith("172.22.")
                || ip.startsWith("172.23.")
                || ip.startsWith("172.24.")
                || ip.startsWith("172.25.")
                || ip.startsWith("172.26.")
                || ip.startsWith("172.27.")
                || ip.startsWith("172.28.")
                || ip.startsWith("172.29.")
                || ip.startsWith("172.30.")
                || ip.startsWith("172.31.")
                || ip.startsWith("127.")
                || ip.equals("0:0:0:0:0:0:0:1")
                || ip.equals("::1");
    }

    /**
     * 获取网站 favicon
     *
     * @param url 目标网站 URL
     * @return favicon URL，获取失败返回 null
     */
    @SneakyThrows
    private String getFavicon(String url) {
        try {
            // 规范化 URL
            String normalizedUrl = url;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                normalizedUrl = "http://" + url;
            }
            
            // 使用 Jsoup 获取网页并解析 favicon
            org.jsoup.nodes.Document document = org.jsoup.Jsoup.connect(normalizedUrl)
                    .timeout(5000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .followRedirects(true)
                    .get();
            
            // 查找 link 标签中的 icon
            org.jsoup.nodes.Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                String faviconUrl = faviconLink.attr("abs:href");
                if (StrUtil.isNotBlank(faviconUrl)) {
                    log.debug("[获取favicon成功] url: {}, favicon: {}", url, faviconUrl);
                    return faviconUrl;
                }
            }
            
            // 尝试默认路径 /favicon.ico
            java.net.URL targetUrl = new java.net.URL(normalizedUrl);
            String defaultFavicon = targetUrl.getProtocol() + "://" + targetUrl.getHost() + "/favicon.ico";
            
            // 检查默认 favicon 是否存在
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(defaultFavicon).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            if (connection.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                log.debug("[获取默认favicon成功] url: {}, favicon: {}", url, defaultFavicon);
                return defaultFavicon;
            }
            
            return null;
        } catch (Exception e) {
            log.warn("[获取favicon失败] url: {}, error: {}", url, e.getMessage());
            return null;
        }
    }
}