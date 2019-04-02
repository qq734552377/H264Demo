package com.xair.h264demo.exception;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import org.apache.log4j.Logger;
import org.xutils.x;

/**
 * Created by Administrator on 2016/6/12.
 */
public class ExceptionApplication extends Application {

    public static Context context;
    public static Logger gLogger;
    public static Point SCREENPOINT = new Point(720,1080);

//    static {
//        System.loadLibrary("opencv_java3");
//        System.loadLibrary("opencv_java");
//        System.loadLibrary("native-lib");
//    }

    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        context=this;

        LogUtil.configLog();
        gLogger= Logger.getLogger(ExceptionApplication.class);
        //输出MyApplication的信息
        gLogger.info("Log4j Is Ready and Tag Application Was Started Successfully! ");

        WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        display.getSize(SCREENPOINT);
    }
    public static Context getInstance(){
        return context;
    }

}
