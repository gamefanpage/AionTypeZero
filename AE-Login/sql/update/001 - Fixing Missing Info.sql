DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `task_type` enum('SHUTDOWN','RESTART') NOT NULL,
  `trigger_type` enum('FIXED_IN_TIME') NOT NULL,
  `exec_param` text,
  `trigger_param` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `account_time`;
CREATE TABLE `account_time` (
  `account_id` int(11) NOT NULL,
  `last_active` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL DEFAULT NULL,
  `session_duration` int(10) DEFAULT '0',
  `accumulated_online` int(10) DEFAULT '0',
  `accumulated_rest` int(10) DEFAULT '0',
  `penalty_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `account_rewards`;
CREATE TABLE `account_rewards` (
  `uniqId` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `added` varchar(70) NOT NULL DEFAULT '',
  `points` decimal(20,0) NOT NULL DEFAULT '0',
  `bonus_points` decimal(20,0) NOT NULL DEFAULT '0',
  `received` varchar(70) NOT NULL DEFAULT '0',
  `rewarded` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uniqId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- -----------------------
-- ALTER
-- -----------------------
ALTER TABLE `tasks`

change `task_type` `task_type` enum('SHUTDOWN','RESTART','CLEAN_ACCOUNTS') character set utf8 collate utf8_general_ci NOT NULL,
change `trigger_type` `trigger_type` enum('FIXED_IN_TIME','AFTER_RESTART') character set utf8 collate utf8_general_ci NOT NULL;

-- -----------------------
-- FOREIGN KEYS
-- -----------------------
DELETE FROM `account_rewards` WHERE `accountId` NOT IN (SELECT `id` FROM `account_data`);
ALTER TABLE `account_rewards` ADD CONSTRAINT `FK_account_rewards` FOREIGN KEY (`accountId`) REFERENCES `account_data` (`id`) ON DELETE CASCADE;

DELETE FROM `account_time` WHERE `account_id` NOT IN (SELECT `id` FROM `account_data`);
ALTER TABLE `account_time` ADD CONSTRAINT `FK_account_time` FOREIGN KEY (`account_id`) REFERENCES `account_data` (`id`) ON DELETE CASCADE;