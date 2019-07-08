/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50636
Source Host           : localhost:3306
Source Database       : testdb

Target Server Type    : MYSQL
Target Server Version : 50636
File Encoding         : 65001

Date: 2019-07-08 11:03:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for batch_job
-- ----------------------------
DROP TABLE IF EXISTS `batch_job`;
CREATE TABLE `batch_job` (
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  `JOB_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '批量名称',
  `JAVA_CLASS` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '批量执行类',
  `SEDULER_DATE` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '定时表达式',
  `SUPER_JOB` int(10) DEFAULT NULL COMMENT '父批ID',
  `START_DATE` datetime DEFAULT NULL COMMENT '批量开始执行时间',
  `STATE` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '跑批是否成功0-失败 1-成功',
  `SUCCESS_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
