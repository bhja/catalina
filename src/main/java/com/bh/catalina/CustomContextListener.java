package com.bh.catalina;

import java.io.File;
import java.io.PrintWriter;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import jnr.constants.platform.Signal;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.util.DefaultPOSIXHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The context listener uses the jnr posix lib to handle the signals.
 * The kill -9 is a special case which cannot be captured in the code.
 * This is an action which is done by someone so the developer knows what is being done.
 * Suggestion - Use script to run this command to kill the process and log the
 * same in a audit log when the script executes.
 */
@WebListener
public class CustomContextListener implements ServletContextListener {


  Logger logger = LoggerFactory.getLogger(CustomContextListener.class);
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try{
      File f = new File("/tmp/logger");
      if(f.exists()){
        logger.info("Shutdown was not graceful");
        logAuditTrail(ExitStatus.FORCEFUL,-1);
        f.delete();
      }
      f.createNewFile();
    }catch (Exception e){
      logger.info("File does not exist");
    }
    logger.info("Creating the posix handler");
    posixHandler();
  }


  public void posixHandler(){
    jnr.posix.SignalHandler handler = signal -> {
      logger.info("Check if this is captured ==>" + signal);
      //Do what ever it is here . You can do most of the work here.
      //Or call the method that handles the shutdown.
      logAuditTrail(ExitStatus.POSIX_SIGNAL,signal);
      System.exit(signal);
    };
    final POSIX posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);
    posix.signal(Signal.SIGKILL, handler);
    posix.signal(Signal.SIGINT,handler);
    posix.signal(Signal.SIGTERM,handler);


  }
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
      logger.info("Context destroyed with graceful shutdown");
      logAuditTrail(ExitStatus.GRACEFUL,0);
      try{
        File f = new File("/tmp/logger");
        if(f.exists()){
          f.delete();
        }else{
          logger.error("File not created in the startup");
        }
      }catch (Exception e){
        logger.error("could not delete the file");
      }
  }

  public void logAuditTrail(ExitStatus status,int signal){
      if (status == ExitStatus.GRACEFUL) {
          logger.info("Regular shutdown in progress");
      }else{
        logger.warn("It is a signaled status "  + signal);
      }

  }

  enum ExitStatus{
    GRACEFUL,
    POSIX_SIGNAL,
    FORCEFUL

  }


}
