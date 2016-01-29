ALTER TABLE `abyss_rank` ADD COLUMN `daily_gp` int(11) NOT NULL AFTER `daily_ap`;
ALTER TABLE `abyss_rank` ADD COLUMN `weekly_gp` int(11) NOT NULL AFTER `weekly_ap`;
ALTER TABLE `abyss_rank` ADD COLUMN `gp` int(11) NOT NULL AFTER `ap`;
ALTER TABLE `abyss_rank` ADD COLUMN `last_gp` int(11) NOT NULL AFTER `last_ap`;