
-- -----------------------------------------------------
-- Table `bleAgent_DEVICE`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS bleAgent_DEVICE (
  bleAgent_DEVICE_ID VARCHAR(45) NOT NULL ,
  DEVICE_NAME VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (bleAgent_DEVICE_ID) );

