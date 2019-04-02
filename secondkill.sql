/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.24-log : Database - miaosha
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`miaosha` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `miaosha`;

/*Table structure for table `item` */

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `price` double(10,0) NOT NULL,
  `description` varchar(500) NOT NULL,
  `sales` int(11) NOT NULL DEFAULT '0',
  `img_url` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `item` */

insert  into `item`(`id`,`title`,`price`,`description`,`sales`,`img_url`) values 
(7,'iphoneXS',9999,'iphone最新款手机',3,'https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2064428387,788908798&fm=27&gp=0.jpg'),
(8,'小米9',2999,'小米最新款手机',0,'https://ss2.bdstatic.com/8_V1bjqh_Q23odCf/pacific/1824682986.jpg'),
(9,'小米8',2199,'过时产品',0,'https://paimgcdn.baidu.com/28F709D32AEBBF10?src=http%3A%2F%2Fms.bdimg.com%2Fdsp-image%2F2132984523.jpg&rz=st_2_968_600&v=0'),
(10,'华为mateX',19999,'华为全球首款折叠屏手机',0,'https://img.alicdn.com/bao/uploaded/i1/2838892713/O1CN01GsbOJ01Vub2IESgNs_!!2838892713.jpg');

/*Table structure for table `item_stock` */

DROP TABLE IF EXISTS `item_stock`;

CREATE TABLE `item_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `item_stock` */

insert  into `item_stock`(`id`,`stock`,`item_id`) values 
(7,994,7),
(8,100,8),
(9,999,9),
(10,10,10);

/*Table structure for table `order_info` */

DROP TABLE IF EXISTS `order_info`;

CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `item_price` double NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `order_price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `order_info` */

insert  into `order_info`(`id`,`user_id`,`item_id`,`item_price`,`amount`,`order_price`) values 
('2019040200000100',14,7,9999,1,9999),
('2019040200000200',14,7,9999,1,9999),
('2019040200000300',14,7,9999,1,9999),
('2019040200000400',14,7,9999,1,9999);

/*Table structure for table `sequence_info` */

DROP TABLE IF EXISTS `sequence_info`;

CREATE TABLE `sequence_info` (
  `name` varchar(255) NOT NULL,
  `current_value` int(11) NOT NULL DEFAULT '0',
  `step` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sequence_info` */

insert  into `sequence_info`(`name`,`current_value`,`step`) values 
('order_info',5,1);

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '为1代表男性，为2代表女性',
  `age` int(11) NOT NULL,
  `telphone` varchar(64) NOT NULL,
  `register_mode` varchar(64) NOT NULL COMMENT 'byphone,bywechat,byalipay',
  `third__party_id` varchar(64) NOT NULL DEFAULT 'no',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telphone_unique_index` (`telphone`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

/*Data for the table `user_info` */

insert  into `user_info`(`id`,`name`,`gender`,`age`,`telphone`,`register_mode`,`third__party_id`) values 
(1,'zuoxiang',1,22,'18971204096','byphone','\'\''),
(8,'张三',1,1,'123','byphone','no'),
(13,'gsgs',2,2,'456','byphone','no'),
(14,'baba',1,1,'123456','byphone','no');

/*Table structure for table `user_password` */

DROP TABLE IF EXISTS `user_password`;

CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `user_password` */

insert  into `user_password`(`id`,`encrpt_password`,`user_id`) values 
(1,'fafjoafjkoaf',1),
(2,'mqQrMYguwDmWXzxJI86QGw==',8),
(5,'BdJR6ijFvpQmYRoSHbDJKg==',13),
(6,'4QrcOUm6Wau+VuBX8g+IPg==',14);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
