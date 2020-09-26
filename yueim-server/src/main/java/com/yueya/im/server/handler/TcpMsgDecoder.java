package com.yueya.im.server.handler;

import com.yueya.im.server.base.TcpPact;
import com.yueya.im.server.util.ProtoJsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpMsgDecoder extends ByteToMessageDecoder {
    private static final  int MAX_LENGTH = 10*1024*1024;
    private static final  byte VERSION = 1;
    private static final  byte MASK = 1;
    private static final int VERSION_INDEX = 0;
    private static final int MASK_INDEX = 1;
    private static final int CMD_INDEX = 2;
    private static final int LEN_INDEX = 4;
    private static final int HEAD_SIZE = 8;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readIndex = byteBuf.readerIndex();
        if(byteBuf.isReadable(HEAD_SIZE)){
            byte version = byteBuf.getByte(readIndex+VERSION_INDEX);
            if(version != VERSION){
                throw  new RuntimeException("数据协议版本不一致");
            }
            //byte mask = byteBuf.getByte(MASK_INDEX);
            short cmd = byteBuf.getShort(readIndex+CMD_INDEX);
            int length = byteBuf.getInt(readIndex+LEN_INDEX);
            if(length<MAX_LENGTH && (byteBuf.readableBytes()-HEAD_SIZE) >= length){
                byteBuf.skipBytes(HEAD_SIZE);
                byte[] bytes = new byte[length];
                byteBuf.readBytes(bytes);
                TcpPact pact = new TcpPact();
                pact.setMsgType(cmd);
                pact.setContent(bytes);
                list.add(pact);
            }
        }
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);
    }
    public static ByteBuf encode(int msgType,String msg){
        if(msgType > Short.MAX_VALUE){
            throw new RuntimeException("消息类型长度超过 32767");
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(VERSION);
        byteBuf.writeByte(MASK);
        byteBuf.writeShort(msgType);
        byte[] content = ProtoJsonUtil.stringByte(msg);
        byteBuf.writeInt(content.length);
        byteBuf.writeBytes(content);
        //byte[] result = new byte[byteBuf.readableBytes()];
        //byteBuf.readBytes(result);
        //byteBuf.release();
        return byteBuf;
    }
}
