package com.yueya.im.node.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class StringUtil extends StringUtils {
    protected static final String ENCODING="utf-8";
    protected static Logger logger = LoggerFactory.getLogger(StringUtil.class);
    public static String byteToString(byte[] bytes){
        try {
            return new String(bytes,ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("bytetostring err",e);
        }
        return "";
    }

}
