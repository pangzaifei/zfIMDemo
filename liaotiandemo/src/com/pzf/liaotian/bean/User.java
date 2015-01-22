package com.pzf.liaotian.bean;

import java.io.Serializable;

/**
 * @desc:用户信息
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String UserId;//
    private String channelId;
    private String nick;//
    private int headIcon;//
    private int group;

    public User(String UserId, String channelId, String nick, int headIcon,
            int group) {
        // TODO Auto-generated constructor stub
        this.UserId = UserId;
        this.channelId = channelId;
        this.nick = nick;
        this.headIcon = headIcon;
        this.group = group;
    }

    public User() {

    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "User [UserId=" + UserId + ", channelId=" + channelId
                + ", nick=" + nick + ", headIcon=" + headIcon + ", group="
                + group + "]";
    }

}
