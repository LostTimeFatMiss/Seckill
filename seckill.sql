/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : seckill

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2017-12-03 15:26:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone7', '98', '2017-12-02 22:01:26', '2017-12-23 00:00:00', '2017-11-30 16:03:34');
INSERT INTO `seckill` VALUES ('1001', '500元秒杀ipad2', '200', '2017-08-22 00:00:00', '2017-08-23 00:00:00', '2017-11-30 16:03:34');
INSERT INTO `seckill` VALUES ('1002', '300元秒杀小米4', '300', '2017-08-22 00:00:00', '2017-08-23 00:00:00', '2017-11-30 16:03:34');
INSERT INTO `seckill` VALUES ('1003', '200元秒杀红米note', '400', '2017-08-22 00:00:00', '2017-08-23 00:00:00', '2017-11-30 16:03:34');

-- ----------------------------
-- Table structure for success_killed
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品id',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标示：-1：无效，0：成功，1：已付款，2：已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------
INSERT INTO `success_killed` VALUES ('1000', '12345678901', '0', '2017-12-02 21:01:26');
INSERT INTO `success_killed` VALUES ('1000', '13521024540', '0', '2017-12-02 20:22:52');

-- ----------------------------
-- Procedure structure for execute_seckill
-- ----------------------------
DROP PROCEDURE IF EXISTS `execute_seckill`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(in v_seckill_id bigint,in v_phone bigint,
   in v_kill_time timestamp,out r_result int)
BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    insert ignore into success_killed
      (seckill_id,user_phone,create_time)
      values(v_seckill_id,v_phone,v_kill_time);
    select row_count() into insert_count;
    IF(insert_count = 0) THEN
      ROLLBACK;
      set r_result = -1;
    ELSEIF(insert_count < 0) THEN
      ROLLBACK;
      set r_result = -2;
    ELSE
      update seckill
      set number = number-1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number > 0;
      select row_count() into insert_count;
      IF(insert_count = 0) THEN
        ROLLBACK;
        set r_result = 0;
      ELSEIF(insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
      ELSE
        COMMIT;
        set r_result = 1;
      END IF;
    END IF;
  END
;;
DELIMITER ;
