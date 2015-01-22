package com.pzf.liaotian.bean;

import com.pzf.liaotian.R;

/**
 * @desc:接收到的聊天数据消息
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class RecentItem implements Comparable<RecentItem> {
    // Text
    public static final int MESSAGE_TYPE_TEXT = 1;
    // image
    public static final int MESSAGE_TYPE_IMG = 2;
    // file
    public static final int MESSAGE_TYPE_FILE = 3;
    // Record
    public static final int MESSAGE_TYPE_RECORD = 4;

    private String userId;
    private int headImg;// 头像
    private String name;// 消息来自
    private String message;// 消息内容
    private int newNum;// 新消息数目
    private long time;// 消息日期
    private int msgType;// 消息类型
    private int voiceTime;// 语音时长
    // ===头像===
    public static final int[] heads = { R.drawable.h0 };

    public RecentItem() {
    }

    /**
     * 
     * @param messageType
     *            消息类型 文本 语音等
     * @param userId
     *            用户id
     * @param headImg
     *            用户头像
     * @param name
     *            用户名
     * @param message
     *            内容
     * @param newNum
     *            消息数目
     * @param time
     *            时间戳
     * @param voiceTime
     *            语音的时长
     */
    public RecentItem(int messageType, String userId, int headImg, String name,
            String message, int newNum, long time, int voiceTime) {
        super();

        this.userId = userId;
        this.headImg = headImg;
        this.name = name;
        this.message = message;
        this.newNum = newNum;
        this.time = time;
        this.msgType = messageType;
        this.voiceTime = voiceTime;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHeadImg() {
        return headImg;
    }

    public void setHeadImg(int headImg) {
        this.headImg = headImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNewNum() {
        return newNum;
    }

    public void setNewNum(int newNum) {
        this.newNum = newNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static int[] getHeads() {
        return heads;
    }

    @Override
    public int hashCode() {
        int code = 0;
        code = (31 * (this.userId.hashCode())) >> 2;
        return code;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof RecentItem) {
            RecentItem item = (RecentItem) o;
            if (item.userId.equals(this.userId))
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(RecentItem another) {
        // TODO Auto-generated method stub
        return (int) (another.time - this.time);
    }

}
