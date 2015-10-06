package com.guo.duoduo.p2pmanager.p2pcore.send;


import java.util.HashMap;

import android.util.Log;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.MelonHandler;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;
import com.guo.duoduo.p2pmanager.p2pentity.P2PNeighbor;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamIPMsg;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamSendFiles;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamTCPNotify;

/**
 * Created by 郭攀峰 on 2015/9/20.
 */
public class SendManager
{
    private static final String tag = SendManager.class.getSimpleName();

    private MelonHandler p2PHandler;
    private HashMap<String, Sender> mSenders;

    private SendServer sendServer;
    private SendServerHandler sendServerHandler;

    public SendManager(MelonHandler handler)
    {
        this.p2PHandler = handler;
        mSenders = new HashMap<>();
        init();
    }

    private void init()
    {
        mSenders.clear();
    }

    public void disPatchMsg(int what, Object obj, int src)
    {
        switch (src)
        {
            case P2PConstant.Src.COMMUNICATE :
            {
                String peerIP = ((ParamIPMsg) obj).peerIAddr.getHostAddress();
                Sender sender = getSender(peerIP);
                sender.dispatchCommMSG(what, (ParamIPMsg) obj);	//dispatch
                break;
            }
            case P2PConstant.Src.MANAGER :
            {
                if (what == P2PConstant.CommandNum.SEND_FILE_REQ)
                {
                    if (!mSenders.isEmpty())
                        return;
                    ParamSendFiles param = (ParamSendFiles) obj;
                    invoke(param.neighbors, param.files);
                }
                else if (what == P2PConstant.CommandNum.SEND_ABORT_SELF)
                {
                    Sender sender = getSender(((P2PNeighbor) obj).ip);
                    sender.dispatchUIMSG(what);
                }
                break;
            }
            case P2PConstant.Src.SEND_TCP_THREAD :
            {
                String peerIP = ((ParamTCPNotify) obj).Neighbor.ip;
                Sender sender = getSender(peerIP);
                if (sender == null)
                    return;
                if (what == P2PConstant.CommandNum.SEND_PERCENTS)
                {
                    sender.flagPercents = false;
                }
                sender.dispatchTCPMsg(what, (ParamTCPNotify) obj);
                break;
            }

        }
    }

    private void invoke(P2PNeighbor[] neighbors, P2PFileInfo[] files)
    {
        StringBuffer stringBuffer = new StringBuffer("");
        for (P2PFileInfo fileInfo : files)
        {
            stringBuffer.append(fileInfo.toString());
        }
        String add = stringBuffer.toString();

        for (P2PNeighbor neighbor : neighbors)
        {
            P2PNeighbor melon = p2PHandler.getNeighborManager().getNeighbors()
                    .get(neighbor.ip);
            Sender sender = null;
            if (melon != null)
            {
                sender = new Sender(p2PHandler, this, melon, files);
            }

            mSenders.put(neighbor.ip, sender);

            if (melon != null) //通知对方，我要发送文件了
            {
                if (p2PHandler != null)
                    p2PHandler.send2Receiver(melon.inetAddress,
                        P2PConstant.CommandNum.SEND_FILE_REQ, add);
            }
        }
    }

    public void startSend(String peerIP, Sender fileSender)
    {
        if (sendServer == null)
        {
            Log.d(tag, "SendManager start send");

            sendServerHandler = new SendServerHandler(this);
            sendServer = new SendServer(sendServerHandler, P2PConstant.PORT);
            sendServer.start();
            sendServer.isReady();
        }
        mSenders.put(fileSender.neighbor.ip, fileSender);
    }

    public void removeSender(String peerIP)
    {
        mSenders.remove(peerIP);
        checkAllOver();
    }

    public void checkAllOver()
    {
        if (mSenders.isEmpty())
        {
            p2PHandler.releaseSend();
        }
    }

    public void quit()
    {
        mSenders.clear();
        if (sendServer != null)
        {
            sendServer.quit();
            sendServer = null;
        }
    }

    protected Sender getSender(String peerIP)
    {
        return mSenders.get(peerIP);
    }
}
