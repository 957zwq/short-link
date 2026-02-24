-- ====================================
-- 短链接分组表分表脚本
-- 总共 16 张表：t_group_0 ~ t_group_15
-- 分片键：username
-- 分片算法：HASH_MOD
-- ====================================

-- 创建分组表
CREATE TABLE IF NOT EXISTS `t_group_0` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_0';

CREATE TABLE IF NOT EXISTS `t_group_1` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_1';

CREATE TABLE IF NOT EXISTS `t_group_2` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_2';

CREATE TABLE IF NOT EXISTS `t_group_3` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_3';

CREATE TABLE IF NOT EXISTS `t_group_4` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_4';

CREATE TABLE IF NOT EXISTS `t_group_5` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_5';

CREATE TABLE IF NOT EXISTS `t_group_6` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_6';

CREATE TABLE IF NOT EXISTS `t_group_7` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_7';

CREATE TABLE IF NOT EXISTS `t_group_8` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_8';

CREATE TABLE IF NOT EXISTS `t_group_9` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_9';

CREATE TABLE IF NOT EXISTS `t_group_10` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_10';

CREATE TABLE IF NOT EXISTS `t_group_11` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_11';

CREATE TABLE IF NOT EXISTS `t_group_12` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_12';

CREATE TABLE IF NOT EXISTS `t_group_13` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_13';

CREATE TABLE IF NOT EXISTS `t_group_14` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_14';

CREATE TABLE IF NOT EXISTS `t_group_15` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)     NOT NULL COMMENT '分组标识',
    `name`        varchar(256)    NOT NULL COMMENT '分组名称',
    `username`    varchar(256)    NOT NULL COMMENT '创建分组用户名',
    `sort_order`  int(11)         DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表_15';
