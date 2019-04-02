package com.xair.h264demo.tools;

import android.util.Base64;

import com.xair.h264demo.exception.CrashHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pj on 2019/3/28.
 */
public class MyTools {


    public static String LOGPATH = CrashHandler.ALBUM_PATH + "/simple_h264_Log.txt";
    public static String LOGPATHWITHTIME = CrashHandler.ALBUM_PATH + "/simple_h264_time_Log.txt";
    public static boolean ISDEBUG = true;

    public static String encode(byte[] bstr) {
        return Base64.encodeToString(bstr, Base64.DEFAULT);
    }


    /**
     * 解码
     *
     * @param str
     * @return string
     */
    public static byte[] decode(String str) {
        try {
            return Base64.decode(str, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     *  将指定byte数组以16进制的形式返回
     * */
    public static String printHexString(byte[] b) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            if(b[i] == 0x00){
                r.append("00 ");
                continue;
            }else if(b[i] == 0xFF){
                r.append("FF ");
                continue;
            }
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r.append(hex.toUpperCase() + " ");
        }
        return r.toString();
    }

    public static void writeSimpleLog(String log){
        writeToFile(LOGPATH,log);
    }
    public static void writeSimpleLogWithTime(String log){
        if (ISDEBUG)
            writeToFile(LOGPATHWITHTIME,millisToDateStringNoSpace(System.currentTimeMillis()) + "  : " +log);
    }

    public static void writeToFile(String path , String data){
        try{
            File f = new File(path);
            FileOutputStream fout = new FileOutputStream(f , true);
            BufferedOutputStream buff = new BufferedOutputStream(fout);
            buff.write((data + "\r\n").getBytes());
            buff.flush();
            buff.close();
        }catch (Exception e){
            System.out.print(e.toString());
        }
    }


    public static String millisToDateStringOnlyYMD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date;
        Date curDate = new Date(time);
        date = formatter.format(curDate);
        return date;
    }
    public static String millisToDateStringNoSpace(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date;
        Date curDate = new Date(time);
        date = formatter.format(curDate);
        return date;
    }
}
