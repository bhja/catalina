package com.bh.catalina;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to hook up the Runtime JVM shutdown hook.
 */

public class CustomShutdown extends Thread{

  Logger logger = LoggerFactory.getLogger(CustomShutdown.class);
  @Override
  public void run(){
    try {
      logger.warn("Shutting down the system");

    }catch (Exception e){
      logger.error("Issue shutting down the system" + e.getMessage());
    }
  }




}
