DROP TABLE  IF EXISTS `cx_order`;
CREATE TABLE `cx_order` (
  `id` bigint(20) NOT NULL auto_increment,
  `product_id` bigint(20) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `telphone` varchar(20) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
	`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ind_user_tel` (`username`,`telphone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '订单表';
