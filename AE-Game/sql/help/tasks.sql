/*
MySQL Data Transfer
Source Host: 88.198.54.114
Source Database: atreia_pvp_db
Target Host: 88.198.54.114
Target Database: atreia_pvp_db
Date: 19.09.2013 3:02:49
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for tasks
-- ----------------------------
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `task_type` enum('SHUTDOWN','RESTART') NOT NULL,
  `trigger_type` enum('FIXED_IN_TIME') NOT NULL,
  `exec_param` text,
  `trigger_param` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `tasks` VALUES ('1', 'RESTART', 'FIXED_IN_TIME', '02:55:00', '60 5 320');
