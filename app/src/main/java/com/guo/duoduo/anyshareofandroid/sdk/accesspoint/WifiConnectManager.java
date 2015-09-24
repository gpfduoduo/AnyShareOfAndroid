package com.guo.duoduo.anyshareofandroid.sdk.accesspoint;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.utils.NetworkUtils;
import com.guo.duoduo.anyshareofandroid.utils.ToastUtils;


/**
 * function: class to manager the WiFi connect etc.
 * */

public class WifiConnectManager extends WifiManagerWrap
{

    private static final int DEFAULT_PRIORITY = 10000;

    private OnWifiScanFinishListener mWifiScanFinishListener;
    private OnWifiConnectListener mWifiConnectListener;
    private OnWifiDisconnectListener mWifiDisconnectListener;
    private int Auto_Scan_Time = 1500;// auto scan time interval
    private Context mContext;

    public WifiConnectManager(Context context)
    {
        super(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        mContext = context;
        mContext.registerReceiver(mWifiReceiver, intentFilter);
    }

    /** set WiFi scan listener */
    public void setScanFinishListener(OnWifiScanFinishListener listener)
    {
        mWifiScanFinishListener = listener;
    }

    /** WiFi state listener */
    public void setOnNetworkConnect(OnWifiConnectListener listener)
    {
        mWifiConnectListener = listener;
    }

    public void setOnNetworkDisconnect(OnWifiDisconnectListener listener)
    {
        mWifiDisconnectListener = listener;
    }

    private void handleNetworkStateChanged(NetworkInfo networkInfo)
    {
        if (networkInfo.isConnected() && mWifiConnectListener != null)
        {
            String ssid = NetworkUtils.getCurrentSSID(MyApplication.getInstance());
            if (ssid != null)
            {
                if (ssid.contains(SSID_PREFIX)) // 2014-1-21 add
                    mWifiConnectListener.onWifiConnected();
            }
            else
            {
                ToastUtils
                        .showTextToast(MyApplication.getInstance(), "您当前WiFi存在异常，请重新连接");
            }
        }
        else if (!networkInfo.isConnected() && mWifiDisconnectListener != null)
        { //2014-1-22 add
            mWifiDisconnectListener.onWifiDisconnected();
        }
        else
        {
        }
    }

    private void handleScanResultsFinish()
    {
        List<ScanResult> list = mWifiManager.getScanResults();
        if (list != null)
        {
            /*
             * for (ScanResult item : list) { }
             */
            List<ScanResult> filterList = scanFinishedEvent(list);
            if (mWifiScanFinishListener != null)
            {
                mWifiScanFinishListener.onScanFinished(filterList);
            }
        }
    }

    /** p2pinterface of BroadcastReceiver */
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
            {
                handleWiFiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                /* handle wifi connect success */
                handleNetworkStateChanged((NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
            }
            else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                /* handle scan finish */
                handleScanResultsFinish();
            }
            else if (intent.getAction().equals(
                WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION))
            {
            }
            else if (intent.getAction().equals(
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
            {
            }
            else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
            {
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_IDS_CHANGED_ACTION))
            {
            }
            else
            {
            }
        }

    };

    /** 停止wifi热点，解绑定监听器 */
    public void destroy()
    {
        removeNetwork(SSID_PREFIX);
        if (mWifiReceiver != null && mContext != null)
        {
            try
            {
                mContext.unregisterReceiver(mWifiReceiver);
                mContext = null;
            }
            catch (IllegalArgumentException e)
            {
            }
        }
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
        releaseWifiLock();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mWifiManager != null)
            {
                mWifiManager.startScan();
                handler.postDelayed(this, Auto_Scan_Time);
            }
        }
    };

    /** handle wifi state changed 2014-1-22 add */
    protected void handleWiFiStateChanged(int wifiState)
    {
        switch (wifiState)
        {
            case WifiManager.WIFI_STATE_ENABLED :
                mWifiManager.startScan();
                handler.postDelayed(runnable, Auto_Scan_Time); // scan WiFi each Atuo_Scan_Time
                break;
            case WifiManager.WIFI_STATE_DISABLED :
        }
    }

    /** Check Whether or not connect */
    public boolean isWifiConnect()
    {
        final ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return info.isConnected();
    }

    private List<ScanResult> scanFinishedEvent(List<ScanResult> scanResults)
    {
        List<ScanResult> filter = filterAccessPoint(scanResults);
        return filter;
    }

    /** filter the scaned WiFi Hot Spot */
    private List<ScanResult> filterAccessPoint(List<ScanResult> results)
    {
        if (results != null)
        {
            List<ScanResult> filteredResult = new ArrayList<ScanResult>();
            for (ScanResult result : results)
            {
                if (result.SSID.contains(SSID_PREFIX))
                {
                    filteredResult.add(result);
                }
            }

            return filteredResult;
        }

        return null;
    }

    /** connect to the specified WiFi hot spot */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean connectToAccessPoint(String ssid, String bssid)
    {
        boolean ret = false;
        removeNetwork(SSID_PREFIX); // remove the current WiFi connect
        //if the current is not the special WiFi hot spot, disconnect
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();

        mWifiManager.disableNetwork(connectionInfo.getNetworkId());
        mWifiManager.disconnect();

        //down the priority of other WiFi 
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs)
        {
            if ((config.SSID != null) && !config.SSID.equals("\"" + ssid + "\""))
            {
                config.priority = 0;
                mWifiManager.updateNetwork(config);
            }
        }

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        //wifiConfig.preSharedKey = "\"" + DEFAULT_PASSWORD + "\"";//WiFi encrypt	      

        wifiConfig.BSSID = bssid;
        wifiConfig.priority = DEFAULT_PRIORITY;

        //setWifiConfigAsWPA(wifiConfig);//WiFi encrypt
        setWifiConfig(wifiConfig);//WiFi no encrypt

        try
        {
            Field field = wifiConfig.getClass().getField("ipAssignment");
            field.set(wifiConfig, Enum.valueOf((Class<Enum>) field.getType(), "DHCP"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        int networkId = mWifiManager.addNetwork(wifiConfig);

        ret = mWifiManager.enableNetwork(networkId, true);
        return ret;
    }

    /** start scan WiFi hot spot */
    public void startScan()
    {
        openWifi();
        acquireWifiLock();
        //mWifiManager.startScan(); //modify, wait the WiFi is connected
    }

    @SuppressWarnings("unused")
    private WifiConfiguration getWifiConfiguration(String ssid)
    {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs)
        {
            loadWifiConfigurationFromProfile(config);
            if ((config.SSID != null) && config.SSID.equals("\"" + ssid + "\""))
            {
                return config;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void removeConnection()
    {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        mWifiManager.removeNetwork(wifiInfo.getNetworkId());
        mWifiManager.saveConfiguration();
    }

    @SuppressWarnings("unused")
    private WifiInfo getConnectionInfo()
    {
        return mWifiManager.getConnectionInfo();
    }

    /** wifi scan end listener */
    public static interface OnWifiScanFinishListener
    {
        public void onScanFinished(List<ScanResult> scanResults);
    }

    /** network status of wifi */
    public interface OnWifiConnectListener
    {
        public void onWifiConnected();
    }

    public interface OnWifiDisconnectListener
    {
        public void onWifiDisconnected();
    }

}
