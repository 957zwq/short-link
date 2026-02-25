package com.winesasfood.project.controller;

import com.winesasfood.project.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 短链接跳转控制器
 * 处理短链接跳转和错误页面展示
 */
@Controller
@RequiredArgsConstructor
public class ShortLinkJumpController {

    private final ShortLinkService shortLinkService;

    /**
     * 短链接跳转入口
     * 路径: /s/{shortUri}
     *
     * @param shortUri 短链接后缀
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param model    视图模型
     * @return 视图名称或重定向
     */
    @SneakyThrows
    @GetMapping("/s/{shortUri}")
    public String jump(@PathVariable("shortUri") String shortUri,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) {
        // 尝试跳转
        boolean success = shortLinkService.tryRedirect(shortUri, request, response);
        
        // 跳转失败，展示错误页面
        if (!success) {
            String fullShortUrl = request.getServerName() + "/s/" + shortUri;
            model.addAttribute("shortUrl", fullShortUrl);
            return "not-found";
        }
        
        // 跳转成功，返回 null（已经通过 response 发送重定向）
        return null;
    }
}
