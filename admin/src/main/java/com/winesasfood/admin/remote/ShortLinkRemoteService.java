package com.winesasfood.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.winesasfood.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkStatsRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接远程调用服务
 */
public interface ShortLinkRemoteService {

    String PROJECT_HOST = "http://127.0.0.1:8001";

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post(PROJECT_HOST + "/api/short-link/v1/create", JSONUtil.toJsonStr(requestParam));
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<ShortLinkCreateRespDTO> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        if (resultJson.getJSONObject("data") != null) {
            ShortLinkCreateRespDTO data = resultJson.getJSONObject("data").toBean(ShortLinkCreateRespDTO.class);
            result.setData(data);
        }
        return result;
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页短链接请求参数
     * @return 查询短链接响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get(PROJECT_HOST + "/api/short-link/v1/page", requestMap);
        JSONObject resultJson = JSONUtil.parseObj(resultPageStr);
        Result<IPage<ShortLinkPageRespDTO>> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        if (resultJson.getJSONObject("data") != null) {
            JSONObject data = resultJson.getJSONObject("data");
            Page<ShortLinkPageRespDTO> page = new Page<>(data.getLong("current"), data.getLong("size"), data.getLong("total"));
            page.setRecords(data.getJSONArray("records").toList(ShortLinkPageRespDTO.class));
            result.setData(page);
        }
        return result;
    }

    /**
     * 查询分组短链接数量
     *
     * @param requestParam 分组标识列表
     * @return 分组短链接数量列表
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam) {
        String resultBodyStr = HttpUtil.get(PROJECT_HOST + "/api/short-link/v1/count", 
                cn.hutool.core.map.MapUtil.of("requestParam", cn.hutool.core.collection.CollUtil.join(requestParam, ",")));
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<List<ShortLinkGroupCountQueryRespDTO>> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        if (resultJson.getJSONArray("data") != null) {
            List<ShortLinkGroupCountQueryRespDTO> data = resultJson.getJSONArray("data").toList(ShortLinkGroupCountQueryRespDTO.class);
            result.setData(data);
        }
        return result;
    }

    /**
     * 更新短链接
     *
     * @param requestParam 更新短链接请求参数
     * @return 更新结果
     */
    default Result<Void> updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        String resultBodyStr = HttpRequest.put(PROJECT_HOST + "/api/short-link/v1/update")
                .body(JSONUtil.toJsonStr(requestParam))
                .execute()
                .body();
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<Void> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        return result;
    }

    /**
     * 保存到回收站
     *
     * @param requestParam 保存请求参数
     * @return 保存结果
     */
    default Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post(PROJECT_HOST + "/api/short-link/v1/recycle-bin/save", JSONUtil.toJsonStr(requestParam));
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<Void> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        return result;
    }

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页请求参数
     * @return 分页结果
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get(PROJECT_HOST + "/api/short-link/v1/recycle-bin/page", requestMap);
        JSONObject resultJson = JSONUtil.parseObj(resultPageStr);
        Result<IPage<ShortLinkPageRespDTO>> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        if (resultJson.getJSONObject("data") != null) {
            JSONObject data = resultJson.getJSONObject("data");
            Page<ShortLinkPageRespDTO> page = new Page<>(data.getLong("current"), data.getLong("size"), data.getLong("total"));
            page.setRecords(data.getJSONArray("records").toList(ShortLinkPageRespDTO.class));
            result.setData(page);
        }
        return result;
    }

    /**
     * 恢复短链接
     *
     * @param requestParam 恢复请求参数
     * @return 恢复结果
     */
    default Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post(PROJECT_HOST + "/api/short-link/v1/recycle-bin/recover", JSONUtil.toJsonStr(requestParam));
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<Void> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        return result;
    }

    /**
     * 移除短链接
     *
     * @param requestParam 移除请求参数
     * @return 移除结果
     */
    default Result<Void> removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post(PROJECT_HOST + "/api/short-link/v1/recycle-bin/remove", JSONUtil.toJsonStr(requestParam));
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<Void> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        return result;
    }

    /**
     * 获取单个短链接监控统计数据
     *
     * @param requestParam 统计请求参数
     * @return 短链接统计数据
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl", requestParam.getFullShortUrl());
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("startDate", requestParam.getStartDate());
        requestMap.put("endDate", requestParam.getEndDate());
        String resultBodyStr = HttpUtil.get(PROJECT_HOST + "/api/short-link/v1/stats", requestMap);
        JSONObject resultJson = JSONUtil.parseObj(resultBodyStr);
        Result<ShortLinkStatsRespDTO> result = new Result<>();
        result.setCode(resultJson.getStr("code"));
        result.setMessage(resultJson.getStr("message"));
        result.setRequestId(resultJson.getStr("requestId"));
        if (resultJson.getJSONObject("data") != null) {
            ShortLinkStatsRespDTO data = resultJson.getJSONObject("data").toBean(ShortLinkStatsRespDTO.class);
            result.setData(data);
        }
        return result;
    }
}
