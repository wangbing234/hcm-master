CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `status` int(255) DEFAULT NULL COMMENT '状态1启用，0不启用',
  `email` varchar(255) DEFAULT NULL COMMENT '邮件',
  `group_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `verification` varchar(255) DEFAULT NULL,
  `verification_useful_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `company_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `group_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL COMMENT 'jdbcurl',
  `group_id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL COMMENT '数据库用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '数据库密码',
  `initialize` tinyint(1) DEFAULT NULL COMMENT '是否初始化',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);