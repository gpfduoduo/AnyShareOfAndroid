package com.guo.duoduo.p2pmanager.p2pentity;


import com.guo.duoduo.p2pmanager.p2pcore.P2PManager;

import java.io.File;


/**
 * Created by 郭攀峰 on 2015/9/16.
 * android设备中的文件
 */
public class P2PFileInfo
{
    public String path;
    public String name;
    public long size;
    public int type;
    public int percent;
    public boolean success;
    public long LengthNeeded = 0;

    public P2PFileInfo()
    {

    }

    public int getPercent()
    {
        return percent;
    }

    public void setPercent(int percent)
    {
        this.percent = percent;
        if (percent == 100)
        {
            success = true;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        return (((P2PFileInfo) (o)).name.equals(name))
            && (((P2PFileInfo) (o)).size == size) && (((P2PFileInfo) (o)).type == type)
            && (((P2PFileInfo) (o)).path.equals(path));
    }

    public P2PFileInfo(String string)
    {
        String str[] = string.split(":");
        name = str[0];
        size = Long.parseLong(str[1]);
        type = Integer.parseInt(str[2]);

        path = P2PManager.getSavePath(type) + File.separator + name;
    }

    @Override
    public String toString()
    {
        return name + ":" + size + ":" + type + "\0";
    }

    public P2PFileInfo duplicate()
    {
        P2PFileInfo file = new P2PFileInfo();

        file.name = this.name;
        file.size = this.size;
        file.path = this.path;
        file.type = this.type;
        file.percent = this.percent;
        file.success = this.success;
        file.LengthNeeded = this.LengthNeeded;

        return file;
    }
}
