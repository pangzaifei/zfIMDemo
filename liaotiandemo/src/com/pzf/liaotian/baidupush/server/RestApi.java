package com.pzf.liaotian.baidupush.server;

import java.util.TreeMap;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class RestApi extends TreeMap<String, String> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;
    public final static String _METHOD = "method";
    public final static String _APIKEY = "apikey";
    public final static String _TIMESTAMP = "timestamp";
    public final static String _SIGN = "sign";
    public final static String _EXPIRES = "expires";
    public final static String _V = "v";

    public final static String _USER_ID = "user_id";
    public final static String _CHANNEL_ID = "channel_id";

    public final static String _PUSH_TYPE = "push_type";
    public final static String _DEVICE_TYPE = "device_type";

    public final static String _MESSAGE_TYPE = "message_type";
    public final static String _MESSAGES = "messages";
    public final static String _MESSAGE_KEYS = "msg_keys";
    public final static String _MESSAGE_EXPIRES = "message_expires";
    public final static String _MESSAGE_IDS = "msg_ids";

    public final static String _NAME = "name";
    public final static String _START = "start";
    public final static String _LIMIT = "limit";
    public final static String _TAG = "tag";

    public final static String METHOD_QUERY_BIND_LIST = "query_bindlist";
    public final static String METHOD_PUSH_MESSAGE = "push_msg";

    public final static String METHOD_VERIFY_BIND = "verify_bind";

    public final static String METHOD_SET_TAG = "set_tag";
    public final static String METHOD_FETCH_TAG = "fetch_tag";
    public final static String METHOD_DELETE_TAG = "delete_tag";
    public final static String METHOD_QUERY_USER_TAG = "query_user_tags";

    public final static String METHOD_FETCH_MESSAGE = "fetch_msg";
    public final static String METHOD_FETCH_MSG_COUNT = "fetch_msgcount";
    public final static String METHOD_DELETE_MESSAGE = "delete_msg";

    public final static String METHOD_QUERY_DEVICE_TYPE = "query_device_type";

    public final static String PUSH_TYPE_USER = "1";
    public final static String PUSH_TYPE_TAG = "2";
    public final static String PUSH_TYPE_ALL = "3";

    public final static String DEVICE_TYPE_BROWSER = "1";
    public final static String DEVICE_TYPE_PC = "2";
    public final static String DEVICE_TYPE_ANDROID = "3";
    public final static String DEVICE_TYPE_IOS = "4";
    public final static String DEVICE_TYPE_WINDOWS_PHONE = "5";

    public final static String MESSAGE_TYPE_MESSAGE = "0";
    public final static String MESSAGE_TYPE_NOTIFY = "1";

    public static String mApiKey = "fiWrR2Ki8NkR6r5GHdM2lY7j";

    public RestApi(String method) {
        put(_METHOD, method);
        put(_APIKEY, mApiKey);
    }

    @Override
    public String put(String key, String value) {
        if ((value == null) || value.isEmpty())
            return null;
        return super.put(key, value);
    }

}
