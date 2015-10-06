package com.guo.duoduo.httpserver.utils;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * Created by Guo.Duo duo on 2015/9/4.
 */
public class Network
{
    public static String getLocalIp(Context context)
    {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);//获取WifiManager

        //检查wifi是否开启
        if (!wifiManager.isWifiEnabled())
        {
            return null;
        }

        WifiInfo wifiinfo = wifiManager.getConnectionInfo();

        String ip = intToIp(wifiinfo.getIpAddress());

        return ip;
    }

    private static String intToIp(int paramInt)
    {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
            + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }
}
