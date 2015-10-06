package com.guo.duoduo.p2pmanager.p2pentity;


import java.net.InetAddress;

/**
 * Created by 郭攀峰 on 2015/9/19.
 * 局域网的用户
 */
public class P2PNeighbor
{
    public String alias;
    public String ip;
    public InetAddress inetAddress;

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;

        P2PNeighbor s = (P2PNeighbor) obj;

        if ((s.ip == null))
            return false;

        return (this.ip.equals(s.ip));
    }
}
