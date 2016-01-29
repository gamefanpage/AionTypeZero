SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for tasks
-- ----------------------------
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id` int(5) NOT NULL auto_increment,
  `task_type` enum('SHUTDOWN','RESTART') NOT NULL,
  `trigger_type` enum('FIXED_IN_TIME') NOT NULL,
  `exec_param` text,
  `trigger_param` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `tasks` VALUES ('1', 'RESTART', 'FIXED_IN_TIME', '60 10 300', '04:55:00');
