package com.guo.duoduo.anyshareofandroid.constant;


import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.R;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class Constant
{
    public static final String WIFI_HOT_SPOT_SSID_PREFIX = MyApplication.getInstance()
            .getString(R.string.app_name);
    public static final String FREE_SERVER = "192.168.43.1";

    public interface MSG
    {
        public static final int PICTURE_OK = 0;
        public static final int APP_OK = 1;
    }

}
