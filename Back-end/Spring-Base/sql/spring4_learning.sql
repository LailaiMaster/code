/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50715
 Source Host           : localhost:3306
 Source Schema         : spring4_learning

 Target Server Type    : MySQL
 Target Server Version : 50715
 File Encoding         : 65001

 Date: 27/01/2021 18:18:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for p1_get_started_account
-- ----------------------------
DROP TABLE IF EXISTS `p1_get_started_account`;
CREATE TABLE `p1_get_started_account`  (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `money` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p1_get_started_user
-- ----------------------------
DROP TABLE IF EXISTS `p1_get_started_user`;
CREATE TABLE `p1_get_started_user`  (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p1_get_started_user
-- ----------------------------
INSERT INTO `p1_get_started_user` VALUES (1, 'jordan');
INSERT INTO `p1_get_started_user` VALUES (2, 'jordan01');
INSERT INTO `p1_get_started_user` VALUES (3, 'jordan012');
INSERT INTO `p1_get_started_user` VALUES (4, 'jordan012');
INSERT INTO `p1_get_started_user` VALUES (5, 'jordan01333333332');
INSERT INTO `p1_get_started_user` VALUES (6, 'zz');

-- ----------------------------
-- Table structure for p4_jdbc_departments
-- ----------------------------
DROP TABLE IF EXISTS `p4_jdbc_departments`;
CREATE TABLE `p4_jdbc_departments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p4_jdbc_employees
-- ----------------------------
DROP TABLE IF EXISTS `p4_jdbc_employees`;
CREATE TABLE `p4_jdbc_employees`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p4_jdbc_employees
-- ----------------------------
INSERT INTO `p4_jdbc_employees` VALUES (1, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (2, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (3, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (4, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (5, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (6, 'XYZ', 'xyz@sina.com', 3);
INSERT INTO `p4_jdbc_employees` VALUES (7, 'XYZ', 'xyz@sina.com', 3);

-- ----------------------------
-- Table structure for p4_tx_account
-- ----------------------------
DROP TABLE IF EXISTS `p4_tx_account`;
CREATE TABLE `p4_tx_account`  (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `balance` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p4_tx_account
-- ----------------------------
INSERT INTO `p4_tx_account` VALUES ('AA', 0);
INSERT INTO `p4_tx_account` VALUES ('BB', 100);

-- ----------------------------
-- Table structure for p4_tx_book
-- ----------------------------
DROP TABLE IF EXISTS `p4_tx_book`;
CREATE TABLE `p4_tx_book`  (
  `isbn` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `book_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `price` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`isbn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p4_tx_book
-- ----------------------------
INSERT INTO `p4_tx_book` VALUES ('1001', '天书', 100);
INSERT INTO `p4_tx_book` VALUES ('1002', '地书', 100);

-- ----------------------------
-- Table structure for p4_tx_book_stock
-- ----------------------------
DROP TABLE IF EXISTS `p4_tx_book_stock`;
CREATE TABLE `p4_tx_book_stock`  (
  `isbn` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `stock` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`isbn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p4_tx_book_stock
-- ----------------------------
INSERT INTO `p4_tx_book_stock` VALUES ('1001', 1);
INSERT INTO `p4_tx_book_stock` VALUES ('1002', 2);

-- ----------------------------
-- Table structure for p5_spring_annotation_tx
-- ----------------------------
DROP TABLE IF EXISTS `p5_spring_annotation_tx`;
CREATE TABLE `p5_spring_annotation_tx`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p5_spring_annotation_tx
-- ----------------------------
INSERT INTO `p5_spring_annotation_tx` VALUES (2, '9f73f', '19');

SET FOREIGN_KEY_CHECKS = 1;
