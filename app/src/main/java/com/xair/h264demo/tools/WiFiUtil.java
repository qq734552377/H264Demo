package com.xair.h264demo.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.xair.h264demo.exception.ExceptionApplication;

/**
 * Created by Administrator on 2016/1/19.
 */
public class WiFiUtil {
    //   获取服务WiFi Ip
    public static String long2ip() {
        Context context= ExceptionApplication.context;
        WifiManager wm = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long ip = di.gateway;
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        Log.e("calm", "------" + sb.toString());
        return sb.toString();
    }

    // 获取当前连接WiFi的IP
    public static String getNowWiFiIP() {
        WifiManager mWifi = (WifiManager) ExceptionApplication.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        String id = wifiInfo.getSSID();
        int ip = wifiInfo.getIpAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    // 获取当前连接WiFi的SSID
    public static String getNowWiFiSSID() {
        WifiManager mWifi = (WifiManager) ExceptionApplication.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        return ssid.substring(1,ssid.length()-1);
    }

    // WiFi 是否连接
    public static boolean isWifiConnect() {
        Context context=ExceptionApplication.context;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    // 连接指定WiFi
    public static void connectWiFi(String ssid, String passward) {
        try {
            if (isWifiConnect() && ssid.equals(getNowWiFiSSID()))
                return;
            WifiManager wifiManager = (WifiManager) ExceptionApplication.context.getSystemService(Context.WIFI_SERVICE);
            WifiConnect wifi = new WifiConnect(wifiManager);
            wifi.openWifi();

            Thread.sleep(2000);

        boolean bool = wifi.connect(ssid, passward, WifiConnect.WifiCipherType.WIFICIPHER_WPA);
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    public static void closeWiFi() {
        WifiManager wifiManager = (WifiManager) ExceptionApplication.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}