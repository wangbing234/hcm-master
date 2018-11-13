
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '全局附件id',
  `file_id` bigint(20) DEFAULT NULL COMMENT '通过全局唯一ID生成器生成的文件ID',
  `origin_name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '附件原始名称',
  `oss_filename` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '附件在阿里云上的存储地址',
  `file_type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件后缀：jpg，doc',
  `file_size` int(11) DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='全局附件表';


DROP TABLE IF EXISTS `customized_employee_form`;
CREATE TABLE `customized_employee_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块名称',
  `type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单类型，position岗位信息，basic基本信息，other其他',
  `required` int(11) DEFAULT NULL COMMENT '是否必填，0否，1是',
  `multi_record` int(11) DEFAULT NULL COMMENT '是否支持多个，0否，1是',
  `on_board` int(11) DEFAULT NULL COMMENT '员工入职是否启用：0否，1是',
  `switchable` int(11) DEFAULT NULL COMMENT '开启状态，0关闭，1开启',
  `enable` int(11) DEFAULT NULL COMMENT '开关是否禁用，0否，1是',
  `idx` int(11) DEFAULT NULL COMMENT '排序',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人：0系统，1用户',
  `code` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块编码，position岗位信息，job工作信息，contract合同信息，identity证件信息，identity证件信息，contact联系信息，emergencyContact紧急联系人信息，education紧急联系人信息，workExperience工作经历信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='自定义员工表单表';


BEGIN;
INSERT INTO `customized_employee_form` VALUES ('1', '岗位信息', 'position', '1', '1', '1', '1', '1', '1', null, 'position'), ('2', '工作信息', 'basic', '1', '0', '1', '1', '1', '1', null, 'job'), ('3', '合同信息', 'basic', '1', '1', '1', '1', '1', '2', null, 'contract'), ('4', '证件信息', 'basic', '1', '1', '1', '1', '1', '3', null, 'identity'), ('5', '联系信息', 'other', '1', '1', '1', '1', '1', '1', null, 'contact'), ('6', '紧急联系人', 'other', '1', '0', '1', '1', '1', '2', null, 'emergencyContact'), ('7', '教育信息', 'other', '1', '1', '1', '1', '1', '3', null, 'education'), ('8', '工作经历', 'other', '0', '1', '1', '1', '1', '4', null, 'workExperience');
COMMIT;

DROP TABLE IF EXISTS `customized_field`;
CREATE TABLE `customized_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段编码',
  `field_type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '控件类型：text_field，text_area，decimal，select，checkbox，date_range，date，file',
  `idx` int(11) DEFAULT NULL COMMENT '排序',
  `target_type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '目标业务类型：company（公司）,department（部门）,position（岗位）,grade（职责）,personnel（人事管理）',
  `target_id` int(11) DEFAULT NULL COMMENT '目标业务ID',
  `attribute` json DEFAULT NULL COMMENT '控件属性',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `enable` tinyint(4) DEFAULT NULL COMMENT '启用状态，0禁用，1启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='自定义字段表';


DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_no` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '员工编号，员工唯一标识，不能重复',
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `avatar_file_id` bigint(20) DEFAULT NULL COMMENT '头像的file id',
  `mobile` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号，将作为员工登录账号',
  `gender` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '性别，male男，female女',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `hire_date` datetime DEFAULT NULL COMMENT '入职日期',
  `work_date` datetime DEFAULT NULL COMMENT '开始工作日期',
  `type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '员工类型，trainee实习，labor劳务，laborContract劳务合同，dispatch派遣，retirement退休返聘',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '员工状态，probation试用，formal正式, former离职',
  `qualify_date` datetime DEFAULT NULL COMMENT '转正日期',
  `qualify_remark` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '转正备注',
  `qualify_letter_file_id` bigint(20) DEFAULT NULL COMMENT '转正信生成的全局唯一id',
  `qualify_letter_origin_name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '转正信的原始名称',
  `resignation_date` datetime DEFAULT NULL COMMENT '离职日期',
  `resignation_reason` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '离职原因',
  `resignation_letter_file_id` bigint(20) DEFAULT NULL COMMENT '离职信生成的全局唯一id',
  `resignation_letter_origin_name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '离职信的原始名称',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `customized_forms` json DEFAULT NULL,
  `resignation_handover_man_id` int(11) DEFAULT NULL COMMENT '离职交接人',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工表';


DROP TABLE IF EXISTS `employee_bank_info`;
CREATE TABLE `employee_bank_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '员工Id',
  `card_no` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '卡号',
  `bank_name` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '开户行名称',
  `bank_address` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '开户行地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工银行卡信息';


DROP TABLE IF EXISTS `employee_contact`;
CREATE TABLE `employee_contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` int(11) DEFAULT NULL COMMENT '员工ID',
  `type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系类型，address居住地，residenceAddress户籍地址，homePhone座机，email邮箱',
  `content` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工联系信息表';


DROP TABLE IF EXISTS `employee_contract`;
CREATE TABLE `employee_contract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL,
  `sign_unit` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '签约单位',
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `period` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '合同期限(1年：year1；2年：year2；3年：year3；5年：year5；不固定：float；自定义：custom)',
  `probation_end_date` datetime DEFAULT NULL COMMENT '试用期结束日期',
  `customized_field` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工合同表';


DROP TABLE IF EXISTS `employee_education`;
CREATE TABLE `employee_education` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '员工ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `school` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学校',
  `education` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学历',
  `fulltime` int(11) DEFAULT NULL COMMENT '是否全日制',
  `highest` int(11) DEFAULT NULL COMMENT '是否最高学历',
  `degree` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学位',
  `customized_field` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工教育信息表';


DROP TABLE IF EXISTS `employee_emergency_contact`;
CREATE TABLE `employee_emergency_contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` int(11) DEFAULT NULL,
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `relationship` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关系',
  `mobile` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工紧急联系人表';


DROP TABLE IF EXISTS `employee_financial`;
CREATE TABLE `employee_financial` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '员工Id',
  `salary` decimal(10,4) DEFAULT NULL COMMENT '薪酬',
  `tax_mode_id` int(11) DEFAULT NULL COMMENT '免税额类型',
  `social_security_plan_id` int(11) DEFAULT NULL COMMENT '社保方案',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `update_date` datetime DEFAULT NULL,
  `social_security_base` decimal(10,0) DEFAULT NULL COMMENT '社保基数',
  `housing_fund_plan_id` int(11) DEFAULT NULL COMMENT '公积金方案',
  `housing_fund_base` decimal(10,0) DEFAULT NULL,
  `housing_fund_account` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工财务表';


DROP TABLE IF EXISTS `employee_history`;
CREATE TABLE `employee_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` int(11) DEFAULT NULL COMMENT '员工ID',
  `content` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `happen_date` datetime DEFAULT NULL COMMENT '发生日期：实际离职，转岗日期',
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工历史日志表';


DROP TABLE IF EXISTS `employee_identity`;
CREATE TABLE `employee_identity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` int(11) DEFAULT NULL,
  `type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '证件类型，idCard身份证，passport护照',
  `code` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  `customized_field` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工证件表';


DROP TABLE IF EXISTS `employee_position`;
CREATE TABLE `employee_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL COMMENT '开始时间',
  `company_id` int(11) DEFAULT NULL COMMENT '公司ID',
  `department_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `position_id` int(11) DEFAULT NULL COMMENT '岗位ID',
  `grade_id` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职级id',
  `leader_id` int(11) DEFAULT NULL COMMENT '直接主管',
  `customized_field` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工岗位表';


DROP TABLE IF EXISTS `employee_role`;
CREATE TABLE `employee_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL COMMENT '员工ID',
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工角色表';


DROP TABLE IF EXISTS `employee_salary_monthly`;
CREATE TABLE `employee_salary_monthly` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '员工Id',
  `include` tinyint(4) DEFAULT '1' COMMENT '是否计薪 1计薪，0 不计薪',
  `date` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '统计月份yyyyMM',
  `items_result` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工月度薪资表';


DROP TABLE IF EXISTS `employee_work_experience`;
CREATE TABLE `employee_work_experience` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '员工ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `work_unit` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '工作单位',
  `job_position` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任职岗位',
  `leave_reason` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '离职原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工工作信息表';

DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_bin NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职级名称',
  `code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职级代码',
  `alias` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职级简称',
  `rank` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职级级别',
  `enable` tinyint(4) DEFAULT '0' COMMENT '是否有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `enable_time` datetime DEFAULT NULL COMMENT '生效日期',
  `custom_field` json DEFAULT NULL,
  `deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


DROP TABLE IF EXISTS `housing_fund_plan`;
CREATE TABLE `housing_fund_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '方案名称',
  `limit_point` int(11) DEFAULT NULL COMMENT '入离职临界点',
  `effect_date` datetime DEFAULT NULL COMMENT '生效日期',
  `point_scale` int(11) DEFAULT NULL COMMENT '小数点保留位数',
  `point_rule` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '小数点进位规则',
  `limit_up` decimal(10,0) DEFAULT NULL COMMENT '社保缴纳上限',
  `limit_down` decimal(10,0) DEFAULT NULL COMMENT '社保缴纳下限',
  `fund_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '公积金（公司）比例\n',
  `fund_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '公积金（个人）比例',
  `fund_adding_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '补充公积金（公司）比例',
  `fund_adding_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '补充公积金（个人）比例',
  `enable` tinyint(4) DEFAULT NULL COMMENT '启用状态，0禁用，1启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='公积金方案';


DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单名称',
  `code` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单编码',
  `parent_id` int(11) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `idx` int(11) DEFAULT NULL COMMENT '排序',
  `type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单类型，catalogue(目录)，menu(菜单),button(按钮)',
  `platform` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '平台类型,backend(管理后台权限),frontend(自助服务权限)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜单表';

BEGIN;
INSERT INTO `menu` VALUES ('1', '组织管理', 'organizationManage', '0', '1', 'catalogue', 'backend'), ('2', '组织架构', 'organization', '0', '1', 'menu', 'backend'), ('3', '公司信息', 'company', '1', '1', 'menu', 'backend'), ('4', '部门信息', 'department', '1', '2', 'menu', 'backend'), ('5', '岗位信息', 'position', '1', '3', 'menu', 'backend'), ('6', '职级信息', 'grade', '1', '4', 'menu', 'backend'), ('7', '人事管理', 'employeeManage', '0', '2', 'catalogue', 'backend');
COMMIT;


DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '代码',
  `alias` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '简称',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级部门ID',
  `type` varchar(15) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型：department-部门 | company-公司',
  `address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公司地址',
  `register_address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公司注册地址',
  `enable` tinyint(4) DEFAULT '0' COMMENT '是否有效',
  `custom_field` json DEFAULT NULL COMMENT '自定义字段',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  `master` varchar(225) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门负责人',
  `legal_person` varchar(225) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '法人代表',
  `formation` int(11) DEFAULT NULL COMMENT '编制',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `enable_time` datetime DEFAULT NULL COMMENT '生效日期',
  `path` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所在树路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


DROP TABLE IF EXISTS `position`;
CREATE TABLE `position` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '岗位名称',
  `code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '岗位代码',
  `alias` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '岗位简称',
  `department_id` int(11) DEFAULT NULL COMMENT '所属部门ID',
  `parent_position_id` int(11) DEFAULT NULL COMMENT '上级岗位ID',
  `grade_id` int(11) DEFAULT NULL COMMENT '职级ID',
  `enable` tinyint(4) DEFAULT '0' COMMENT '是否有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `enable_time` datetime DEFAULT NULL COMMENT '生效日期',
  `custom_field` json DEFAULT NULL,
  `deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色名称',
  `super_admin` int(11) DEFAULT NULL COMMENT '是否为超级管理员,0否，1是',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色表';

-- ----------------------------
--  Records of `role`
-- ----------------------------
BEGIN;
INSERT INTO `role` VALUES ('1', '超级管理员', '1', null);
COMMIT;

-- ----------------------------
--  Table structure for `role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `menu_id` int(11) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色菜单表';

-- ----------------------------
--  Table structure for `role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `resource` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源',
  `action` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作类型,GET，DELETE，PUT，POST,EDIT',
  `conditions` json DEFAULT NULL COMMENT '权限条件',
  `effect` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问状态，allow允许,deny拒绝',
  `menu_code` varchar(45) COLLATE utf8mb4_bin DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色权限表';

-- ----------------------------
--  Table structure for `salary_history`
-- ----------------------------
DROP TABLE IF EXISTS `salary_history`;
CREATE TABLE `salary_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT '雇员id',
  `before_adjust` decimal(10,4) DEFAULT NULL COMMENT '调薪前薪资',
  `after_adjust` decimal(10,4) DEFAULT NULL COMMENT '调薪后薪资',
  `adjust_date` datetime DEFAULT NULL COMMENT '调薪时间',
  `type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  `increased` decimal(4,4) DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='员工调薪记录表';

-- ----------------------------
--  Table structure for `salary_item`
-- ----------------------------
DROP TABLE IF EXISTS `salary_item`;
CREATE TABLE `salary_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `code` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '税前加项, 税前减项,税后加项,税后减项,不直接参与薪资计算项, 人力成本项\n',
  `point_scale` int(11) DEFAULT NULL COMMENT '小数点保留位数',
  `point_rule` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '小数点进位规则',
  `formula` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公式',
  `display` tinyint(2) DEFAULT NULL COMMENT '是否显示在个人明细',
  `deletable` tinyint(2) DEFAULT '1' COMMENT '是否可以删除',
  `editable` tinyint(2) DEFAULT '1' COMMENT '是否可以编辑',
  `in_list` tinyint(2) DEFAULT '1',
  `in_option` tinyint(2) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='薪资项';

-- ----------------------------
--  Records of `salary_item`
-- ----------------------------
BEGIN;
INSERT INTO `salary_item` VALUES ('1', '出勤天数', 'dutyDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('2', '事假天数', 'affairDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('3', '加班天数', 'extraDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('4', '旷工天数', 'absentDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('5', '病假天数', 'sickDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('6', '长病假天数', 'longSickDays', 'external', '1', 'round', null, '1', '0', '0', '1', '1'), ('7', '本月工作日', 'workDays', 'external', '0', 'round', null, '0', '0', '0', '1', '1'), ('8', '本月天数', 'monthDays', 'external', '0', 'round', null, '0', '0', '0', '1', '1'), ('9', '基本工资', 'salary', 'taxBeforeAdd', '2', 'round', null, '1', '1', '1', '1', '1'), ('10', '应发工资', 'grossPay', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('11', '社保（个人）合计', 'personalSocialSecurity', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('12', '计税工资', 'taxSalary', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('13', '应税工资', 'grossSalary', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('14', '个税', 'tax', 'taxAfterMinus', '2', 'round', null, '1', '1', '1', '1', '1'), ('15', '实发工资', 'afterTaxSalary', 'external', '2', 'round', null, '1', '1', '1', '1', '1'), ('16', '养老保险（个人）', 'personalPensionEmployer', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('17', '失业保险（个人）', 'personalOutwork', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('18', '医疗保险（个人）', 'personalHistoryEmployer', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('19', '工伤保险（个人）', 'personalInjury', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('20', '生育保险（个人）', 'personalBirth', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('21', '基本公积金（个人）', 'personalHousingFund', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('22', '补充公积金（个人）', 'personalAddingHousingFund', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('23', '公积金（个人）合计', 'personalHousingFund', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('24', '养老保险（公司）', 'employerPensionEmployer', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('25', '失业保险（公司）', 'employerOutwork', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('26', '医疗保险（公司）', 'employerHistory', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('27', '工伤保险（公司）', 'employerInjury', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('28', '生育保险（公司）', 'employerBirth', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('29', '社保（公司）合计', 'employerSocialSecurity', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('30', '基本公积金（公司）', 'employerHousingFund', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('31', '补充公积金（公司）', 'employerAddingHousingFund', 'laborCost', '2', 'round', null, '0', '1', '1', '1', '1'), ('32', '公积金（公司）合计', 'employerHousingFund', 'external', '2', 'round', null, '0', '1', '1', '1', '1'), ('33', '公司成本合计', 'employerCost', 'external', '2', 'round', null, '0', '1', '1', '1', '1');
COMMIT;

-- ----------------------------
--  Table structure for `salary_record_monthly`
-- ----------------------------
DROP TABLE IF EXISTS `salary_record_monthly`;
CREATE TABLE `salary_record_monthly` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '归档名称',
  `date` datetime DEFAULT NULL COMMENT '归档日期',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `accounted` tinyint(2) DEFAULT NULL COMMENT '是否核算',
  `recorded` tinyint(2) DEFAULT NULL COMMENT '是否归档',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='归档记录表';

-- ----------------------------
--  Table structure for `salary_setting`
-- ----------------------------
DROP TABLE IF EXISTS `salary_setting`;
CREATE TABLE `salary_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `key` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '键',
  `value` int(11) DEFAULT NULL COMMENT '小数点保留位数',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '小数点进位规则',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='薪资配置表';

-- ----------------------------
--  Records of `salary_setting`
-- ----------------------------
BEGIN;
INSERT INTO `salary_setting` VALUES ('1', '计薪周期开始日期', 'start_cycle_day', null, null), ('2', '首次薪资核算开始月份', 'first_cycle_month', null, null), ('3', '发薪日期', 'salary_pay_date', null, null);
COMMIT;

-- ----------------------------
--  Table structure for `salary_threshold`
-- ----------------------------
DROP TABLE IF EXISTS `salary_threshold`;
CREATE TABLE `salary_threshold` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `point` int(11) DEFAULT NULL COMMENT '税点',
  `type` varchar(16) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '中国公民个人所得税 chinese 外籍人个人所得税   foreign',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
--  Records of `salary_threshold`
-- ----------------------------
BEGIN;
INSERT INTO `salary_threshold` VALUES ('1', '中国公民个人所得税', '5000', 'chinese'), ('2', '外籍人个人所得税', '4800', 'foreign');
COMMIT;

-- ----------------------------
--  Table structure for `social_security_plan`
-- ----------------------------
DROP TABLE IF EXISTS `social_security_plan`;
CREATE TABLE `social_security_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '方案名称',
  `limit_point` int(11) DEFAULT NULL COMMENT '入离职临界点',
  `effect_date` datetime DEFAULT NULL COMMENT '调整日期',
  `point_scale` int(11) DEFAULT NULL COMMENT '小数点保留位数',
  `point_rule` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '小数点进位规则',
  `limit_up` decimal(10,0) DEFAULT NULL COMMENT '社保缴纳上限',
  `limit_down` decimal(10,0) DEFAULT NULL COMMENT '社保缴纳下限',
  `pension_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '养老保险（个人）比例',
  `pension_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '养老保险（公司）比例',
  `history_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '医疗保险（个人）比例',
  `history_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '医疗保险（公司）比例',
  `outwork_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '失业保险（个人）比例',
  `outwork_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '失业保险（公司）比例',
  `injury_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '工伤保险（个人）比例',
  `injury_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '工伤保险（公司）比例',
  `birth_personal_ratio` decimal(10,4) DEFAULT NULL COMMENT '生育保险（个人）比例',
  `birth_employer_ratio` decimal(10,4) DEFAULT NULL COMMENT '生育保险（公司）比例',
  `enable` tinyint(4) DEFAULT NULL COMMENT '启用状态，0禁用，1启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='社保方案';

SET FOREIGN_KEY_CHECKS = 1;
