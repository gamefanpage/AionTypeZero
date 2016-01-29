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
  `rank` int(2) NOT NULL default '1',
  `top_ranking` int(4) NOT NULL,
  `daily_kill` int(5) NOT NULL,
  `weekly_kill` int(5) NOT NULL,
  `all_kill` int(4) NOT NULL default '0',
  `max_rank` int(2) NOT NULL default '1',
  `last_kill` int(5) NOT NULL,
  `last_ap` int(11) NOT NULL,
  `last_gp` int(11) NOT NULL,
  `last_update` decimal(20,0) NOT NULL,
  `rank_pos` int(11) NOT NULL default '0',
  `old_rank_pos` int(11) NOT NULL default '0',
  `rank_ap` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`),
  CONSTRAINT `abyss_rank_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for account_data
-- ----------------------------
DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `activated` tinyint(1) NOT NULL default '1',
  `access_level` tinyint(3) NOT NULL default '0',
  `membership` tinyint(3) NOT NULL default '0',
  `old_membership` tinyint(3) NOT NULL default '0',
  `last_server` tinyint(3) NOT NULL default '-1',
  `last_ip` varchar(20) default NULL,
  `last_mac` varchar(20) NOT NULL default 'xx-xx-xx-xx-xx-xx',
  `ip_force` varchar(20) default NULL,
  `expire` date default NULL,
  `toll` bigint(13) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for account_rewards
-- ----------------------------
DROP TABLE IF EXISTS `account_rewards`;
CREATE TABLE `account_rewards` (
  `uniqId` int(11) NOT NULL auto_increment,
  `accountId` int(11) NOT NULL,
  `added` varchar(70) NOT NULL default '',
  `points` decimal(20,0) NOT NULL default '0',
  `received` varchar(70) NOT NULL default '0',
  `rewarded` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`uniqId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for account_time
-- ----------------------------
DROP TABLE IF EXISTS `account_time`;
CREATE TABLE `account_time` (
  `account_id` int(11) NOT NULL,
  `last_active` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL default NULL,
  `session_duration` int(10) default '0',
  `accumulated_online` int(10) default '0',
  `accumulated_rest` int(10) default '0',
  `penalty_end` timestamp NULL default NULL,
  PRIMARY KEY  (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` int(3) NOT NULL auto_increment,
  `announce` text NOT NULL,
  `faction` enum('ALL','ASMODIANS','ELYOS') NOT NULL default 'ALL',
  `type` enum('SHOUT','ORANGE','YELLOW','WHITE','SYSTEM') NOT NULL default 'SYSTEM',
  `delay` int(4) NOT NULL default '1800',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for banned_ip
-- ----------------------------
DROP TABLE IF EXISTS `banned_ip`;
CREATE TABLE `banned_ip` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for banned_mac
-- ----------------------------
DROP TABLE IF EXISTS `banned_mac`;
CREATE TABLE `banned_mac` (
  `uniId` int(10) NOT NULL auto_increment,
  `address` varchar(20) NOT NULL,
  `time` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  `details` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`uniId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for blocks
-- ----------------------------
DROP TABLE IF EXISTS `blocks`;
CREATE TABLE `blocks` (
  `player` int(11) NOT NULL,
  `blocked_player` int(11) NOT NULL,
  `reason` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`player`,`blocked_player`),
  KEY `blocked_player` (`blocked_player`),
  CONSTRAINT `blocks_ibfk_1` FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `blocks_ibfk_2` FOREIGN KEY (`blocked_player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bookmark
-- ----------------------------
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE `bookmark` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(50) default NULL,
  `char_id` int(11) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `world_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for broker
-- ----------------------------
DROP TABLE IF EXISTS `broker`;
CREATE TABLE `broker` (
  `id` int(11) NOT NULL auto_increment,
  `item_pointer` int(11) NOT NULL default '0',
  `item_id` int(11) NOT NULL,
  `item_count` bigint(20) NOT NULL,
  `item_creator` varchar(50) default NULL,
  `seller` varchar(50) NOT NULL,
  `price` bigint(20) NOT NULL default '0',
  `broker_race` enum('ELYOS','ASMODIAN') NOT NULL,
  `expire_time` timestamp NOT NULL default '2010-01-01 02:00:00',
  `settle_time` timestamp NOT NULL default '2010-01-01 02:00:00',
  `seller_id` int(11) NOT NULL,
  `is_sold` tinyint(1) NOT NULL,
  `is_settled` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `seller_id` (`seller_id`),
  CONSTRAINT `broker_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for challenge_tasks
-- ----------------------------
DROP TABLE IF EXISTS `challenge_tasks`;
CREATE TABLE `challenge_tasks` (
  `task_id` int(11) NOT NULL,
  `quest_id` int(10) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `owner_type` enum('LEGION','TOWN') NOT NULL,
  `complete_count` int(3) unsigned NOT NULL default '0',
  `complete_time` timestamp NULL default NULL,
  PRIMARY KEY  (`task_id`,`quest_id`,`owner_id`,`owner_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for craft_cooldowns
-- ----------------------------
DROP TABLE IF EXISTS `craft_cooldowns`;
CREATE TABLE `craft_cooldowns` (
  `player_id` int(11) NOT NULL,
  `delay_id` int(11) unsigned NOT NULL,
  `reuse_time` bigint(13) unsigned NOT NULL,
  PRIMARY KEY  (`player_id`,`delay_id`),
  CONSTRAINT `craft_cooldowns_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for friends
-- ----------------------------
DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends` (
  `player` int(11) NOT NULL,
  `friend` int(11) NOT NULL,
  PRIMARY KEY  (`player`,`friend`),
  KEY `friend` (`friend`),
  CONSTRAINT `friends_ibfk_1` FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `friends_ibfk_2` FOREIGN KEY (`friend`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gameservers
-- ----------------------------
DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for guides
-- ----------------------------
DROP TABLE IF EXISTS `guides`;
CREATE TABLE `guides` (
  `guide_id` int(11) NOT NULL auto_increment,
  `player_id` int(11) NOT NULL,
  `title` varchar(80) NOT NULL,
  PRIMARY KEY  (`guide_id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `guides_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for house_bids
-- ----------------------------
DROP TABLE IF EXISTS `house_bids`;
CREATE TABLE `house_bids` (
  `player_id` int(10) NOT NULL,
  `house_id` int(10) NOT NULL,
  `bid` bigint(20) NOT NULL,
  `bid_time` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`player_id`,`house_id`,`bid`),
  KEY `house_id_ibfk_1` (`house_id`),
  CONSTRAINT `house_id_ibfk_1` FOREIGN KEY (`house_id`) REFERENCES `houses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for house_object_cooldowns
-- ----------------------------
DROP TABLE IF EXISTS `house_object_cooldowns`;
CREATE TABLE `house_object_cooldowns` (
  `player_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `reuse_time` bigint(20) NOT NULL,
  PRIMARY KEY  (`player_id`,`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for house_scripts
-- ----------------------------
DROP TABLE IF EXISTS `house_scripts`;
CREATE TABLE `house_scripts` (
  `house_id` int(11) NOT NULL,
  `index` tinyint(4) NOT NULL,
  `script` mediumtext,
  PRIMARY KEY  (`house_id`,`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for houses
-- ----------------------------
DROP TABLE IF EXISTS `houses`;
CREATE TABLE `houses` (
  `id` int(10) NOT NULL,
  `player_id` int(10) NOT NULL default '0',
  `building_id` int(10) NOT NULL,
  `address` int(10) NOT NULL,
  `acquire_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `settings` int(11) NOT NULL default '0',
  `status` enum('ACTIVE','SELL_WAIT','INACTIVE','NOSALE') NOT NULL default 'ACTIVE',
  `fee_paid` tinyint(1) NOT NULL default '1',
  `next_pay` timestamp NULL default NULL,
  `sell_started` timestamp NULL default NULL,
  `sign_notice` binary(130) default NULL,
  PRIMARY KEY  (`id`),
  KEY `address` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ingameshop
-- ----------------------------
DROP TABLE IF EXISTS `ingameshop`;
CREATE TABLE `ingameshop` (
  `object_id` int(11) NOT NULL auto_increment,
  `item_id` int(11) NOT NULL,
  `item_count` bigint(13) NOT NULL default '0',
  `item_price` bigint(13) NOT NULL default '0',
  `category` tinyint(1) NOT NULL default '0',
  `sub_category` tinyint(1) NOT NULL default '0',
  `list` int(11) NOT NULL default '0',
  `sales_ranking` int(11) NOT NULL default '0',
  `item_type` tinyint(1) NOT NULL default '0',
  `gift` tinyint(1) NOT NULL default '0',
  `title_description` varchar(512) NOT NULL,
  `description` varchar(512) default NULL,
  PRIMARY KEY  (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ingameshop_log
-- ----------------------------
DROP TABLE IF EXISTS `ingameshop_log`;
CREATE TABLE `ingameshop_log` (
  `transaction_id` int(11) NOT NULL auto_increment,
  `transaction_type` enum('BUY','GIFT') NOT NULL,
  `transaction_date` timestamp NULL default NULL,
  `payer_name` varchar(50) NOT NULL,
  `payer_account_name` varchar(50) NOT NULL,
  `receiver_name` varchar(50) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` bigint(13) NOT NULL default '0',
  `item_price` bigint(13) NOT NULL default '0',
  PRIMARY KEY  (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `item_unique_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` bigint(20) NOT NULL default '0',
  `item_color` int(11) NOT NULL default '0',
  `color_expires` int(11) NOT NULL default '0',
  `item_creator` varchar(50) default NULL,
  `expire_time` int(11) NOT NULL default '0',
  `activation_count` int(11) NOT NULL default '0',
  `item_owner` int(11) NOT NULL,
  `is_equiped` tinyint(1) NOT NULL default '0',
  `is_soul_bound` tinyint(1) NOT NULL default '0',
  `slot` bigint(20) NOT NULL default '0',
  `item_location` tinyint(1) default '0',
  `enchant` tinyint(1) default '0',
  `item_skin` int(11) NOT NULL default '0',
  `fusioned_item` int(11) NOT NULL default '0',
  `optional_socket` int(1) NOT NULL default '0',
  `optional_fusion_socket` int(1) NOT NULL default '0',
  `charge` mediumint(9) NOT NULL default '0',
  `rnd_bonus` smallint(6) default NULL,
  `authorize` int(11) NOT NULL default '0',
  PRIMARY KEY  (`item_unique_id`),
  KEY `item_location` USING HASH (`item_location`),
  KEY `index3` (`item_owner`,`item_location`,`is_equiped`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for item_cooldowns
-- ----------------------------
DROP TABLE IF EXISTS `item_cooldowns`;
CREATE TABLE `item_cooldowns` (
  `player_id` int(11) NOT NULL,
  `delay_id` int(11) NOT NULL,
  `use_delay` int(10) unsigned NOT NULL,
  `reuse_time` bigint(13) NOT NULL,
  PRIMARY KEY  (`player_id`,`delay_id`),
  CONSTRAINT `item_cooldowns_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for item_stones
-- ----------------------------
DROP TABLE IF EXISTS `item_stones`;
CREATE TABLE `item_stones` (
  `item_unique_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `slot` int(2) NOT NULL,
  `category` int(2) NOT NULL default '0',
  `polishNumber` int(11) NOT NULL default '0',
  `polishCharge` int(11) NOT NULL default '0',
  PRIMARY KEY  (`item_unique_id`,`slot`,`category`),
  CONSTRAINT `item_stones_ibfk_1` FOREIGN KEY (`item_unique_id`) REFERENCES `inventory` (`item_unique_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for legion_announcement_list
-- ----------------------------
DROP TABLE IF EXISTS `legion_announcement_list`;
CREATE TABLE `legion_announcement_list` (
  `legion_id` int(11) NOT NULL,
  `announcement` varchar(256) NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  KEY `legion_id` (`legion_id`),
  CONSTRAINT `legion_announcement_list_ibfk_1` FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for legion_emblems
-- ----------------------------
DROP TABLE IF EXISTS `legion_emblems`;
CREATE TABLE `legion_emblems` (
  `legion_id` int(11) NOT NULL,
  `emblem_id` int(1) NOT NULL default '0',
  `color_r` int(3) NOT NULL default '0',
  `color_g` int(3) NOT NULL default '0',
  `color_b` int(3) NOT NULL default '0',
  `emblem_type` enum('DEFAULT','CUSTOM') NOT NULL default 'DEFAULT',
  `emblem_data` longblob,
  PRIMARY KEY  (`legion_id`),
  CONSTRAINT `legion_emblems_ibfk_1` FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for legion_history
-- ----------------------------
DROP TABLE IF EXISTS `legion_history`;
CREATE TABLE `legion_history` (
  `id` int(11) NOT NULL auto_increment,
  `legion_id` int(11) NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `history_type` enum('CREATE','JOIN','KICK','APPOINTED','EMBLEM_REGISTER','EMBLEM_MODIFIED','ITEM_DEPOSIT','ITEM_WITHDRAW','KINAH_DEPOSIT','KINAH_WITHDRAW','LEVEL_UP') NOT NULL,
  `name` varchar(50) NOT NULL,
  `tab_id` smallint(3) NOT NULL default '0',
  `description` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `legion_id` (`legion_id`),
  CONSTRAINT `legion_history_ibfk_1` FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for legion_members
-- ----------------------------
DROP TABLE IF EXISTS `legion_members`;
CREATE TABLE `legion_members` (
  `legion_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `nickname` varchar(10) NOT NULL default '',
  `rank` enum('BRIGADE_GENERAL','CENTURION','LEGIONARY','DEPUTY','VOLUNTEER') NOT NULL default 'VOLUNTEER',
  `selfintro` varchar(32) default '',
  `challenge_score` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`),
  KEY `player_id` (`player_id`),
  KEY `legion_id` (`legion_id`),
  CONSTRAINT `legion_members_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `legion_members_ibfk_2` FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for legions
-- ----------------------------
DROP TABLE IF EXISTS `legions`;
CREATE TABLE `legions` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `level` int(1) NOT NULL default '1',
  `contribution_points` bigint(20) NOT NULL default '0',
  `deputy_permission` int(11) NOT NULL default '7692',
  `centurion_permission` int(11) NOT NULL default '7176',
  `legionary_permission` int(11) NOT NULL default '6144',
  `volunteer_permission` int(11) NOT NULL default '2048',
  `disband_time` int(11) NOT NULL default '0',
  `rank_cp` int(11) NOT NULL default '0',
  `rank_pos` int(11) NOT NULL default '0',
  `old_rank_pos` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mail
-- ----------------------------
DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail` (
  `mail_unique_id` int(11) NOT NULL,
  `mail_recipient_id` int(11) NOT NULL,
  `sender_name` varchar(20) NOT NULL,
  `mail_title` varchar(20) NOT NULL,
  `mail_message` varchar(1000) NOT NULL,
  `unread` tinyint(4) NOT NULL default '1',
  `attached_item_id` int(11) NOT NULL,
  `attached_kinah_count` bigint(20) NOT NULL,
  `express` tinyint(4) NOT NULL default '0',
  `recieved_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`mail_unique_id`),
  KEY `mail_recipient_id` (`mail_recipient_id`),
  CONSTRAINT `FK_mail` FOREIGN KEY (`mail_recipient_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for old_names
-- ----------------------------
DROP TABLE IF EXISTS `old_names`;
CREATE TABLE `old_names` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `player_id` int(11) NOT NULL,
  `old_name` varchar(50) NOT NULL,
  `new_name` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `old_names_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for petitions
-- ----------------------------
DROP TABLE IF EXISTS `petitions`;
CREATE TABLE `petitions` (
  `id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `add_data` varchar(255) default NULL,
  `time` bigint(11) NOT NULL default '0',
  `status` enum('PENDING','IN_PROGRESS','REPLIED') NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_appearance
-- ----------------------------
DROP TABLE IF EXISTS `player_appearance`;
CREATE TABLE `player_appearance` (
  `player_id` int(11) NOT NULL,
  `face` int(11) NOT NULL,
  `hair` int(11) NOT NULL,
  `deco` int(11) NOT NULL,
  `tattoo` int(11) NOT NULL,
  `face_contour` int(11) NOT NULL,
  `expression` int(11) NOT NULL,
  `jaw_line` int(11) NOT NULL,
  `skin_rgb` int(11) NOT NULL,
  `hair_rgb` int(11) NOT NULL,
  `lip_rgb` int(11) NOT NULL,
  `eye_rgb` int(11) NOT NULL,
  `face_shape` int(11) NOT NULL,
  `forehead` int(11) NOT NULL,
  `eye_height` int(11) NOT NULL,
  `eye_space` int(11) NOT NULL,
  `eye_width` int(11) NOT NULL,
  `eye_size` int(11) NOT NULL,
  `eye_shape` int(11) NOT NULL,
  `eye_angle` int(11) NOT NULL,
  `brow_height` int(11) NOT NULL,
  `brow_angle` int(11) NOT NULL,
  `brow_shape` int(11) NOT NULL,
  `nose` int(11) NOT NULL,
  `nose_bridge` int(11) NOT NULL,
  `nose_width` int(11) NOT NULL,
  `nose_tip` int(11) NOT NULL,
  `cheek` int(11) NOT NULL,
  `lip_height` int(11) NOT NULL,
  `mouth_size` int(11) NOT NULL,
  `lip_size` int(11) NOT NULL,
  `smile` int(11) NOT NULL,
  `lip_shape` int(11) NOT NULL,
  `jaw_height` int(11) NOT NULL,
  `chin_jut` int(11) NOT NULL,
  `ear_shape` int(11) NOT NULL,
  `head_size` int(11) NOT NULL,
  `neck` int(11) NOT NULL,
  `neck_length` int(11) NOT NULL,
  `shoulders` int(11) NOT NULL,
  `shoulder_size` int(11) NOT NULL,
  `torso` int(11) NOT NULL,
  `chest` int(11) NOT NULL,
  `waist` int(11) NOT NULL,
  `hips` int(11) NOT NULL,
  `arm_thickness` int(11) NOT NULL,
  `arm_length` int(11) NOT NULL,
  `hand_size` int(11) NOT NULL,
  `leg_thickness` int(11) NOT NULL,
  `leg_length` int(11) NOT NULL,
  `foot_size` int(11) NOT NULL,
  `facial_rate` int(11) NOT NULL,
  `voice` int(11) NOT NULL,
  `height` float NOT NULL,
  PRIMARY KEY  (`player_id`),
  CONSTRAINT `player_id_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_bind_point
-- ----------------------------
DROP TABLE IF EXISTS `player_bind_point`;
CREATE TABLE `player_bind_point` (
  `player_id` int(11) NOT NULL,
  `map_id` int(11) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `heading` int(3) NOT NULL,
  PRIMARY KEY  (`player_id`),
  CONSTRAINT `player_bind_point_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_cooldowns
-- ----------------------------
DROP TABLE IF EXISTS `player_cooldowns`;
CREATE TABLE `player_cooldowns` (
  `player_id` int(11) NOT NULL,
  `cooldown_id` int(6) NOT NULL,
  `reuse_delay` bigint(13) NOT NULL,
  PRIMARY KEY  (`player_id`,`cooldown_id`),
  CONSTRAINT `player_cooldowns_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_effects
-- ----------------------------
DROP TABLE IF EXISTS `player_effects`;
CREATE TABLE `player_effects` (
  `player_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_lvl` tinyint(4) NOT NULL,
  `current_time` int(11) NOT NULL,
  `end_time` bigint(13) NOT NULL,
  PRIMARY KEY  (`player_id`,`skill_id`),
  CONSTRAINT `player_effects_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_emotions
-- ----------------------------
DROP TABLE IF EXISTS `player_emotions`;
CREATE TABLE `player_emotions` (
  `player_id` int(11) NOT NULL,
  `emotion` int(11) NOT NULL,
  `remaining` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`emotion`),
  CONSTRAINT `player_emotions_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_life_stats
-- ----------------------------
DROP TABLE IF EXISTS `player_life_stats`;
CREATE TABLE `player_life_stats` (
  `player_id` int(11) NOT NULL,
  `hp` int(11) NOT NULL default '1',
  `mp` int(11) NOT NULL default '1',
  `fp` int(11) NOT NULL default '1',
  PRIMARY KEY  (`player_id`),
  CONSTRAINT `FK_player_life_stats` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_macrosses
-- ----------------------------
DROP TABLE IF EXISTS `player_macrosses`;
CREATE TABLE `player_macrosses` (
  `player_id` int(11) NOT NULL,
  `order` int(3) NOT NULL,
  `macro` text NOT NULL,
  UNIQUE KEY `main` (`player_id`,`order`),
  CONSTRAINT `player_macrosses_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_motions
-- ----------------------------
DROP TABLE IF EXISTS `player_motions`;
CREATE TABLE `player_motions` (
  `player_id` int(11) NOT NULL,
  `motion_id` int(3) NOT NULL,
  `time` int(11) NOT NULL default '0',
  `active` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`player_id`,`motion_id`),
  CONSTRAINT `motions_player_id_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_npc_factions
-- ----------------------------
DROP TABLE IF EXISTS `player_npc_factions`;
CREATE TABLE `player_npc_factions` (
  `player_id` int(11) NOT NULL,
  `faction_id` int(2) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `time` int(11) NOT NULL,
  `state` enum('NOTING','START','COMPLETE') NOT NULL default 'NOTING',
  `quest_id` int(6) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`faction_id`),
  CONSTRAINT `player_npc_factions_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_passkey
-- ----------------------------
DROP TABLE IF EXISTS `player_passkey`;
CREATE TABLE `player_passkey` (
  `account_id` int(11) NOT NULL,
  `passkey` varchar(32) NOT NULL default '',
  PRIMARY KEY  (`account_id`,`passkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_pets
-- ----------------------------
DROP TABLE IF EXISTS `player_pets`;
CREATE TABLE `player_pets` (
  `player_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `decoration` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `hungry_level` tinyint(4) NOT NULL default '0',
  `feed_progress` int(11) NOT NULL default '0',
  `reuse_time` bigint(20) NOT NULL default '0',
  `birthday` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `mood_started` bigint(20) NOT NULL default '0',
  `counter` int(11) NOT NULL default '0',
  `mood_cd_started` bigint(20) NOT NULL default '0',
  `gift_cd_started` bigint(20) NOT NULL default '0',
  `dopings` varchar(80) character set ascii default NULL,
  `despawn_time` timestamp NULL default NULL,
  `expire_time` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`pet_id`),
  CONSTRAINT `FK_player_pets` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_punishments
-- ----------------------------
DROP TABLE IF EXISTS `player_punishments`;
CREATE TABLE `player_punishments` (
  `player_id` int(11) NOT NULL,
  `punishment_type` enum('PRISON','GATHER','CHARBAN') NOT NULL,
  `start_time` int(10) unsigned default '0',
  `duration` int(10) unsigned default '0',
  `reason` text,
  PRIMARY KEY  (`player_id`,`punishment_type`),
  CONSTRAINT `player_punishments_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_quests
-- ----------------------------
DROP TABLE IF EXISTS `player_quests`;
CREATE TABLE `player_quests` (
  `player_id` int(11) NOT NULL,
  `quest_id` int(10) unsigned NOT NULL default '0',
  `status` varchar(10) NOT NULL default 'NONE',
  `quest_vars` int(10) unsigned NOT NULL default '0',
  `complete_count` int(3) unsigned NOT NULL default '0',
  `next_repeat_time` timestamp NULL default NULL,
  `reward` smallint(3) default NULL,
  `complete_time` timestamp NULL default NULL,
  PRIMARY KEY  (`player_id`,`quest_id`),
  CONSTRAINT `player_quests_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_recipes
-- ----------------------------
DROP TABLE IF EXISTS `player_recipes`;
CREATE TABLE `player_recipes` (
  `player_id` int(11) NOT NULL,
  `recipe_id` int(11) NOT NULL,
  PRIMARY KEY  (`player_id`,`recipe_id`),
  CONSTRAINT `player_recipes_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_registered_items
-- ----------------------------
DROP TABLE IF EXISTS `player_registered_items`;
CREATE TABLE `player_registered_items` (
  `player_id` int(10) NOT NULL,
  `item_unique_id` int(10) NOT NULL,
  `item_id` int(10) NOT NULL,
  `expire_time` int(20) default NULL,
  `color` int(11) default NULL,
  `color_expires` int(11) NOT NULL default '0',
  `owner_use_count` int(10) NOT NULL default '0',
  `visitor_use_count` int(10) NOT NULL default '0',
  `x` float NOT NULL default '0',
  `y` float NOT NULL default '0',
  `z` float NOT NULL default '0',
  `h` smallint(3) default NULL,
  `area` enum('NONE','INTERIOR','EXTERIOR','ALL','DECOR') NOT NULL default 'NONE',
  `floor` tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`item_unique_id`,`item_id`),
  CONSTRAINT `player_regitems_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_settings
-- ----------------------------
DROP TABLE IF EXISTS `player_settings`;
CREATE TABLE `player_settings` (
  `player_id` int(11) NOT NULL,
  `settings_type` tinyint(1) NOT NULL,
  `settings` blob NOT NULL,
  PRIMARY KEY  (`player_id`,`settings_type`),
  CONSTRAINT `ps_pl_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_skills
-- ----------------------------
DROP TABLE IF EXISTS `player_skills`;
CREATE TABLE `player_skills` (
  `player_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_level` int(3) NOT NULL default '1',
  PRIMARY KEY  (`player_id`,`skill_id`),
  CONSTRAINT `player_skills_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_titles
-- ----------------------------
DROP TABLE IF EXISTS `player_titles`;
CREATE TABLE `player_titles` (
  `player_id` int(11) NOT NULL,
  `title_id` int(11) NOT NULL,
  `remaining` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`title_id`),
  CONSTRAINT `player_titles_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_transfers
-- ----------------------------
DROP TABLE IF EXISTS `player_transfers`;
CREATE TABLE `player_transfers` (
  `id` int(11) NOT NULL auto_increment,
  `source_server` tinyint(3) NOT NULL,
  `target_server` tinyint(3) NOT NULL,
  `source_account_id` int(11) NOT NULL,
  `target_account_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL default '0',
  `time_added` varchar(100) default NULL,
  `time_performed` varchar(100) default NULL,
  `time_done` varchar(100) default NULL,
  `comment` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_vars
-- ----------------------------
DROP TABLE IF EXISTS `player_vars`;
CREATE TABLE `player_vars` (
  `player_id` int(11) NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` varchar(255) default NULL,
  `time` varchar(200) default NULL,
  PRIMARY KEY  (`player_id`,`param`),
  CONSTRAINT `player_vars_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for players
-- ----------------------------
DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `account_id` int(11) NOT NULL,
  `account_name` varchar(50) NOT NULL,
  `exp` bigint(20) NOT NULL default '0',
  `recoverexp` bigint(20) NOT NULL default '0',
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `heading` int(11) NOT NULL,
  `world_id` int(11) NOT NULL,
  `world_owner` int(11) NOT NULL default '0',
  `gender` enum('MALE','FEMALE') NOT NULL,
  `race` enum('ASMODIANS','ELYOS') NOT NULL,
  `player_class` enum('WARRIOR','GLADIATOR','TEMPLAR','SCOUT','ASSASSIN','RANGER','MAGE','SORCERER','SPIRIT_MASTER','PRIEST','CLERIC','CHANTER','ENGINEER','GUNNER','ARTIST','BARD','RIDER','ALL') NOT NULL,
  `creation_date` timestamp NULL default NULL,
  `deletion_date` timestamp NULL default NULL,
  `last_online` timestamp NULL default NULL on update CURRENT_TIMESTAMP,
  `quest_expands` tinyint(1) NOT NULL default '0',
  `npc_expands` tinyint(1) NOT NULL default '0',
  `advenced_stigma_slot_size` tinyint(1) NOT NULL default '0',
  `warehouse_size` tinyint(1) NOT NULL default '0',
  `mailbox_letters` tinyint(4) unsigned NOT NULL default '0',
  `title_id` int(3) NOT NULL default '-1',
  `bonus_title_id` int(3) NOT NULL default '-1',
  `dp` int(3) NOT NULL default '0',
  `soul_sickness` tinyint(1) unsigned NOT NULL default '0',
  `reposte_energy` bigint(20) NOT NULL default '0',
  `online` tinyint(1) NOT NULL default '0',
  `note` text,
  `mentor_flag_time` int(11) NOT NULL default '0',
  `last_transfer_time` decimal(20,0) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name_unique` (`name`),
  KEY `account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for portal_cooldowns
-- ----------------------------
DROP TABLE IF EXISTS `portal_cooldowns`;
CREATE TABLE `portal_cooldowns` (
  `player_id` int(11) NOT NULL,
  `world_id` int(11) NOT NULL,
  `reuse_time` bigint(13) NOT NULL,
  `count` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`world_id`),
  CONSTRAINT `portal_cooldowns_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for server_variables
-- ----------------------------
DROP TABLE IF EXISTS `server_variables`;
CREATE TABLE `server_variables` (
  `key` varchar(30) NOT NULL,
  `value` varchar(30) NOT NULL,
  PRIMARY KEY  (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for siege_locations
-- ----------------------------
DROP TABLE IF EXISTS `siege_locations`;
CREATE TABLE `siege_locations` (
  `id` int(11) NOT NULL,
  `race` enum('ELYOS','ASMODIANS','BALAUR') NOT NULL,
  `legion_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for skill_motions
-- ----------------------------
DROP TABLE IF EXISTS `skill_motions`;
CREATE TABLE `skill_motions` (
  `motion_name` varchar(255) NOT NULL default '',
  `skill_id` int(11) NOT NULL,
  `attack_speed` int(11) NOT NULL,
  `weapon_type` varchar(255) NOT NULL,
  `off_weapon_type` varchar(255) NOT NULL,
  `race` varchar(255) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `time` int(11) NOT NULL default '0',
  PRIMARY KEY  (`motion_name`,`skill_id`,`attack_speed`,`weapon_type`,`off_weapon_type`,`gender`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for surveys
-- ----------------------------
DROP TABLE IF EXISTS `surveys`;
CREATE TABLE `surveys` (
  `unique_id` int(11) NOT NULL auto_increment,
  `owner_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` decimal(20,0) NOT NULL default '1',
  `html_text` text NOT NULL,
  `html_radio` varchar(100) NOT NULL default 'accept',
  `used` tinyint(1) NOT NULL default '0',
  `used_time` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`unique_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `surveys_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
-- Table structure for towns
-- ----------------------------
DROP TABLE IF EXISTS `towns`;
CREATE TABLE `towns` (
  `id` int(11) NOT NULL,
  `level` int(11) NOT NULL default '1',
  `points` int(10) NOT NULL default '0',
  `race` enum('ELYOS','ASMODIANS') NOT NULL,
  `level_up_date` timestamp NOT NULL default '1970-01-01 07:00:01',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for web_reward
-- ----------------------------
DROP TABLE IF EXISTS `web_reward`;
CREATE TABLE `web_reward` (
  `unique` int(11) NOT NULL auto_increment,
  `item_owner` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` decimal(20,0) NOT NULL default '1',
  `rewarded` tinyint(1) NOT NULL default '0',
  `added` varchar(70) default '',
  `received` varchar(70) default '',
  PRIMARY KEY  (`unique`),
  KEY `item_owner` (`item_owner`),
  CONSTRAINT `web_reward_ibfk_1` FOREIGN KEY (`item_owner`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weddings
-- ----------------------------
DROP TABLE IF EXISTS `weddings`;
CREATE TABLE `weddings` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `player1` int(11) NOT NULL,
  `player2` int(11) NOT NULL,
  PRIMARY KEY  USING BTREE (`id`),
  KEY `player1` (`player1`),
  KEY `player2` (`player2`),
  CONSTRAINT `weddings_ibfk_1` FOREIGN KEY (`player1`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `weddings_ibfk_2` FOREIGN KEY (`player2`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;