package com.yueya.im.node.messages;

public enum MsgType {
    /**
     * <code>NO = 0;</code>
     */
    NO(0),
    /**
     * <code>UNKNOWN_CODE = 9999;</code>
     */
    UNKNOWN_CODE(9999),
    /**
     * <code>LOGIN = 1000;</code>
     */
    LOGIN(1000),
    /**
     * <code>LOGIN_RESP = 3000;</code>
     */
    LOGIN_RESP(3000),
    /**
     * <code>LOGOUT = 1001;</code>
     */
    LOGOUT(1001),
    /**
     * <code>LOGOUT_RESP = 3001;</code>
     */
    LOGOUT_RESP(3001),
    /**
     * <code>POINT_TEXT = 2000;</code>
     */
    POINT_TEXT(2000),
    /**
     * <code>POINT_TEXT_RESP = 4000;</code>
     */
    POINT_TEXT_RESP(4000),
    /**
     * <code>POINT_AUDIO = 2001;</code>
     */
    POINT_AUDIO(2001),
    /**
     * <code>POINT_AUDIO_RESP = 4001;</code>
     */
    POINT_AUDIO_RESP(4001),
    /**
     * <code>POINT_VIDEO = 2002;</code>
     */
    POINT_VIDEO(2002),
    /**
     * <code>POINT_VIDEO_RESP = 4002;</code>
     */
    POINT_VIDEO_RESP(4002),
    /**
     * <code>GROUP_TEXT = 2003;</code>
     */
    GROUP_TEXT(2003),
    /**
     * <code>GROUP_TEXT_RESP = 4003;</code>
     */
    GROUP_TEXT_RESP(4003),
    /**
     * <code>GROUP_AUDIO = 2004;</code>
     */
    GROUP_AUDIO(2004),
    /**
     * <code>GROUP_AUDIO_RESP = 4004;</code>
     */
    GROUP_AUDIO_RESP(4004),
    /**
     * <code>CREATE_GROUP = 2006;</code>
     */
    CREATE_GROUP(2006),
    /**
     * <code>CREATE_GROUP_RESP = 4006;</code>
     */
    CREATE_GROUP_RESP(4006),
    /**
     * <code>EXIT_GROUP = 2018;</code>
     */
    EXIT_GROUP(2018),
    /**
     * <code>EXIT_GROUP_RESP = 4018;</code>
     */
    EXIT_GROUP_RESP(4018),
    /**
     * <code>ADD_FRIEND = 2015;</code>
     */
    ADD_FRIEND(2015),
    /**
     * <code>ADD_FRIEND_RESP = 4015;</code>
     */
    ADD_FRIEND_RESP(4015),
    /**
     * <code>REMOVE_FRIEND = 2016;</code>
     */
    REMOVE_FRIEND(2016),
    /**
     * <code>REMOVE_FRIEND_RESP = 4016;</code>
     */
    REMOVE_FRIEND_RESP(4016),
    /**
     * <code>ALLOW_FRIEND = 2017;</code>
     */
    ALLOW_FRIEND(2017),
    /**
     * <code>ALLOW_FRIEND_RESP = 4017;</code>
     */
    ALLOW_FRIEND_RESP(4017),
    /**
     * <code>DISAGREE_FRIEND = 2007;</code>
     */
    DISAGREE_FRIEND(2007),
    /**
     * <code>DISAGREE_FRIEND_RESP = 4007;</code>
     */
    DISAGREE_FRIEND_RESP(4007),
    /**
     * <code>INVITE_JOIN_GROUP = 2010;</code>
     */
    INVITE_JOIN_GROUP(2010),
    /**
     * <code>INVITE_JOIN_GROUP_RESP = 4010;</code>
     */
    INVITE_JOIN_GROUP_RESP(4010),
    /**
     * <code>DISAGREE_GROUP = 2011;</code>
     */
    DISAGREE_GROUP(2011),
    /**
     * <code>DISAGREE_GROUP_RESP = 4011;</code>
     */
    DISAGREE_GROUP_RESP(4011),
    /**
     * <code>ALLOW_GROUP = 2012;</code>
     */
    ALLOW_GROUP(2012),
    /**
     * <code>ALLOW_GROUP_RESP = 4012;</code>
     */
    ALLOW_GROUP_RESP(4012),
    /**
     * <code>POINT_FILE = 2008;</code>
     */
    POINT_FILE(2008),
    /**
     * <code>POINT_FILE_RESP = 4008;</code>
     */
    POINT_FILE_RESP(4008),
    /**
     * <code>GROUP_FILE = 2009;</code>
     */
    GROUP_FILE(2009),
    /**
     * <code>GROUP_FILE_RESP = 4009;</code>
     */
    GROUP_FILE_RESP(4009),
    /**
     * <code>USER_ONLINE = 2013;</code>
     */
    USER_ONLINE(2013),
    /**
     * <code>USER_ONLINE_RESP = 4013;</code>
     */
    USER_ONLINE_RESP(4013),
    /**
     * <code>USER_OFFLINE = 2014;</code>
     */
    USER_OFFLINE(2014),
    /**
     * <code>USER_OFFLINE_RESP = 4014;</code>
     */
    USER_OFFLINE_RESP(4014),
    /**
     * <code>GROUP_VIDEO = 2005;</code>
     */
    GROUP_VIDEO(2005),
    /**
     * <code>GROUP_VIDEO_RESP = 4005;</code>
     */
    GROUP_VIDEO_RESP(4005),
    /**
     * <code>LOGIN_ERR = 9998;</code>
     */
    LOGIN_ERR(9998),
    UNRECOGNIZED(-1),
    ;
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException(
                    "Can't get the number of an unknown enum value.");
        }
        return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static MsgType valueOf(int value) {
        return forNumber(value);
    }

    public static MsgType forNumber(int value) {
        switch (value) {
            case 0: return NO;
            case 9999: return UNKNOWN_CODE;
            case 1000: return LOGIN;
            case 3000: return LOGIN_RESP;
            case 1001: return LOGOUT;
            case 3001: return LOGOUT_RESP;
            case 2000: return POINT_TEXT;
            case 4000: return POINT_TEXT_RESP;
            case 2001: return POINT_AUDIO;
            case 4001: return POINT_AUDIO_RESP;
            case 2002: return POINT_VIDEO;
            case 4002: return POINT_VIDEO_RESP;
            case 2003: return GROUP_TEXT;
            case 4003: return GROUP_TEXT_RESP;
            case 2004: return GROUP_AUDIO;
            case 4004: return GROUP_AUDIO_RESP;
            case 2006: return CREATE_GROUP;
            case 4006: return CREATE_GROUP_RESP;
            case 2018: return EXIT_GROUP;
            case 4018: return EXIT_GROUP_RESP;
            case 2015: return ADD_FRIEND;
            case 4015: return ADD_FRIEND_RESP;
            case 2016: return REMOVE_FRIEND;
            case 4016: return REMOVE_FRIEND_RESP;
            case 2017: return ALLOW_FRIEND;
            case 4017: return ALLOW_FRIEND_RESP;
            case 2007: return DISAGREE_FRIEND;
            case 4007: return DISAGREE_FRIEND_RESP;
            case 2010: return INVITE_JOIN_GROUP;
            case 4010: return INVITE_JOIN_GROUP_RESP;
            case 2011: return DISAGREE_GROUP;
            case 4011: return DISAGREE_GROUP_RESP;
            case 2012: return ALLOW_GROUP;
            case 4012: return ALLOW_GROUP_RESP;
            case 2008: return POINT_FILE;
            case 4008: return POINT_FILE_RESP;
            case 2009: return GROUP_FILE;
            case 4009: return GROUP_FILE_RESP;
            case 2013: return USER_ONLINE;
            case 4013: return USER_ONLINE_RESP;
            case 2014: return USER_OFFLINE;
            case 4014: return USER_OFFLINE_RESP;
            case 2005: return GROUP_VIDEO;
            case 4005: return GROUP_VIDEO_RESP;
            case 9998: return LOGIN_ERR;
            default: return null;
        }
    }
    private final int value;

    private MsgType(int value) {
        this.value = value;
    }

}