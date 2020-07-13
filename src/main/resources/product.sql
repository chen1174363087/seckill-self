 DROP TABLE if EXISTS `cx_product`;
 CREATE TABLE `cx_product` (
    `id` bigint(20) NOT NULL auto_increment,
    `stock` int(11) NOT NULL DEFAULT 0,
    `describle` varchar(50) DEFAULT NULL,
		`create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '产品表';