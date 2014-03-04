-- first time run the lines below by logging into mysql as root (i.e. mysql -uroot)
-- this will create the initial user and initial database
# CREATE DATABASE chaturaji;
# CREATE USER 'dao_user'@'localhost' IDENTIFIED BY 'mypass';
# SET PASSWORD FOR 'dao_user'@'localhost' = PASSWORD('Chaturaji4');
# GRANT ALL ON chaturaji.* TO 'dao_user'@'localhost';
# FLUSH PRIVILEGES;

-- once the lines above have been run once then create the tables in the database as below

-- 1: connect to mysql as follows:
# mysql -u dao_user -h localhost -D chaturaji --password=Chaturaji4

-- 2: then run the following sequence on DDL commands to recreate all tables with the correct structure
USE chaturaji;

-- TODO - not all columns have been correctly added

DROP TABLE IF EXISTS move;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS game;

CREATE TABLE game
(
  game_id       VARCHAR(80) NOT NULL,
  startDate     DATE        NOT NULL,
  currentPlayer INTEGER     NOT NULL,
  PRIMARY KEY (game_id)
);

CREATE TABLE player
(
  player_id VARCHAR(80) NOT NULL,
  game_id   VARCHAR(80) NOT NULL,
  user_id   VARCHAR(80) NOT NULL,
  PRIMARY KEY (player_id),
  FOREIGN KEY (game_id) REFERENCES game (game_id),
  FOREIGN KEY (user_id) REFERENCES user (user_id)
);

CREATE TABLE user
(
  user_id VARCHAR(80) NOT NULL,
  PRIMARY KEY (user_id)
);

CREATE TABLE move
(
  move_id VARCHAR(80) NOT NULL,
  game_id VARCHAR(80) NOT NULL,
  PRIMARY KEY (move_id),
  FOREIGN KEY (game_id) REFERENCES game (game_id)
);

SHOW TABLES;
