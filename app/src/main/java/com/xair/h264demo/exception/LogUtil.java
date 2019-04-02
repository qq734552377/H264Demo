package com.xair.h264demo.exception;

import android.os.Environment;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by Administrator on 2016/8/12.
 */
public class LogUtil {


    private static final String LOG_FILE_PATTERN = "[%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n";

    public static void configLog() {
        final LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "Ucast" + File.separator + "h264player.log");
        // Set the root log level
        logConfigurator.setRootLevel(Level.INFO);
        // Set log level of a specific logger
        logConfigurator.setLevel("org.apache", Level.INFO);
        logConfigurator.setFilePattern(LOG_FILE_PATTERN);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setUseLogCatAppender(false);
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        //设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(10);
        logConfigurator.configure();

    }
}
