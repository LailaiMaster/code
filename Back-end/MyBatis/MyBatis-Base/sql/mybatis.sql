-- ----------------------------
-- Table structure for `base_user`
-- ----------------------------
DROP TABLE IF EXISTS `base_user`;
CREATE TABLE `user`
(
    `id`       INT(11)     NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(32) NOT NULL COMMENT '用户名称',
    `birthday` DATE         DEFAULT NULL COMMENT '生日',
    `sex`      CHAR(1)      DEFAULT NULL COMMENT '性别',
    `address`  VARCHAR(256) DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 27
    DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user`
VALUES ('1', '王五', NULL, '2', NULL);
INSERT INTO `user`
VALUES ('10', '张三', '2014-07-10', '1', '北京市');
INSERT INTO `user`
VALUES ('16', '张小明', NULL, '1', '河南郑州');
INSERT INTO `user`
VALUES ('22', '陈小明', NULL, '1', '河南郑州');
INSERT INTO `user`
VALUES ('24', '张三丰', NULL, '1', '河南郑州');
INSERT INTO `user`
VALUES ('25', '陈小明', NULL, '1', '河南郑州');
INSERT INTO `user`
VALUES ('26', '王五', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`
(
    `id`         INT(11)     NOT NULL AUTO_INCREMENT,
    `user_id`    INT(11)     NOT NULL COMMENT '下单用户id',
    `number`     VARCHAR(32) NOT NULL COMMENT '订单号',
    `createtime` DATETIME    NOT NULL COMMENT '创建订单时间',
    `note`       VARCHAR(100) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `FK_orders_1` (`user_id`),
    CONSTRAINT `FK_orders_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 6
    DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order`
VALUES ('3', '1', '1000010', '2015-02-04 13:22:35', NULL);
INSERT INTO `order`
VALUES ('4', '1', '1000011', '2015-02-03 13:22:41', NULL);
INSERT INTO `order`
VALUES ('5', '10', '1000012', '2015-02-12 16:13:23', NULL);

-- ----------------------------
-- Table structure for `items`
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items`
(
    id         INT(11) PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(32)  NOT NULL,
    price      FLOAT(10, 1) NOT NULL,
    detail     TEXT,
    pic        VARCHAR(64),
    createtime DATETIME
);

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items`
VALUES (NULL, '小米6', 3021.1, '拍人更美！', NULL, NULL);
INSERT INTO `items`
VALUES (NULL, 'HuaWei', 15310.2, '质量好！1', NULL, NULL);
INSERT INTO `items`
VALUES (NULL, '三星X9', 10445.3, '质量好！2', NULL, NULL);
INSERT INTO `items`
VALUES (NULL, '1华为 荣耀8', 2399.3, '质量好！3', NULL, NULL);

-- ----------------------------
-- Table structure for `tbl_employee` and `tbl_dept`
-- ----------------------------
create table tbl_dept
(
    id        int(11) primary key auto_increment,
    dept_name varchar(255)
);

create table tbl_employee
(
    id        int(11) primary key auto_increment,
    d_id      int(11),
    last_name varchar(255),
    gender    char(1),
    email     varchar(255),
    constraint `FK_dept_id` foreign key (`d_id`) references tbl_dept (`id`)
);
