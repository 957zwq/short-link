package com.winesasfood.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.winesasfood.project.dao.entity.LinkAccessStatsDO;
import com.winesasfood.project.dao.entity.LinkLocaleStatsDO;
import com.winesasfood.project.dao.mapper.*;
import com.winesasfood.project.dto.req.ShortLinkStatsReqDTO;
import com.winesasfood.project.dto.resp.*;
import com.winesasfood.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 短链接监控统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        // 1. 基础访问数据（每日PV/UV/UIP）
        List<LinkAccessStatsDO> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(requestParam);
        if (CollUtil.isEmpty(listStatsByShortLink)) {
            return buildEmptyStats();
        }

        // 每日访问详情
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        // 汇总PV/UV/UIP
        AtomicInteger totalPv = new AtomicInteger(0);
        AtomicInteger totalUv = new AtomicInteger(0);
        AtomicInteger totalUip = new AtomicInteger(0);

        listStatsByShortLink.forEach(each -> {
            ShortLinkStatsAccessDailyRespDTO dailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                    .date(each.getDate())
                    .pv(each.getPv())
                    .uv(each.getUv())
                    .uip(each.getUip())
                    .build();
            daily.add(dailyRespDTO);
            totalPv.addAndGet(each.getPv());
            totalUv.addAndGet(each.getUv());
            totalUip.addAndGet(each.getUip());
        });

        // 2. 地区访问详情（国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
        List<LinkLocaleStatsDO> listLocaleByShortLink = linkLocaleStatsMapper.listLocaleByShortLink(requestParam);
        int localeCnSum = listLocaleByShortLink.stream().mapToInt(LinkLocaleStatsDO::getCnt).sum();
        listLocaleByShortLink.forEach(each -> {
            double ratio = localeCnSum == 0 ? 0 : (double) each.getCnt() / localeCnSum;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(each.getCnt())
                    .locale(each.getProvince())
                    .ratio(Math.round(ratio * 100.0) / 100.0)
                    .build();
            localeCnStats.add(localeCNRespDTO);
        });

        // 3. 小时访问详情（0-23小时）
        List<Integer> hourStats = new ArrayList<>(Collections.nCopies(24, 0));
        List<HashMap<String, Object>> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        for (HashMap<String, Object> each : listHourStatsByShortLink) {
            Integer hour = (Integer) each.get("hour");
            Integer pv = ((Number) each.get("pv")).intValue();
            hourStats.set(hour, pv);
        }

        // 4. 一周访问详情（周一到周日）
        List<Integer> weekdayStats = new ArrayList<>(Collections.nCopies(7, 0));
        List<HashMap<String, Object>> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(requestParam);
        for (HashMap<String, Object> each : listWeekdayStatsByShortLink) {
            Integer weekday = (Integer) each.get("weekday");
            Integer pv = ((Number) each.get("pv")).intValue();
            // weekday: 1-7 对应周一到周日，转换为索引 0-6
            weekdayStats.set(weekday - 1, pv);
        }

        // 5. 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByShortLink = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
        listTopIpByShortLink.forEach(each -> {
            ShortLinkStatsTopIpRespDTO topIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip((String) each.get("ip"))
                    .cnt(((Number) each.get("cnt")).intValue())
                    .build();
            topIpStats.add(topIpRespDTO);
        });

        // 6. 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByShortLink = linkBrowserStatsMapper.listBrowserStatsByShortLink(requestParam);
        int browserSum = listBrowserStatsByShortLink.stream().mapToInt(each -> ((Number) each.get("cnt")).intValue()).sum();
        listBrowserStatsByShortLink.forEach(each -> {
            int cnt = ((Number) each.get("cnt")).intValue();
            double ratio = browserSum == 0 ? 0 : (double) cnt / browserSum;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .browser((String) each.get("browser"))
                    .cnt(cnt)
                    .ratio(Math.round(ratio * 100.0) / 100.0)
                    .build();
            browserStats.add(browserRespDTO);
        });

        // 7. 操作系统访问详情
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByShortLink = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        int osSum = listOsStatsByShortLink.stream().mapToInt(each -> ((Number) each.get("cnt")).intValue()).sum();
        listOsStatsByShortLink.forEach(each -> {
            int cnt = ((Number) each.get("cnt")).intValue();
            double ratio = osSum == 0 ? 0 : (double) cnt / osSum;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .os((String) each.get("os"))
                    .cnt(cnt)
                    .ratio(Math.round(ratio * 100.0) / 100.0)
                    .build();
            osStats.add(osRespDTO);
        });

        // 8. 设备类型访问详情
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<HashMap<String, Object>> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
        int deviceSum = listDeviceStatsByShortLink.stream().mapToInt(each -> ((Number) each.get("cnt")).intValue()).sum();
        listDeviceStatsByShortLink.forEach(each -> {
            int cnt = ((Number) each.get("cnt")).intValue();
            double ratio = deviceSum == 0 ? 0 : (double) cnt / deviceSum;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .device((String) each.get("device"))
                    .cnt(cnt)
                    .ratio(Math.round(ratio * 100.0) / 100.0)
                    .build();
            deviceStats.add(deviceRespDTO);
        });

        // 9. 网络类型访问详情
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<HashMap<String, Object>> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(requestParam);
        int networkSum = listNetworkStatsByShortLink.stream().mapToInt(each -> ((Number) each.get("cnt")).intValue()).sum();
        listNetworkStatsByShortLink.forEach(each -> {
            int cnt = ((Number) each.get("cnt")).intValue();
            double ratio = networkSum == 0 ? 0 : (double) cnt / networkSum;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .network((String) each.get("network"))
                    .cnt(cnt)
                    .ratio(Math.round(ratio * 100.0) / 100.0)
                    .build();
            networkStats.add(networkRespDTO);
        });

        // 10. 新老访客统计
        List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
        List<HashMap<String, Object>> listUvTypeByShortLink = linkAccessLogsMapper.listUvTypeByShortLink(requestParam);
        List<HashMap<String, Object>> listUserFirstAccessTime = linkAccessLogsMapper.listUserFirstAccessTime(requestParam.getFullShortUrl());

        // 构建用户首次访问时间Map
        Map<String, Date> userFirstAccessMap = new HashMap<>();
        listUserFirstAccessTime.forEach(each -> {
            String user = (String) each.get("user");
            Date firstAccessTime = (Date) each.get("first_access_time");
            userFirstAccessMap.put(user, firstAccessTime);
        });

        // 统计新老访客
        int newUserCount = 0;
        int oldUserCount = 0;
        Date startDate = DateUtil.parse(requestParam.getStartDate(), "yyyy-MM-dd");
        for (HashMap<String, Object> each : listUvTypeByShortLink) {
            String user = (String) each.get("user");
            Date firstAccessTime = userFirstAccessMap.get(user);
            if (firstAccessTime != null && !firstAccessTime.before(startDate)) {
                // 首次访问时间在查询开始日期之后，为新访客
                newUserCount++;
            } else {
                oldUserCount++;
            }
        }
        int uvSum = newUserCount + oldUserCount;
        if (uvSum > 0) {
            double newRatio = (double) newUserCount / uvSum;
            double oldRatio = (double) oldUserCount / uvSum;
            uvTypeStats.add(ShortLinkStatsUvRespDTO.builder()
                    .uvType("newUser")
                    .cnt(newUserCount)
                    .ratio(Math.round(newRatio * 100.0) / 100.0)
                    .build());
            uvTypeStats.add(ShortLinkStatsUvRespDTO.builder()
                    .uvType("oldUser")
                    .cnt(oldUserCount)
                    .ratio(Math.round(oldRatio * 100.0) / 100.0)
                    .build());
        }

        return ShortLinkStatsRespDTO.builder()
                .pv(totalPv.get())
                .uv(totalUv.get())
                .uip(totalUip.get())
                .daily(daily)
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .uvTypeStats(uvTypeStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .build();
    }

    /**
     * 构建空统计数据
     */
    private ShortLinkStatsRespDTO buildEmptyStats() {
        return ShortLinkStatsRespDTO.builder()
                .pv(0)
                .uv(0)
                .uip(0)
                .daily(new ArrayList<>())
                .localeCnStats(new ArrayList<>())
                .hourStats(new ArrayList<>(Collections.nCopies(24, 0)))
                .topIpStats(new ArrayList<>())
                .weekdayStats(new ArrayList<>(Collections.nCopies(7, 0)))
                .browserStats(new ArrayList<>())
                .osStats(new ArrayList<>())
                .uvTypeStats(new ArrayList<>())
                .deviceStats(new ArrayList<>())
                .networkStats(new ArrayList<>())
                .build();
    }
}
