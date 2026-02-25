package com.winesasfood.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winesasfood.project.dto.req.RecycleBinPageReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRecoverReqDTO;
import com.winesasfood.project.dto.req.RecycleBinRemoveReqDTO;
import com.winesasfood.project.dto.req.RecycleBinSaveReqDTO;
import com.winesasfood.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站服务接口
 */
public interface RecycleBinService {

    /**
     * 保存到回收站（软删除）
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 请求参数
     * @return 分页结果
     */
    Page<ShortLinkPageRespDTO> pageShortLink(RecycleBinPageReqDTO requestParam);

    /**
     * 恢复短链接
     *
     * @param requestParam 请求参数
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 移除短链接
     *
     * @param requestParam 请求参数
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
