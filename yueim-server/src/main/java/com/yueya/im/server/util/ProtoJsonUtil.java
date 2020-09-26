package com.yueya.im.server.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.yueya.im.common.model.ActorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class ProtoJsonUtil {
    public static JsonFormat.Printer printer = JsonFormat.printer().printingEnumsAsInts();
    static Logger logger = LoggerFactory.getLogger(ProtoJsonUtil.class);
    private static final String enc = "utf-8";
    public static String toJson(ActorMsg.ImMessage msg)  {
        try {
            return printer.print(msg);
        } catch (InvalidProtocolBufferException e) {
            logger.error("proto 转json异常",e);
        }
        return null;
    }
    public static byte[] stringByte(String str){
        try {
            return str.getBytes(enc);
        } catch (UnsupportedEncodingException e) {
            logger.error("转换异常",e);
        }
        return null;
    }
}
