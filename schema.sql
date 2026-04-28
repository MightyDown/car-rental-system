-- ==================== 用户表 ====================
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    `license_no`  VARCHAR(18)  DEFAULT NULL COMMENT '驾照号',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, ADMIN-管理员',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常, 0-禁用',
    `credit_score` INT          NOT NULL DEFAULT 100 COMMENT '信用分：默认100，低于60冻结租车',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==================== 车辆配置表 ====================
CREATE TABLE IF NOT EXISTS `vehicle_config` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `name`           VARCHAR(50)    NOT NULL COMMENT '配置名称',
    `type`           VARCHAR(20)    NOT NULL COMMENT '类型：economy-经济型, luxury-豪华型',
    `daily_rate`     DECIMAL(10,2)  NOT NULL COMMENT '日租金（元/天）',
    `overtime_rate`  DECIMAL(10,2)  NOT NULL COMMENT '超时费率（元/小时）',
    `free_mileage`   INT            NOT NULL COMMENT '每日免费里程（公里）',
    `mileage_rate`   DECIMAL(10,2)  NOT NULL COMMENT '超里程费率（元/公里）',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆配置表';

-- ==================== 车辆表 ====================
CREATE TABLE IF NOT EXISTS `vehicle` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
    `plate_no`        VARCHAR(20)  NOT NULL COMMENT '车牌号',
    `model`           VARCHAR(50)  NOT NULL COMMENT '车型名称',
    `type`            VARCHAR(20)  NOT NULL COMMENT '类型：economy-经济型, luxury-豪华型',
    `status`          VARCHAR(20)  NOT NULL DEFAULT 'idle' COMMENT '状态：idle-空闲, reserved-已预约, confirmed-已预留, rented-已租用, maintenance-维修中',
    `current_mileage` INT          NOT NULL DEFAULT 0 COMMENT '当前里程（公里）',
    `image`           VARCHAR(255) DEFAULT NULL COMMENT '车辆图片URL',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_plate_no` (`plate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆表';

-- ==================== 预订记录表 ====================
CREATE TABLE IF NOT EXISTS `booking` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '预订ID',
    `booking_no`          VARCHAR(20)  NOT NULL COMMENT '预订编号',
    `user_id`             BIGINT       NOT NULL COMMENT '用户ID',
    `vehicle_id`          BIGINT       NOT NULL COMMENT '车辆ID',
    `planned_pickup_time` DATETIME     NOT NULL COMMENT '计划取车时间',
    `planned_return_time` DATETIME     NOT NULL COMMENT '计划还车时间',
    `planned_days`        INT          NOT NULL COMMENT '计划租用天数',
    `estimated_rent`      DECIMAL(10,2) NOT NULL COMMENT '预计租金快照',
    `actual_pickup_time`  DATETIME     DEFAULT NULL COMMENT '实际取车时间',
    `pickup_mileage`      INT          DEFAULT NULL COMMENT '取车时里程',
    `actual_return_time`  DATETIME     DEFAULT NULL COMMENT '实际还车时间',
    `return_mileage`      INT          DEFAULT NULL COMMENT '还车时里程',
    `overtime_minutes`    INT          DEFAULT NULL COMMENT '超时分钟数（快照）',
    `overtime_fee`        DECIMAL(10,2) DEFAULT NULL COMMENT '超时费（快照）',
    `excess_mileage_fee`  DECIMAL(10,2) DEFAULT NULL COMMENT '超里程费（快照）',
    `total_fee`           DECIMAL(10,2) DEFAULT NULL COMMENT '总费用（快照）',
    `status`              VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待确认, confirmed-已确认, picked_up-已取车, overdue-超时, returned-已还车, cancelled-已取消',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booking_no` (`booking_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_vehicle_id` (`vehicle_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预订记录表';

-- ==================== 事故记录表 ====================
CREATE TABLE IF NOT EXISTS `accident` (
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '事故ID',
    `booking_id`              BIGINT       NOT NULL COMMENT '预订ID',
    `vehicle_id`              BIGINT       NOT NULL COMMENT '车辆ID',
    `description`             VARCHAR(500) NOT NULL COMMENT '事故描述',
    `deduction_points`        INT          DEFAULT NULL COMMENT '扣除信用分值',
    `expected_completion_date` DATE         DEFAULT NULL COMMENT '预计维修完成日期',
    `actual_completion_date`  DATE         DEFAULT NULL COMMENT '实际维修完成日期',
    `status`                  VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理, in_progress-处理中, completed-已完成',
    `create_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                 TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_booking_id` (`booking_id`),
    KEY `idx_vehicle_id` (`vehicle_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事故记录表';
