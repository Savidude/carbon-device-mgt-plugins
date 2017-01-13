
-- -----------------------------------------------------
-- Table `digitaldisplay_DEVICE`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `digitaldisplay_DEVICE` (
  `digitaldisplay_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`digitaldisplay_DEVICE_ID`) )
ENGINE = InnoDB;
