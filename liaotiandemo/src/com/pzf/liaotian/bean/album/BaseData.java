package com.pzf.liaotian.bean.album;

import java.io.Serializable;

import com.baidu.android.itemview.helper.BaseStyle;

/**
 * @desc:网络数据类的基类
 * @author: pangzf
 * @date: 2015年1月22日 下午5:50:30
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public abstract class BaseData extends BaseStyle implements Serializable {

    protected static final int STATUS_OK = 0;
    protected static final int STATUS_ERROR = -1;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private transient int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
