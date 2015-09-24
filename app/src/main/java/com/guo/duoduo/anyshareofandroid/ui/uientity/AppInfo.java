package com.guo.duoduo.anyshareofandroid.ui.uientity;


import android.graphics.drawable.Drawable;

import com.guo.duoduo.anyshareofandroid.constant.Constant;
import com.guo.duoduo.anyshareofandroid.sdk.p2p.p2pconstant.P2PConstant;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class AppInfo implements IInfo
{
    public int type = P2PConstant.TYPE.APP;
    public Drawable appIcon;
    public String appLabel;
    public String pkgName;
    public String appSize;
    public String appFilePath;

    @Override
    public String getFilePath()
    {
        return appFilePath;
    }

    @Override
    public String getFileSize()
    {
        return appSize;
    }

    @Override
    public int getFileType()
    {
        return type;
    }

    @Override
    public Drawable getFileIcon()
    {
        return appIcon;
    }

    @Override
    public String getFileName()
    {
        return appLabel;
    }

    @Override
    public boolean equals(Object o)
    {
        return getFilePath().equals(((AppInfo) o).getFilePath());
    }

}
