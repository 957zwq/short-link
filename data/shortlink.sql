DROP DATABASE IF EXISTS `link`;
CREATE DATABASE `link` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE TABLE `t_user` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                          `username` varchar(256) DEFAULT NULL COMMENT '用户名',
                          `password` varchar(512) DEFAULT NULL COMMENT '密码',
                          `real_name` varchar(256) DEFAULT NULL COMMENT '真实姓名',
                          `phone` varchar(128) DEFAULT NULL COMMENT '手机号',
                          `mail` varchar(512) DEFAULT NULL COMMENT '邮箱',
                          `deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳',
                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                          `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                          `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_group` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                           `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
                           `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
                           `sort_order` int(3) DEFAULT NULL COMMENT '分组排序',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `idx_unique_username_gid` (`gid`,`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;;


CREATE TABLE `t_link` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                          `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                          `short_uri` varchar(8) DEFAULT NULL COMMENT '短链接',
                          `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                          `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                          `click_num` int(11) DEFAULT 0 COMMENT '点击量',
                          `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                          `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                          `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                          `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                          `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                          `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                          `describe` varchar(1024) DEFAULT NULL COMMENT '描述',
                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                          `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                          `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 短链接分表 t_link_0 ~ t_link_15（按 gid 分片）
CREATE TABLE `t_link_0` LIKE `t_link`;
CREATE TABLE `t_link_1` LIKE `t_link`;
CREATE TABLE `t_link_2` LIKE `t_link`;
CREATE TABLE `t_link_3` LIKE `t_link`;
CREATE TABLE `t_link_4` LIKE `t_link`;
CREATE TABLE `t_link_5` LIKE `t_link`;
CREATE TABLE `t_link_6` LIKE `t_link`;
CREATE TABLE `t_link_7` LIKE `t_link`;
CREATE TABLE `t_link_8` LIKE `t_link`;
CREATE TABLE `t_link_9` LIKE `t_link`;
CREATE TABLE `t_link_10` LIKE `t_link`;
CREATE TABLE `t_link_11` LIKE `t_link`;
CREATE TABLE `t_link_12` LIKE `t_link`;
CREATE TABLE `t_link_13` LIKE `t_link`;
CREATE TABLE `t_link_14` LIKE `t_link`;
CREATE TABLE `t_link_15` LIKE `t_link`;



CREATE TABLE `t_link_access_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `pv`             int(11) DEFAULT NULL COMMENT '访问量',
    `uv`             int(11) DEFAULT NULL COMMENT '独立访客数',
    `uip`            int(11) DEFAULT NULL COMMENT '独立IP数',
    `hour`           int(3) DEFAULT NULL COMMENT '小时',
    `weekday`        int(3) DEFAULT NULL COMMENT '星期',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_access_stats` (`full_short_url`,`date`,`hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_link_os_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `os`             varchar(64)  DEFAULT NULL COMMENT '操作系统',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_os_stats` (`full_short_url`,`date`,`os`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_link_locale_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `province`       varchar(64)  DEFAULT NULL COMMENT '省份名称',
    `city`           varchar(64)  DEFAULT NULL COMMENT '市名称',
    `adcode`         varchar(64)  DEFAULT NULL COMMENT '城市编码',
    `country`        varchar(64)  DEFAULT NULL COMMENT '国家标识',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`,`date`,`adcode`,`province`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_link_browser_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`browser`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_link_access_logs`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `user`           varchar(64)  DEFAULT NULL COMMENT '用户信息',
    `ip`             varchar(64)  DEFAULT NULL COMMENT 'IP',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `os`             varchar(64)  DEFAULT NULL COMMENT '操作系统',
    `network`        varchar(64)  DEFAULT NULL COMMENT '访问网络',
    `device`         varchar(64)  DEFAULT NULL COMMENT '访问设备',
    `locale`         varchar(256) DEFAULT NULL COMMENT '地区',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY              `idx_full_short_url` (`full_short_url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_link_device_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11) DEFAULT NULL COMMENT '访问量',
    `device`         varchar(64)  DEFAULT NULL COMMENT '访问设备',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`device`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;