package com.guo.duoduo.p2pmanager.p2pentity.param;


import com.guo.duoduo.p2pmanager.p2pentity.SigMessage;

import java.net.InetAddress;


/**
 * Created by 郭攀峰 on 2015/9/20.
 */
public class ParamIPMsg
{
    public SigMessage peerMSG;
    public InetAddress peerIAddr;
    public int peerPort;

    public ParamIPMsg(String msg, InetAddress addr, int port)
    {
        peerMSG = new SigMessage(msg);
        peerIAddr = addr;
        peerPort = port;
    }
}
