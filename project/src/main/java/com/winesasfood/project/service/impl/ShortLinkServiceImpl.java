package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.mapper.ShortLinkMapper;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
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
}