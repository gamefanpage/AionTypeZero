SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for player_passports
-- ----------------------------
DROP TABLE IF EXISTS `player_passports`;
CREATE TABLE `player_passports` (
  `player_id` int(11) NOT NULL,
  `passportid` int(11) NOT NULL,
  `rewarded` int(11) NOT NULL,
  PRIMARY KEY (`player_id`,`passportid`),
  CONSTRAINT `player_passports` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
