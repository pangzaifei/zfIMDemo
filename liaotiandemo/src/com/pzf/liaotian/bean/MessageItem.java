package com.pzf.liaotian.bean;

import com.pzf.liaotian.bean.album.BaseData;

/**
 * @desc:发送的聊天数据消息
 * @author: pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class MessageItem extends BaseData{
    // Text
    public static final int MESSAGE_TYPE_TEXT = 1;
    // image
    public static final int MESSAGE_TYPE_IMG = 2;
    // file
    public static final int MESSAGE_TYPE_FILE = 3;
    // Record
    public static final int MESSAGE_TYPE_RECORD = 4;

    private int msgType;// 消息类型
    private String name;// 消息来自
    private long time;// 消息日期
    private String message;// 消息内容
    private int headImg;// 头像
    private boolean isComMeg = true;// 是否为收到的消息

    private int isNew;
    private int voiceTime;

    public MessageItem() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @param msgType
     *            消息类型 MessageItem
     * @param name
     *            用户名
     * @param date
     *            时间
     * @param message
     *            消息
     * @param headImg
     *            头像
     * @param isComMeg
     *            接收的消息 false不是true是
     * @param isNew
     *            是否是新消息
     * @param voiceTime
     *            录音的时间 如果没有为0
     */
    public MessageItem(int msgType, String name, long date, String message,
            int headImg, boolean isComMeg, int isNew, int voiceTime) {
        super();
        this.msgType = msgType;
        this.name = name;
        this.time = date;
        this.message = message;
        this.headImg = headImg;
        this.isComMeg = isComMeg;
        this.isNew = isNew;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return time;
    }

    public void setDate(long date) {
        this.time = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHeadImg() {
        return headImg;
    }

    public void setHeadImg(int headImg) {
        this.headImg = headImg;
    }

    public boolean isComMeg() {
        return isComMeg;
    }

    public void setComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }

    public static int getMessageTypeText() {
        return MESSAGE_TYPE_TEXT;
    }

    public static int getMessageTypeImg() {
        return MESSAGE_TYPE_IMG;
    }

    public static int getMessageTypeFile() {
        return MESSAGE_TYPE_FILE;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

}
