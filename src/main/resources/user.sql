 DROP TABLE if EXISTS `cx_user`;
 CREATE TABLE `cx_user` (
    `id` bigint(20) NOT NULL auto_increment,
    `username` varchar(50) DEFAULT NULL,
    `password` varchar(50) DEFAULT NULL,
		`create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `ind_user` (`username`) USING BTREE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户表';