package com.guo.duoduo.anyshareofandroid.ui.receive;


import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.constant.Constant;
import com.guo.duoduo.anyshareofandroid.sdk.accesspoint.AccessPointManager;
import com.guo.duoduo.anyshareofandroid.ui.view.CommonProgressDialog;
import com.guo.duoduo.anyshareofandroid.utils.NetworkUtils;
import com.guo.duoduo.anyshareofandroid.utils.ToastUtils;


public class ReceiveActivity extends AppCompatActivity
    implements
        AccessPointManager.OnWifiApStateChangeListener
{

    private static final String tag = ReceiveActivity.class.getSimpleName();

    private AccessPointManager mWifiApManager = null;
    private Random random = new Random();
    private CommonProgressDialog progressDialog;
    private TextView wifiName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_receive_toolbar);
        setSupportActionBar(toolbar);

        wifiName = (TextView) findViewById(R.id.activity_receive_radar_wifi);

        if (!NetworkUtils.isWifiConnected(MyApplication.getInstance()))
        { //create wifi hot spot
            Log.d(tag, "no WiFi init wifi hotspot");
            intWifiHotSpot();
        }
        else
        {
            Log.d(tag, "useWiFi");
            wifiName.setText(String.format(getString(R.string.send_connect_to),
                NetworkUtils.getCurrentSSID(ReceiveActivity.this)));
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //stop wifi hot spot
        closeAccessPoint();
    }

    private void intWifiHotSpot()
    {
        progressDialog = new CommonProgressDialog(ReceiveActivity.this);
        progressDialog.setMessage(getString(R.string.wifi_hotspot_creating));
        progressDialog.show();

        mWifiApManager = new AccessPointManager(MyApplication.getInstance());
        mWifiApManager.setWifiApStateChangeListener(this);
        createAccessPoint();
    }

    private void createAccessPoint()
    {
        mWifiApManager.createWifiApSSID(Constant.WIFI_HOT_SPOT_SSID_PREFIX
            + android.os.Build.MODEL + "-" + random.nextInt(1000));

        if (!mWifiApManager.startWifiAp())
        {
            if (progressDialog != null)
                progressDialog.dismiss();

            ToastUtils.showTextToast(MyApplication.getInstance(),
                getString(R.string.wifi_hotspot_fail));
            onBackPressed();
        }
    }

    private void closeAccessPoint()
    {
        try
        {
            if (mWifiApManager != null && mWifiApManager.isWifiApEnabled())
            {
                mWifiApManager.stopWifiAp(false);
                mWifiApManager.destroy(this);
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onWifiStateChanged(int state)
    {
        if (state == AccessPointManager.WIFI_AP_STATE_ENABLED)
        {
            onBuildWifiApSuccess();
        }
        else if (AccessPointManager.WIFI_AP_STATE_FAILED == state)
        {
            onBuildWifiApFailed();
        }
    }

    private void onBuildWifiApFailed()
    {
        ToastUtils.showTextToast(MyApplication.getInstance(),
            getString(R.string.wifi_hotspot_fail));

        if (progressDialog != null)
            progressDialog.dismiss();

        onBackPressed();
    }

    private void onBuildWifiApSuccess()
    {
        if (progressDialog != null)
            progressDialog.dismiss();

        wifiName.setText(String.format(getString(R.string.send_connect_to),
            mWifiApManager.getWifiApSSID()));
    }

}
