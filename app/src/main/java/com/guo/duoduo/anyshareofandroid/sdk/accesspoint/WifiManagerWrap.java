package com.guo.duoduo.anyshareofandroid.sdk.accesspoint;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.guo.duoduo.anyshareofandroid.constant.Constant;


public class WifiManagerWrap
{

    /** no pin */
    public static final int TYPE_NO_PASSWD = 0x11;
    /** wep */
    public static final int TYPE_WEP = 0x12;
    /** wpa */
    public static final int TYPE_WPA = 0x13;
    /** the default */
    public static final int DEFAULT_TYPE = TYPE_WPA;
    /** WiFi hot spot prefix */
    public static final String SSID_PREFIX = Constant.WIFI_HOT_SPOT_SSID_PREFIX;
    /** default SSID */
    public static final String DEFAULT_SSID = "";
    /** default WiFi pin */
    public static final String DEFAULT_PASSWORD = "";
    /**
     * To follow the limit of the max character length is 32 as a wifi access
     * name
     */
    public static final int WIFI_AP_NAME_MAX_COUNT = 32;
    /**
     * prefix length
     */
    public static final int IDENTIFY_PREFIX_LENGTH = 6;

    /**
     * runtime context
     */
    protected Context mContext;
    /**
     * wifi manager service instance
     */
    protected WifiManager mWifiManager;
    /**
     * wifi lock
     */
    protected WifiManager.WifiLock mWifiLock;

    WifiManagerWrap(Context context)
    {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, SSID_PREFIX);
        mContext = context;
    }

    protected void acquireWifiLock()
    {
        if (!mWifiLock.isHeld())
        {
            mWifiLock.acquire();
        }
    }

    protected void releaseWifiLock()
    {
        if (mWifiLock.isHeld())
        {
            mWifiLock.release();
        }
    }

    /**
     * start WiFi connect
     */
    public boolean startWifi()
    {
        try
        {
            Method method = mWifiManager.getClass().getMethod("startWifi");
            return (Boolean) method.invoke(mWifiManager);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * stop WiFi connect
     */
    public boolean stopWifi()
    {
        try
        {
            Method method = mWifiManager.getClass().getMethod("stopWifi");
            return (Boolean) method.invoke(mWifiManager);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public void closeWifi()
    {
        int wifiState = mWifiManager.getWifiState();
        if (((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED)))
        {
            mWifiManager.setWifiEnabled(false);
        }

    }

    /** open WiFi */
    public void openWifi()
    {
        if (!mWifiManager.isWifiEnabled())
        {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * clear the special net Info
     */
    public void removeNetwork(String ssid)
    {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null)
        {
            for (WifiConfiguration config : configs)
            {
                loadWifiConfigurationFromProfile(config);
                if ((config.SSID != null) && config.SSID.contains(ssid))
                {
                    mWifiManager.disableNetwork(config.networkId);
                    mWifiManager.removeNetwork(config.networkId);
                }
            }
        }
        mWifiManager.saveConfiguration();

    }

    protected void loadWifiConfigurationFromProfile(WifiConfiguration wifiConfiguration)
    {
        if (wifiConfiguration != null)
        {
            if (TextUtils.isEmpty(wifiConfiguration.SSID)
                || TextUtils.isEmpty(wifiConfiguration.BSSID))
            {
                try
                {
                    Field wifiApProfileField = WifiConfiguration.class
                            .getDeclaredField("mWifiApProfile");
                    wifiApProfileField.setAccessible(true);
                    Object wifiApProfile = wifiApProfileField.get(wifiConfiguration);
                    wifiApProfileField.setAccessible(false);

                    if (wifiApProfile != null)
                    {
                        Field ssidField = wifiApProfile.getClass().getDeclaredField(
                            "SSID");
                        ssidField.setAccessible(true);
                        Object value2 = ssidField.get(wifiApProfile);
                        if (value2 != null)
                        {
                            wifiConfiguration.SSID = (String) value2;
                        }
                        ssidField.setAccessible(false);

                        Field bssidField = wifiApProfile.getClass().getDeclaredField(
                            "BSSID");
                        bssidField.setAccessible(true);
                        Object value3 = bssidField.get(wifiApProfile);
                        if (value3 != null)
                        {
                            wifiConfiguration.BSSID = (String) value3;
                        }
                        bssidField.setAccessible(false);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * WPA connect WPA/WPA2
     */
    protected void setWifiConfigAsWPA(WifiConfiguration wifiConfig)
    {
        wifiConfig.hiddenSSID = false;
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        setWifiConfigurationProfile(wifiConfig);
    }

    public int setWifiApConfig(WifiConfiguration config)
    {
        try
        {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfig",
                WifiConfiguration.class);
            return (Integer) method.invoke(mWifiManager, config);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 4;
        }
    }

    /**
     * no WiFi password
     */
    protected void setWifiConfig(WifiConfiguration wifiConfig)
    {
        wifiConfig.wepKeys[0] = "\"" + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.wepTxKeyIndex = 0;
        setWifiConfigurationProfileWithoutKey(wifiConfig);
    }

    private void setWifiConfigurationProfileWithoutKey(WifiConfiguration wifiConfiguration)
    {
        if (wifiConfiguration != null)
        {
            try
            {
                Field wifiApProfileField = WifiConfiguration.class
                        .getDeclaredField("mWifiApProfile");
                wifiApProfileField.setAccessible(true);
                Object wifiApProfile = wifiApProfileField.get(wifiConfiguration);
                wifiApProfileField.setAccessible(false);

                if (wifiApProfile != null)
                {
                    Field ssidField = wifiApProfile.getClass().getDeclaredField("SSID");
                    ssidField.setAccessible(true);
                    ssidField.set(wifiApProfile, wifiConfiguration.SSID);
                    ssidField.setAccessible(false);

                    Field bssidField = wifiApProfile.getClass().getDeclaredField("BSSID");
                    bssidField.setAccessible(true);
                    bssidField.set(wifiApProfile, wifiConfiguration.BSSID);
                    bssidField.setAccessible(false);

                    Field dhcpField = wifiApProfile.getClass().getDeclaredField(
                        "dhcpEnable");
                    dhcpField.setAccessible(true);
                    dhcpField.set(wifiApProfile, 1);
                    dhcpField.setAccessible(false);

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void setWifiConfigurationProfile(WifiConfiguration wifiConfiguration)
    {

        if (wifiConfiguration != null)
        {
            try
            {
                Field wifiApProfileField = WifiConfiguration.class
                        .getDeclaredField("mWifiApProfile");
                wifiApProfileField.setAccessible(true);
                Object wifiApProfile = wifiApProfileField.get(wifiConfiguration);
                wifiApProfileField.setAccessible(false);

                if (wifiApProfile != null)
                {
                    Field ssidField = wifiApProfile.getClass().getDeclaredField("SSID");
                    ssidField.setAccessible(true);
                    ssidField.set(wifiApProfile, wifiConfiguration.SSID);
                    ssidField.setAccessible(false);

                    Field bssidField = wifiApProfile.getClass().getDeclaredField("BSSID");
                    bssidField.setAccessible(true);
                    bssidField.set(wifiApProfile, wifiConfiguration.BSSID);
                    bssidField.setAccessible(false);

                    Field dhcpField = wifiApProfile.getClass().getDeclaredField(
                        "dhcpEnable");
                    dhcpField.setAccessible(true);
                    dhcpField.set(wifiApProfile, 1);
                    dhcpField.setAccessible(false);

                    Field keyField = wifiApProfile.getClass().getDeclaredField("key");
                    keyField.setAccessible(true);
                    keyField.set(wifiApProfile, wifiConfiguration.preSharedKey);
                    keyField.setAccessible(false);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * 设置移动网络是否允许打开
     * 
     * @param context 上下文
     * @param enabled 是否允许打开移动网络
     */
    public void setMobileDataEnabled(Context context, boolean enabled)
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod(
                "setMobileDataEnabled", boolean.class);
            method.invoke(connectivityManager, enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 获取当前移动网络是否打开
     * 
     * @param context 上下文
     * @return 移动网络是否打开
     */
    public boolean getMobileDataEnabled(Context context)
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod(
                "getMobileDataEnabled");

            return (Boolean) method.invoke(connectivityManager);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

}
