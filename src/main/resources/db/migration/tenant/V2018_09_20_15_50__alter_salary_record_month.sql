ALTER TABLE `salary_record_monthly` DROP COLUMN `create_date`,CHANGE COLUMN `record_date` `record_time` datetime DEFAULT NULL COMMENT '归档时间';
ALTER TABLE `salary_setting` CHANGE COLUMN `setting_name` `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称';
