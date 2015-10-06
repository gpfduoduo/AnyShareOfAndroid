package com.guo.duoduo.p2pmanager.p2pentity;


/**
 * Created by 郭攀峰 on 2015/9/21.
 */
public class SocketTransInfo
{
    public long Offset;
    public long Length;
    public long Transferred;
    public int P2PFile_Idx;
    public int Index;

    public SocketTransInfo(int idx)
    {
        P2PFile_Idx = idx;
        Length = 0; // need not to be transferred
        Transferred = 0;
    }

    @Override
    public String toString()
    {
        return Index + ":" + P2PFile_Idx + ":" + Offset + ":" + Length;
    }

    public SocketTransInfo(String protocolString)
    {
        String[] args = protocolString.split(":");
        Index = Integer.parseInt(args[0]);
        P2PFile_Idx = Integer.parseInt(args[1]);
        Offset = Long.parseLong(args[2]);
        Length = Long.parseLong(args[3]);
    }
}
