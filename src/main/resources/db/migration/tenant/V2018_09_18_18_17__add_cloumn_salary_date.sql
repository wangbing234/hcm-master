

ALTER TABLE  `salary_setting`
CHANGE COLUMN `value` `value` VARCHAR(100) NULL DEFAULT NULL COMMENT '小数点保留位数' ;


ALTER TABLE  `employee_financial` ADD COLUMN `salary_adjust_date` DATETIME NULL AFTER `housing_fund_account`;

UPDATE `salary_item` SET `code`='employerBasicHousingFund' WHERE `id`='30';
UPDATE `salary_item` SET `code`='personalBasicHousingFund' WHERE `id`='21';

ALTER TABLE `salary_item`
CHANGE COLUMN `display` `display` TINYINT(2) NOT NULL COMMENT '是否显示在个人明细' ,
CHANGE COLUMN `deletable` `deletable` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '是否可以删除' ,
CHANGE COLUMN `editable` `editable` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '是否可以编辑' ,
CHANGE COLUMN `in_list` `in_list` TINYINT(2) NOT NULL DEFAULT '1' ,
CHANGE COLUMN `in_option` `in_option` TINYINT(2) NOT NULL DEFAULT '1' ,
ADD UNIQUE INDEX `code_UNIQUE` (`code` ASC),
ADD UNIQUE INDEX `name_UNIQUE` (`name` ASC);

ALTER TABLE `salary_history`
CHANGE COLUMN `increased` `increased` DECIMAL(11,4) NULL DEFAULT NULL ;

ALTER TABLE `employee_bank_info` DROP COLUMN `create_date`;