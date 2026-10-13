CREATE DATABASE IF NOT EXISTS DBTemp;

USE DBTemp;

CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish_ci NOT NULL,
  last_name varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish_ci NOT NULL,
  email varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish_ci NOT NULL,
  password varchar(256) NOT NULL,
  active bit(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE product(
  code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  status bit(1) DEFAULT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO user VALUES
(1,'Marcos','Martinez','mmartinez@temp.com','{bcrypt}$2a$10$uvWNfki.WxQhUD6e51di0uvC9G0DiGly7/PvyLR0Ay0mpFV24YklG',1);
INSERT INTO product VALUES (1,'Mochila backpack Adidas',1),(2,'USB 2.0 Security',1);