package com.guo.duoduo.p2pmanager.p2pcore.receive;


import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.MelonHandler;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;
import com.guo.duoduo.p2pmanager.p2pentity.P2PNeighbor;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamIPMsg;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamReceiveFiles;

/**
 * Created by 郭攀峰 on 2015/9/20.
 */
public class ReceiveManager
{

    protected MelonHandler p2PHandler;
    private Receiver receiver;

    public ReceiveManager(MelonHandler handler)
    {
        p2PHandler = handler;
    }

    public void init()
    {
        if (receiver != null)
            receiver = null;
    }

    public void disPatchMsg(int cmd, Object obj, int src)
    {
        switch (src)
        {
            case P2PConstant.Src.COMMUNICATE :
            {
                ParamIPMsg paramIPMsg = (ParamIPMsg) obj;
                if (cmd == P2PConstant.CommandNum.SEND_FILE_REQ)
                {
                    invoke(paramIPMsg);
                }
                else
                {
                    if (receiver != null)
                        receiver.dispatchCommMSG(cmd, paramIPMsg);
                }
                break;
            }
            case P2PConstant.Src.MANAGER :
                if (receiver != null)
                    receiver.dispatchUIMSG(cmd, obj);
                break;
            case P2PConstant.Src.RECEIVE_TCP_THREAD :
                if (cmd == P2PConstant.CommandNum.RECEIVE_PERCENT)
                    if (receiver != null)
                        receiver.flagPercent = false;
                if (receiver != null)
                    receiver.dispatchTCPMSG(cmd, obj);
                break;
        }
    }

    public void quit()
    {
        init();
    }

    private void invoke(ParamIPMsg paramIPMsg)
    {
        String peerIP = paramIPMsg.peerIAddr.getHostAddress();

        String[] strArray = paramIPMsg.peerMSG.addition.split(P2PConstant.MSG_SEPARATOR);
        P2PFileInfo[] files = new P2PFileInfo[strArray.length];
        for (int i = 0; i < strArray.length; i++)
        {
            files[i] = new P2PFileInfo(strArray[i]);
        }

        P2PNeighbor neighbor = p2PHandler.getNeighborManager().getNeighbors().get(peerIP);

        receiver = new Receiver(this, neighbor, files);

        ParamReceiveFiles paramReceiveFiles = new ParamReceiveFiles(neighbor, files);
        if (p2PHandler != null)
            p2PHandler.send2UI(P2PConstant.CommandNum.SEND_FILE_REQ, paramReceiveFiles);
    }
}
