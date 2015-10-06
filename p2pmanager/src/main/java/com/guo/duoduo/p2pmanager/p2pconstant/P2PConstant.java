package com.guo.duoduo.p2pmanager.p2pconstant;


/**
 * Created by 郭攀峰 on 2015/9/19.
 */
public class P2PConstant
{

    public static final int BUFFER_LENGTH = 8192;
    public static final int PORT = 10000;

    public static final String FORMAT = "gbk";

    public static final String MSG_SEPARATOR = "\0";

    public static final String MULTI_ADDRESS = "255.255.255.255";

    public static final String FILE_SHARE_SAVE_PATH = "西瓜快传";

    public static final int MAXIMUM_POOL_SIZE = 4;

    public interface TYPE
    {
        public static final int APP = 0;
        public static final int PIC = 1;
    }

    public interface UI_MSG
    {
        public static final int ADD_NEIGHBOR = 1000;
        public static final int REMOVE_NEIGHBOR = 10001;
    }

    public interface CommandNum
    {
        public static final int ON_LINE = 0;
        public static final int OFF_LINE = 1;
        public static final int ON_LINE_ANS = 2;

        public static final int SEND_FILE_REQ = 3;
        public static final int RECEIVE_FILE_ACK = 4;
        public static final int SEND_FILE_START = 5;

        public static final int SEND_TCP_ESTABLISHED = 6;
        public static final int SEND_LINK_ERROR = 7;
        public static final int SEND_PERCENTS = 8;
        public static final int SEND_OVER = 9;

        public static final int RECEIVE_TCP_ESTABLISHED = 10;
        public static final int RECEIVE_PERCENT = 11;
        public static final int RECEIVE_OVER = 12;

        public static final int RECEIVE_ABORT_SELF = 13;
        public static final int SEND_ABORT_SELF = 14;

    }

    public interface Src
    {
        public static final int MANAGER = 90;
        public static final int COMMUNICATE = 91;
        public static final int SEND_TCP_THREAD = 92;
        public static final int RECEIVE_TCP_THREAD = 92;
    }

    public interface Recipient
    {
        public static final int NEIGHBOR = 100;
        public static final int FILE_SEND = 101;
        public static final int FILE_RECEIVE = 102;
    }
}
