package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dao.mapper.ShortLinkMapper;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.project.service.ShortLinkService;
import com.winesasfood.project.toolkit.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        return HashUtil.hashToBase62(originUrl);
    }
}