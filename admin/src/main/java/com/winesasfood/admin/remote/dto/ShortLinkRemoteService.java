package com.winesasfood.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winesasfood.admin.common.result.Result;
import com.winesasfood.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.winesasfood.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.winesasfood.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
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
}
