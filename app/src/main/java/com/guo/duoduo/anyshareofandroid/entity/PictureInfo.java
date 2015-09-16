package com.guo.duoduo.anyshareofandroid.entity;


import android.graphics.drawable.Drawable;

import com.guo.duoduo.anyshareofandroid.constant.Constant;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class PictureInfo implements IInfo
{
    public int type = Constant.MediaType.PICTURE;
    public String picPath;
    public String picSize;
    public String picName;

    @Override
    public String getFilePath()
    {
        return picPath;
    }

    @Override
    public String getFileSize()
    {
        return picSize;
    }

    @Override
    public int getFileType()
    {
        return type;
    }

    @Override
    public Drawable getFileIcon()
    {
        return null;
    }

    @Override
    public String getFileName()
    {
        return picName;
    }

    @Override
    public boolean equals(Object o)
    {
        return getFilePath().equals(((PictureInfo) o).getFilePath());
    }
}
