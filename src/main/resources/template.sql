CREATE TABLE if not exists `slime_war_players` (
  `player` VARCHAR(45) NOT NULL,
  `damage` DOUBLE NOT NULL,
  `kills` INT NOT NULL,
  `max_wave` INT NOT NULL,
  PRIMARY KEY (`player`))
