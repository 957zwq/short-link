package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.mapper.ShortLinkMapper;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;
import com.winesasfood.project.service.ShortLinkService;
import com.winesasfood.project.toolkit.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Autowired
    private RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    private static final int MAX_RETRY = 10;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setDelFlag(0);
        shortLinkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        baseMapper.insert(shortLinkDO);
        shortUriCreateCachePenetrationBloomFilter.add(shortLinkDO.getFullShortUrl());
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
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
                        .fullShortUrl(linkDO.getFullShortUrl())
                        .domain(linkDO.getDomain())
                        .shortUri(linkDO.getShortUri())
                        .originUrl(linkDO.getOriginUrl())
                        .gid(linkDO.getGid())
                        .validDateType(linkDO.getValidDateType())
                        .validDate(linkDO.getValidDate() != null ? linkDO.getValidDate().toString() : null)
                        .describe(linkDO.getDescribe())
                        .favicon(null)  // 暂未实现
                        .build())
                .collect(Collectors.toList()));

        return resultPage;
    }
}