package com.yueya.im.server.base;

import akka.serialization.JSerializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yueya.im.common.model.ActorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyOwnSerializer extends JSerializer {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        try {
            return ActorMsg.ActorCmd.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            logger.error("反序列化异常",e);
        }
        return null;
    }

    @Override
    public int identifier() {
        return 1234567;
    }

    @Override
    public byte[] toBinary(Object o) {
        return ((ActorMsg.ActorCmd)o).toByteArray();
    }

    @Override
    public boolean includeManifest() {
        return false;
    }
}
