package com.guo.duoduo.p2pmanager.p2pcore;


import java.io.File;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;
import com.guo.duoduo.p2pmanager.p2pentity.P2PNeighbor;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamIPMsg;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamReceiveFiles;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamSendFiles;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamTCPNotify;
import com.guo.duoduo.p2pmanager.p2pinterface.Melon_Callback;
import com.guo.duoduo.p2pmanager.p2pinterface.ReceiveFile_Callback;
import com.guo.duoduo.p2pmanager.p2pinterface.SendFile_Callback;


/**
 * Created by 郭攀峰 on 2015/9/17.
 */
public class P2PManager
{

    private static final String tag = P2PManager.class.getSimpleName();

    private static String SAVE_DIR = Environment.getExternalStorageDirectory().getPath()
        + File.separator + P2PConstant.FILE_SHARE_SAVE_PATH;

    private P2PNeighbor meMelonInfo;
    private Melon_Callback melon_callback;
    private CustomHandlerThread p2pThread;
    private MelonHandler p2PHandler;
    private P2PManagerHandler mHandler;

    private ReceiveFile_Callback receiveFile_callback;
    private SendFile_Callback sendFile_callback;

    private Context mContext;

    public P2PManager(Context context)
    {
        mContext = context;
        mHandler = new P2PManagerHandler(this);
    }

    public void start(P2PNeighbor melon, Melon_Callback melon_callback)
    {
        this.meMelonInfo = melon;
        this.melon_callback = melon_callback;

        p2pThread = new CustomHandlerThread("P2PThread", MelonHandler.class);
        p2pThread.start();
        p2pThread.isReady();

        p2PHandler = (MelonHandler) p2pThread.getLooperHandler();
        p2PHandler.init(this, mContext);
    }

    public void receiveFile(ReceiveFile_Callback callback)
    {
        receiveFile_callback = callback;
        p2PHandler.initReceive();
    }

    public void sendFile(P2PNeighbor[] dsts, P2PFileInfo[] files,
            SendFile_Callback callback)
    {
        this.sendFile_callback = callback;
        p2PHandler.initSend();

        ParamSendFiles paramSendFiles = new ParamSendFiles(dsts, files);
        p2PHandler.send2Handler(P2PConstant.CommandNum.SEND_FILE_REQ,
            P2PConstant.Src.MANAGER, P2PConstant.Recipient.FILE_SEND, paramSendFiles);
    }

    public void ackReceive()
    {
        p2PHandler.send2Handler(P2PConstant.CommandNum.RECEIVE_FILE_ACK,
            P2PConstant.Src.MANAGER, P2PConstant.Recipient.FILE_RECEIVE, null);
    }

    public P2PNeighbor getSelfMeMelonInfo()
    {
        return meMelonInfo;
    }

    public Handler getHandler()
    {
        return mHandler;
    }

    public void stop()
    {
        if (p2pThread != null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(tag, "p2pManager stop");
                    ((MelonHandler) p2pThread.getLooperHandler()).release();
                    p2pThread.quit();
                    p2pThread = null;
                    p2PHandler = null;
                }
            }).start();
        }
    }

    public void cancelReceive()
    {
        p2PHandler.send2Handler(P2PConstant.CommandNum.RECEIVE_ABORT_SELF,
            P2PConstant.Src.MANAGER, P2PConstant.Recipient.FILE_RECEIVE, null);
    }

    public void cancelSend(P2PNeighbor neighbor)
    {
        p2PHandler.send2Handler(P2PConstant.CommandNum.SEND_ABORT_SELF,
            P2PConstant.Src.MANAGER, P2PConstant.Recipient.FILE_SEND, neighbor);
    }

    public static String getSavePath(int type)
    {
        String[] typeStr = {"APP", "Picture"};
        return SAVE_DIR + File.separator + typeStr[type];
    }

    /**
     * 获取广播地址
     * 
     * @param context
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context)
            throws UnknownHostException
    {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null)
        {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public static void setSaveDir(String dir)
    {
        SAVE_DIR = dir;
    }

    public static String getSaveDir()
    {
        return SAVE_DIR;
    }

    private static class P2PManagerHandler extends Handler
    {
        private WeakReference<P2PManager> weakReference;

        public P2PManagerHandler(P2PManager manager)
        {
            this.weakReference = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg)
        {
            P2PManager manager = weakReference.get();
            if (manager == null)
                return;

            switch (msg.what)
            {
                case P2PConstant.UI_MSG.ADD_NEIGHBOR :
                    if (manager.melon_callback != null)
                        manager.melon_callback.Melon_Found((P2PNeighbor) msg.obj);
                    break;
                case P2PConstant.UI_MSG.REMOVE_NEIGHBOR :
                    if (manager.melon_callback != null)
                        manager.melon_callback.Melon_Removed((P2PNeighbor) msg.obj);
                    break;
                case P2PConstant.CommandNum.SEND_FILE_REQ : //收到请求发送文件
                    if (manager.receiveFile_callback != null)
                    {
                        ParamReceiveFiles params = (ParamReceiveFiles) msg.obj;
                        manager.receiveFile_callback.QueryReceiving(params.Neighbor,
                            params.Files);
                    }
                    break;
                case P2PConstant.CommandNum.SEND_FILE_START : //发送端开始发送
                    if (manager.sendFile_callback != null)
                    {
                        manager.sendFile_callback.BeforeSending();
                    }
                    break;
                case P2PConstant.CommandNum.SEND_PERCENTS :
                    ParamTCPNotify notify = (ParamTCPNotify) msg.obj;
                    if (manager.sendFile_callback != null)
                        manager.sendFile_callback.OnSending((P2PFileInfo) notify.Obj,
                            notify.Neighbor);
                    break;
                case P2PConstant.CommandNum.SEND_OVER :
                    if (manager.sendFile_callback != null)
                        manager.sendFile_callback.AfterSending((P2PNeighbor) msg.obj);
                    break;
                case P2PConstant.CommandNum.SEND_ABORT_SELF : //通知接收者，发送者退出了
                    if (manager.receiveFile_callback != null)
                    {
                        ParamIPMsg paramIPMsg = (ParamIPMsg) msg.obj;
                        if (paramIPMsg != null)
                            manager.receiveFile_callback.AbortReceiving(
                                P2PConstant.CommandNum.SEND_ABORT_SELF,
                                paramIPMsg.peerMSG.senderAlias);
                    }
                    break;
                case P2PConstant.CommandNum.RECEIVE_ABORT_SELF : //通知发送者，接收者退出了
                    if (manager.sendFile_callback != null)
                        manager.sendFile_callback.AbortSending(msg.what,
                            (P2PNeighbor) msg.obj);
                    break;
                case P2PConstant.CommandNum.RECEIVE_OVER :
                    if (manager.receiveFile_callback != null)
                        manager.receiveFile_callback.AfterReceiving();
                    break;
                case P2PConstant.CommandNum.RECEIVE_PERCENT :
                    if (manager.receiveFile_callback != null)
                        manager.receiveFile_callback.OnReceiving((P2PFileInfo) msg.obj);
                    break;
            }
        }
    }

}
