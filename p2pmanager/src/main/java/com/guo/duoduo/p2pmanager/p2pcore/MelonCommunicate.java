package com.guo.duoduo.p2pmanager.p2pcore;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PNeighbor;
import com.guo.duoduo.p2pmanager.p2pentity.SigMessage;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamIPMsg;


/**
 * Created by 郭攀峰 on 2015/9/19. 接收端和发送端的udp交互
 */
public class MelonCommunicate extends Thread
{

    private static final String tag = MelonCommunicate.class.getSimpleName();

    private MelonHandler p2PHandler;
    private P2PManager p2PManager;

    private DatagramSocket udpSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private byte[] resBuffer = new byte[P2PConstant.BUFFER_LENGTH];
    private byte[] sendBuffer = null;
    private String[] mLocalIPs;
    private boolean isStopped = false;

    private Context mContext;

    public MelonCommunicate(P2PManager manager, MelonHandler handler, Context context)
    {
        mContext = context;
        this.p2PHandler = handler;
        this.p2PManager = manager;
        setPriority(MAX_PRIORITY);
        init();
    }

    public void BroadcastMSG(int cmd, int recipient)
    {
        try
        {
            sendMsg2Peer(
            /* InetAddress.getByName(P2PConstant.MULTI_ADDRESS) */
            P2PManager.getBroadcastAddress(mContext), cmd, recipient, null);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMsg2Peer(InetAddress sendTo, int cmd, int recipient, String add)
    {
        SigMessage sigMessage = getSelfMsg(cmd);
        if (add == null)
            sigMessage.addition = "null";
        else
            sigMessage.addition = add;
        sigMessage.recipient = recipient;

        sendUdpData(sigMessage.toProtocolString(), sendTo);
    }

    private synchronized void sendUdpData(String sendStr, InetAddress sendTo)
    {
        try
        {
            sendBuffer = sendStr.getBytes(P2PConstant.FORMAT);
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sendTo,
                P2PConstant.PORT);
            if (udpSocket != null)
            {
                udpSocket.send(sendPacket);
                Log.d(
                    tag,
                    "send upd data = " + sendStr + "; sendto = "
                        + sendTo.getHostAddress());
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void init()
    {
        mLocalIPs = getLocalAllIP();
        try
        {
            udpSocket = new DatagramSocket(null);
            udpSocket.setReuseAddress(true);
            udpSocket.bind(new InetSocketAddress(P2PConstant.PORT));
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            if (udpSocket != null)
            {
                udpSocket.close();
                isStopped = true;
                return;
            }
        }
        receivePacket = new DatagramPacket(resBuffer, P2PConstant.BUFFER_LENGTH);
        isStopped = false;
    }

    private SigMessage getSelfMsg(int cmd)
    {
        SigMessage msg = new SigMessage();
        msg.commandNum = cmd;
        P2PNeighbor melonInfo = p2PManager.getSelfMeMelonInfo();
        if (melonInfo != null)
        {
            msg.senderAlias = melonInfo.alias;
            msg.senderIp = melonInfo.ip;
        }

        return msg;
    }

    @Override
    public void run()
    {
        while (!isStopped)
        {
            try
            {
                udpSocket.receive(receivePacket);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                isStopped = true;
                break;
            }
            if (receivePacket.getLength() == 0)
            {
                continue;
            }
            String strReceive = null;
            try
            {
                strReceive = new String(resBuffer, 0, receivePacket.getLength(),
                    P2PConstant.FORMAT);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                continue;
            }
            String ip = receivePacket.getAddress().getHostAddress();
            if (!TextUtils.isEmpty(ip))
            {
                if (!isLocal(ip)) //自己会收到自己的广播消息，进行过滤
                {
                    if (!isStopped)
                    {
                        Log.d(tag, "sig communicate process received udp message = "
                            + strReceive);
                        ParamIPMsg msg = new ParamIPMsg(strReceive,
                            receivePacket.getAddress(), receivePacket.getPort());
                        p2PHandler.send2Handler(msg.peerMSG.commandNum,
                            P2PConstant.Src.COMMUNICATE, msg.peerMSG.recipient, msg);
                    }
                    else
                    {
                        break;
                    }
                }
            }
            else
            {
                isStopped = true;
                break;
            }
            //
            if (receivePacket != null)
                receivePacket.setLength(P2PConstant.BUFFER_LENGTH);
        }

        release();
    }

    public void quit()
    {
        isStopped = true;
        release();
    }

    private void release()
    {
        Log.d(tag, "sigCommunicate release");
        if (udpSocket != null)
            udpSocket.close();
        if (receivePacket != null)
            receivePacket = null;
    }

    private boolean isLocal(String ip)
    {
        for (int i = 0; i < mLocalIPs.length; i++)
        {
            if (ip.equals(mLocalIPs[i]))
                return true;
        }

        return false;
    }

    private String[] getLocalAllIP()
    {
        ArrayList<String> IPs = new ArrayList<String>();

        try
        {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements())
            {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements())
                {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                        && InetAddressUtils.isIPv4Address(ip.getHostAddress()))
                    {
                        IPs.add(ip.getHostAddress());
                    }
                }

            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

        return (String[]) IPs.toArray(new String[]{});
    }
}
