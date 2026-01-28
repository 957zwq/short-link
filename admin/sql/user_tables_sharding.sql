-- ====================================
-- 用户分表建表脚本
-- 总共16张表：t_user_0 ~ t_user_15
-- 分片键：username
-- 分片算法：HASH_MOD
-- ====================================

-- 创建用户分表
CREATE TABLE IF NOT EXISTS `t_user_0` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_0';

CREATE TABLE IF NOT EXISTS `t_user_1` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_1';

CREATE TABLE IF NOT EXISTS `t_user_2` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_2';

CREATE TABLE IF NOT EXISTS `t_user_3` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_3';

CREATE TABLE IF NOT EXISTS `t_user_4` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_4';

CREATE TABLE IF NOT EXISTS `t_user_5` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_5';

CREATE TABLE IF NOT EXISTS `t_user_6` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_6';

CREATE TABLE IF NOT EXISTS `t_user_7` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_7';

CREATE TABLE IF NOT EXISTS `t_user_8` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_8';

CREATE TABLE IF NOT EXISTS `t_user_9` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_9';

CREATE TABLE IF NOT EXISTS `t_user_10` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_10';

CREATE TABLE IF NOT EXISTS `t_user_11` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_11';

CREATE TABLE IF NOT EXISTS `t_user_12` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_12';

CREATE TABLE IF NOT EXISTS `t_user_13` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_13';

CREATE TABLE IF NOT EXISTS `t_user_14` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_14';

CREATE TABLE IF NOT EXISTS `t_user_15` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(256)    NOT NULL COMMENT '用户名',
    `password`    varchar(512)    NOT NULL COMMENT '密码',
    `real_name`   varchar(256)    NOT NULL COMMENT '真实姓名',
    `phone`       varchar(128)    DEFAULT NULL COMMENT '手机号',
    `mail`        varchar(512)    DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)    DEFAULT NULL COMMENT '注销时间戳',
    `create_time` datetime        DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime        DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)      DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_15';
