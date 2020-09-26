package com.yueya.im.server;

import com.yueya.im.server.handler.TcpMsgDecoder;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.Socket;

public class TestTcp {

    public static void main(String[] args) throws IOException, InterruptedException {
        String loginMsg = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqVspMLFGyMjS1tDCzNDSwMNNRyiwuVrJSSiwtydAtrixW0lHKS0pDUmGoo5RaUQAXMANpySrJBGpJM01NSzM2Sk0yMUoxMTQxTTRKTDVJSjZKS0y0TDGyMAAZlZibClT5bM7GF1P3AfkFRalAy6JjdZRKi1OLPFOAcoamQPGc_PTMPD-I4pLU4hKgUFF-DkRtLQAAAP__.6o4dFHwY2OoxmNuw9FUSug7A9MX1v3F8X7ZbYVLv-S4\",\"device\":\"72dde784-521b-449b-807f-6a81fc34a7a5\"}";
        ByteBuf loginByte = TcpMsgDecoder.encode(1000,loginMsg);
        byte[] bytes = new byte[loginByte.readableBytes()];
        loginByte.readBytes(bytes);
        Socket socket = new Socket("127.0.0.1",8084);
        socket.getOutputStream().write(bytes);
        String msg ="{\"from\":\"15\",\"to\":18,\"groupId\":18,\"nickName\":\"朱蕾\",\"groupName\":\"测试群组1\",\"content\":\"你好%s\",\"date\":1598291676008}";
        for (int i = 0; i < 3; i++) {
            ByteBuf msgBytbuf = TcpMsgDecoder.encode(2003,String.format(msg,i));
            byte[] msgbytes = new byte[msgBytbuf.readableBytes()];
            msgBytbuf.readBytes(msgbytes);
            socket.getOutputStream().write(msgbytes);
            System.out.println(i);
            msgBytbuf.release();
        }
        loginByte.release();
    }
}
