
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for abyss_rank
-- ----------------------------
DROP TABLE IF EXISTS `abyss_rank`;
CREATE TABLE `abyss_rank` (
  `player_id` int(11) NOT NULL,
  `daily_ap` int(11) NOT NULL,
  `daily_gp` int(11) NOT NULL,
  `weekly_ap` int(11) NOT NULL,
  `weekly_gp` int(11) NOT NULL,
  `ap` int(11) NOT NULL,
  `gp` int(11) NOT NULL,
  `rank` int(2) NOT NULL DEFAULT '1',
  `top_ranking` int(4) NOT NULL,
  `daily_kill` int(5) NOT NULL,
  `weekly_kill` int(5) NOT NULL,
  `all_kill` int(4) NOT NULL DEFAULT '0',
  `max_rank` int(2) NOT NULL DEFAULT '1',
  `last_kill` int(5) NOT NULL,
  `last_ap` int(11) NOT NULL,
  `last_gp` int(11) NOT NULL,
  `last_update` decimal(20,0) NOT NULL,
  `rank_pos` int(11) NOT NULL DEFAULT '0',
  `old_rank_pos` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_id`),
  CONSTRAINT `abyss_rank_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;