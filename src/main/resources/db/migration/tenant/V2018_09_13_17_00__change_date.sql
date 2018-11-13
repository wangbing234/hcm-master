ALTER TABLE `employee_financial`
CHANGE COLUMN `create_date` `create_time` DATETIME NULL DEFAULT NULL COMMENT '创建日期' ,
CHANGE COLUMN `update_date` `update_time` DATETIME NULL DEFAULT NULL ;


ALTER TABLE `employee_financial`
CHANGE COLUMN `tax_mode_id` `threshold_id` INT(11) NULL DEFAULT NULL COMMENT '免税额类型ID' ;