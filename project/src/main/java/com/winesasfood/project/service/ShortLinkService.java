package com.winesasfood.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import com.winesasfood.project.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.project.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.project.dto.req.ShortLinkUpdateReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询请求参数
     * @return 分页结果
     */
    Page<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组短链接数量
     *
     * @param requestParam 分组标识列表
     * @return 分组短链接数量列表
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 更新短链接
     *
     * @param requestParam 更新短链接请求参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转
     *
     * @param shortUri 短链接后缀
     * @param request  ServletRequest
     * @param response ServletResponse
     */
    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response);
}