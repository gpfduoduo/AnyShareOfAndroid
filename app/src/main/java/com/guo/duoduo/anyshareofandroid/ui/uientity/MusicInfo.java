package com.guo.duoduo.anyshareofandroid.ui.uientity;

import android.graphics.drawable.Drawable;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;

/**
 * Created by longsky on 2017/4/18.
 */

public class MusicInfo implements IInfo {

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getFileSize() {
        return fileSize;
    }

    @Override
    public int getFileType() {
        return type;
    }

    @Override
    public Drawable getFileIcon() {
        return null;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName){
        this.fileName = fileName;
    }

    public void setFilePath(final String filePath){
        this.filePath = filePath;
    }

    public void setFileSize(final String fileSize){
        this.fileSize = fileSize;
    }

    private String fileName;

    private String filePath;

    private String fileSize;
    private final int type = P2PConstant.TYPE.MUSIC;
}
