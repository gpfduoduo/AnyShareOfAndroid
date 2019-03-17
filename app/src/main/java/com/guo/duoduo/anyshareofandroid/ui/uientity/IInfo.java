package com.guo.duoduo.anyshareofandroid.ui.uientity;


import android.graphics.drawable.Drawable;

/**
 * Created by 郭攀峰 on 2015/9/16.
 */
public interface IInfo {
    String getFilePath();
    String getFileSize();
    int getFileType();
    Drawable getFileIcon();
    String getFileName();

}
