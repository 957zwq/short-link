package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
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
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.entity.ShortLinkGotoDO;
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
                        .favicon(null)  // 暂未实现
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

    @SneakyThrows
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        // 构建完整短链接（不带协议）
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;

        // 1. 布隆过滤器检查 - 防止缓存穿透（不存在的短链接直接返回）
        if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            log.warn("[缓存穿透防护] 布隆过滤器确认不存在: {}", fullShortUrl);
            // 布隆过滤器说"不存在"就是一定不存在，直接返回404
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
            return;
        }

        // 2. 查询 Redis 缓存
        String redisKey = RedisKeyConstant.getGotoShortLinkKey(fullShortUrl);
        String cachedGid = stringRedisTemplate.opsForValue().get(redisKey);

        // 检查布隆过滤器误判缓存
        String nullCacheKey = RedisKeyConstant.getGotoIsNullShortLinkKey(fullShortUrl);
        String nullCache = stringRedisTemplate.opsForValue().get(nullCacheKey);
        if (StrUtil.isNotBlank(nullCache)) {
            log.debug("[布隆过滤器误判缓存命中] shortUrl: {}", fullShortUrl);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
            return;
        }

        if (StrUtil.isNotBlank(cachedGid)) {
            // 缓存命中，直接获取 gid
            log.debug("[缓存命中] shortUrl: {}, gid: {}", fullShortUrl, cachedGid);
            // 查询短链接表并跳转
            redirectToOriginUrl(cachedGid, fullShortUrl, response, nullCacheKey);
            return;
        }

        // 3. 缓存未命中，使用分布式锁防止缓存击穿
        String lockKey = RedisKeyConstant.getLockGotoShortLinkKey(fullShortUrl);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                log.warn("[获取锁失败] shortUrl: {}", fullShortUrl);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "系统繁忙，请稍后重试");
                return;
            }

            // 双重检查 - 获取锁后再次检查缓存
            cachedGid = stringRedisTemplate.opsForValue().get(redisKey);
            if (StrUtil.isNotBlank(cachedGid)) {
                if ("null".equals(cachedGid)) {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
                    return;
                }
                redirectToOriginUrl(cachedGid, fullShortUrl, response, nullCacheKey);
                return;
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
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
                return;
            }

            // 写入 Redis 缓存
            String gid = shortLinkGotoDO.getGid();
            stringRedisTemplate.opsForValue().set(redisKey, gid, CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("[缓存写入] shortUrl: {}, gid: {}", fullShortUrl, gid);

            // 5. 查询短链接表并跳转
            redirectToOriginUrl(gid, fullShortUrl, response, nullCacheKey);

        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 查询短链接表并重定向到原始链接
     */
    @SneakyThrows
    private void redirectToOriginUrl(String gid, String fullShortUrl, ServletResponse response, String nullCacheKey) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

        if (shortLinkDO == null) {
            // 短链接已失效或不存在
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接已失效");
            return;
        }

        // 检查有效期
        if (shortLinkDO.getValidDateType() != null && VailDateTypeEnum.isCustom(shortLinkDO.getValidDateType())) {
            if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new java.util.Date())) {
                // 短链接已过期，写入空值缓存，避免下次再次查询数据库
                log.warn("[短链接过期] 写入空值缓存: {}", fullShortUrl);
                stringRedisTemplate.opsForValue().set(nullCacheKey, "1", NULL_CACHE_EXPIRE, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_GONE, "短链接已过期");
                return;
            }
        }

        // 302 跳转到原始链接
        ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
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
                return redirectToOriginUrlWithResult(cachedGid, fullShortUrl, response, nullCacheKey);
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
                    return redirectToOriginUrlWithResult(cachedGid, fullShortUrl, response, nullCacheKey);
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
                return redirectToOriginUrlWithResult(gid, fullShortUrl, response, nullCacheKey);

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

    /**
     * 查询短链接表并重定向到原始链接（返回结果版本）
     * @return true-跳转成功, false-跳转失败
     */
    @SneakyThrows
    private boolean redirectToOriginUrlWithResult(String gid, String fullShortUrl, HttpServletResponse response, String nullCacheKey) {
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

        // 302 跳转到原始链接
        response.sendRedirect(shortLinkDO.getOriginUrl());
        return true;
    }
}