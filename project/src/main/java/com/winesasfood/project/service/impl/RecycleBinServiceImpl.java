package com.winesasfood.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winesasfood.project.common.convention.exception.ServiceException;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.entity.ShortLinkGotoDO;
import com.winesasfood.project.dao.mapper.ShortLinkGotoMapper;
import com.winesasfood.project.dao.mapper.ShortLinkMapper;
import com.winesasfood.project.dto.req.RecycleBinPageReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRecoverReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRemoveReqDTO;
import com.winesasfood.project.dto.req.RecycleBinSaveReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;
import com.winesasfood.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 回收站服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存过期时间（分钟）
     */
    private static final long CACHE_EXPIRE = 30;

    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        // 软删除：将 del_flag 设置为 1
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getDelFlag, 1);
        
        int updated = shortLinkMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new ServiceException("短链接不存在或已在回收站中");
        }
        
        // 删除 Redis 缓存
        String cacheKey = "short:link:goto:" + requestParam.getFullShortUrl();
        stringRedisTemplate.delete(cacheKey);
        
        log.info("[移入回收站] gid: {}, fullShortUrl: {}", requestParam.getGid(), requestParam.getFullShortUrl());
    }

    @Override
    public Page<ShortLinkPageRespDTO> pageShortLink(RecycleBinPageReqDTO requestParam) {
        // 查询回收站中的短链接（del_flag = 1）
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 1)
                .orderByDesc(ShortLinkDO::getUpdateTime);

        Page<ShortLinkDO> page = shortLinkMapper.selectPage(
                new Page<>(requestParam.getCurrent(), requestParam.getSize()), 
                queryWrapper);

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
                        .build())
                .collect(Collectors.toList()));

        return resultPage;
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        // 恢复：将 del_flag 设置为 0
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1)
                .set(ShortLinkDO::getDelFlag, 0);
        
        int updated = shortLinkMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new ServiceException("短链接不在回收站中");
        }
        
        // 恢复 Redis 缓存
        String cacheKey = "short:link:goto:" + requestParam.getFullShortUrl();
        stringRedisTemplate.opsForValue().set(cacheKey, requestParam.getGid(), CACHE_EXPIRE, TimeUnit.MINUTES);
        
        log.info("[恢复短链接] gid: {}, fullShortUrl: {}", requestParam.getGid(), requestParam.getFullShortUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        // 物理删除短链接
        LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1);
        
        int deleted = shortLinkMapper.delete(linkQueryWrapper);
        if (deleted == 0) {
            throw new ServiceException("短链接不在回收站中");
        }
        
        // 删除路由表记录
        LambdaQueryWrapper<ShortLinkGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getGid, requestParam.getGid())
                .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl());
        shortLinkGotoMapper.delete(gotoQueryWrapper);
        
        // 删除 Redis 缓存
        String cacheKey = "short:link:goto:" + requestParam.getFullShortUrl();
        stringRedisTemplate.delete(cacheKey);
        
        log.info("[彻底删除短链接] gid: {}, fullShortUrl: {}", requestParam.getGid(), requestParam.getFullShortUrl());
    }
}
