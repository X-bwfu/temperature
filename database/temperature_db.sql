/*
 Navicat Premium Data Transfer

 Source Server         : temperature
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : temperature_db

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 25/11/2025 21:02:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for login_logs
-- ----------------------------
DROP TABLE IF EXISTS `login_logs`;
CREATE TABLE `login_logs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `success` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `login_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_logs
-- ----------------------------
INSERT INTO `login_logs` VALUES (1, 1, '2025-11-19 12:25:00', '192.168.1.10', 1);
INSERT INTO `login_logs` VALUES (2, 1, '2025-11-19 12:25:00', '192.168.1.11', 0);
INSERT INTO `login_logs` VALUES (3, 2, '2025-11-19 12:25:00', '192.168.1.12', 1);

-- ----------------------------
-- Table structure for password_reset_tokens
-- ----------------------------
DROP TABLE IF EXISTS `password_reset_tokens`;
CREATE TABLE `password_reset_tokens`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(1) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token`(`token`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `password_reset_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of password_reset_tokens
-- ----------------------------
INSERT INTO `password_reset_tokens` VALUES (1, 1, 'token12345', '2025-11-12 12:00:00', 0, '2025-11-19 12:25:00');
INSERT INTO `password_reset_tokens` VALUES (2, 2, 'token67890', '2025-11-12 12:00:00', 0, '2025-11-19 12:25:00');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'staff', '工作人员');
INSERT INTO `roles` VALUES (2, 'user', '其他人员');

-- ----------------------------
-- Table structure for sensor_data
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data`;
CREATE TABLE `sensor_data`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sensor_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sensor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `timestamp` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `temperature` double NULL DEFAULT NULL,
  `humidity` double NULL DEFAULT NULL,
  `pressure` double NULL DEFAULT NULL,
  `predicted_temperature` double NULL DEFAULT NULL,
  `alarm_triggered` tinyint(1) NULL DEFAULT NULL,
  `alarm_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `is_uploaded` tinyint(1) NULL DEFAULT NULL,
  `storage_level` enum('REALTIME','MINUTELY_COMPACTED','HOURLY_COMPACTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'REALTIME',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sensor_data
-- ----------------------------
INSERT INTO `sensor_data` VALUES (1, 'jetson-001', 'cold-room-01', '冷藏库1号', '2025-11-16 19:50:00.123', 25.5, 60.2, 1012.3, 25.8, 1, '温度过高: 35.50°C (阈值: 30.0°C)', 1, 'REALTIME', '2025-11-17 19:26:02', '2025-11-19 20:27:40');
INSERT INTO `sensor_data` VALUES (2, 'jetson-001', 'cold-room-01', '冷藏库1号', '2025-11-16 19:51:10.456', 26.1, 59.8, 1012.1, 26.3, 0, NULL, 1, 'REALTIME', '2025-11-17 19:26:02', '2025-11-19 20:27:40');
INSERT INTO `sensor_data` VALUES (3, 'jetson-002', 'cold-room-02', '冷藏库2号', '2025-11-16 19:52:20.789', 28.4, 63, 1011.8, 28.7, 1, '温度过高: 38.20°C (阈值: 30.0°C)', 1, 'REALTIME', '2025-11-17 19:26:02', '2025-11-19 20:27:40');
INSERT INTO `sensor_data` VALUES (4, 'jetson-001', 'cold-room-01', '冷藏库1号', '2025-11-16 19:53:30.100', 24.9, 58, 1012.5, 25, 0, NULL, 1, 'REALTIME', '2025-11-17 19:26:02', '2025-11-19 20:27:40');
INSERT INTO `sensor_data` VALUES (5, 'jetson-003', 'cold-room-03', '冷藏库3号', '2025-11-16 19:54:45.333', 31.2, 66.5, 1011.5, 31.8, 1, '温度过高: 41.10°C (阈值: 30.0°C)', 1, 'REALTIME', '2025-11-17 19:26:02', '2025-11-19 20:27:40');

-- ----------------------------
-- Table structure for sensor_data_copy1
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data_copy1`;
CREATE TABLE `sensor_data_copy1`  (
  `sensor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sensor_data_copy1
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('staff','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'alice', '$2a$10$7qj5Jz.5l8H9KcL6yR3HeO9OGZ2F7kZypfO3E2IojPnX1lGZx9XzO', 'staff', 'alice@example.com', '2025-11-19 12:25:00', '2025-11-19 12:25:00');
INSERT INTO `users` VALUES (2, 'bob', '$2a$10$N9qo8uLOickgx2ZMRZo4i.1qPdM6hM5O3B8rpeplcIYxQ8Vd7WZb6', 'user', 'bob@example.com', '2025-11-19 12:25:00', '2025-11-19 12:25:00');
INSERT INTO `users` VALUES (3, 'aaa', '$2a$10$grDDu4SKJuZe.tw.KrWmrOMH7VhVXZXxev0hxwJK7Ak80LTeTlUJu', 'user', 'kuanfengmie@qq.com', '2025-11-19 14:15:27', '2025-11-19 20:05:29');
INSERT INTO `users` VALUES (4, 'aaaa', '$2a$10$4s8n9qj/6T0i5ZkB8uixy.BYGCAlRgpjQdxwSwVmdAd5CC5Lg3iM2', 'user', 'kuanfengmie@qq.com', '2025-11-19 14:19:31', '2025-11-19 19:58:04');

SET FOREIGN_KEY_CHECKS = 1;
