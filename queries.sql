CREATE TABLE `hotels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(245) DEFAULT NULL,
  `address` text,
  `city` varchar(145) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `longitude` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(125) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `usersalt` char(64) DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_time` varchar(25) DEFAULT NULL,
  `current_login_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_user_created_on` (`created_on`),
  KEY `ix_user_id` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `reviews` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hotel_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `rating` int(11) DEFAULT NULL,
  `title` varchar(299) DEFAULT NULL,
  `review_text` text,
  `review_submission_time` varchar(45) DEFAULT NULL,
  `is_recommended` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id_hotel_id` (`hotel_id`,`user_id`)
) ENGINE=InnoDB ;

CREATE TABLE `review_likes` (
  `user_id` int(11) NOT NULL,
  `review_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `hotel_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`review_id`),
  UNIQUE KEY `uq_review_id_user_id_hotel_id` (`user_id`,`review_id`,`hotel_id`)
) ENGINE=InnoDB;

CREATE TABLE `hotel_wishlist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hotel_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_hotel_id_user_id` (`hotel_id`,`user_id`),
  KEY `fk_hotel_id_idx` (`hotel_id`)
) ENGINE=InnoDB ;

CREATE TABLE `visited_links` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `link` varchar(500) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `hotel_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;
