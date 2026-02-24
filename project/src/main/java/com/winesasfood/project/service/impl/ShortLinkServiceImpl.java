package com.winesasfood.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Autowired
    private RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Autowired
    private ShortLinkGotoMapper shortLinkGotoMapper;

    private static final int MAX_RETRY = 10;

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

        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);

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

        // 1. 查询 goto 表获取 gid（按 full_short_url 分片）
        LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);

        if (shortLinkGotoDO == null) {
            // 短链接不存在，返回 404
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
            return;
        }

        // 2. 查询短链接表获取原始链接（按 gid 分片）
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

        if (shortLinkDO == null) {
            // 短链接已失效或不存在
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "短链接已失效");
            return;
        }

        // 3. 检查有效期
        if (shortLinkDO.getValidDateType() != null && VailDateTypeEnum.isCustom(shortLinkDO.getValidDateType())) {
            if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new java.util.Date())) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_GONE, "短链接已过期");
                return;
            }
        }

        // 4. 302 跳转到原始链接
        ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
    }
}