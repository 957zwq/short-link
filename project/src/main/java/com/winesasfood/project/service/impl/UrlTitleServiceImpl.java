package com.winesasfood.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.winesasfood.project.service.UrlTitleService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * URL 标题服务实现类
 */
@Slf4j
@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    /**
     * 请求超时时间（毫秒）
     */
    private static final int TIMEOUT = 5000;

    @Override
    public String getTitleByUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        
        try {
            // 规范化 URL（确保有协议）
            String normalizedUrl = normalizeUrl(url);
            
            // 使用 Jsoup 获取网页内容并解析标题
            Document doc = Jsoup.connect(normalizedUrl)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .followRedirects(true)
                    .get();
            
            String title = doc.title();
            if (StrUtil.isNotBlank(title)) {
                log.debug("[获取标题成功] url: {}, title: {}", url, title);
                return title;
            }
            
            // 如果 title 为空，尝试获取 og:title meta 标签
            title = doc.select("meta[property=og:title]").attr("content");
            if (StrUtil.isNotBlank(title)) {
                log.debug("[获取og:title成功] url: {}, title: {}", url, title);
                return title;
            }
            
            return null;
        } catch (Exception e) {
            log.warn("[获取标题失败] url: {}, error: {}", url, e.getMessage());
            return null;
        }
    }
    
    /**
     * 规范化 URL，确保有协议前缀
     */
    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}
