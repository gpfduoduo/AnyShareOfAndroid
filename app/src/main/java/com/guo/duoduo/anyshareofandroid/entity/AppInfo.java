package com.guo.duoduo.anyshareofandroid.entity;


import android.graphics.drawable.Drawable;

import com.guo.duoduo.anyshareofandroid.constant.Constant;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class AppInfo implements IInfo
{
    public int type = Constant.MediaType.APP;
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
