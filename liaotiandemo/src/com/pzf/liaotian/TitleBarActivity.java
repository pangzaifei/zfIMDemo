package com.pzf.liaotian;

import android.view.View;

import com.baidu.android.activity.BaseActivity;
import com.pzf.liaotian.view.TitleBar;

/**
 * @desc:公用的title
 * @author: pangzf
 * @date: 2015年1月22日 上午11:05:52
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public abstract class TitleBarActivity extends BaseActivity implements
        TitleBar.OnClickListener {

    private TitleBar mTitleBar;

    private void initTitleBar() {
        mTitleBar = (TitleBar) findViewById(R.id.titleBar);
        mTitleBar.setOnClickListener(this);
    };

    public void setTitleLeft(View view) {
        checkTitleBar();
        mTitleBar.setLeft(view);
    }

    public void setTitleLeft(View view, int gravity) {
        checkTitleBar();
        mTitleBar.setLeft(view, gravity);
    }

    public void setTitleMiddle(View view) {
        checkTitleBar();
        mTitleBar.setMiddle(view);
    }

    public void setTitleRight(View view) {
        checkTitleBar();
        mTitleBar.setRight(view);
    }

    public void setTitleRight(View view, int gravity) {
        checkTitleBar();
        mTitleBar.setRight(view, gravity);
    }

    private void checkTitleBar() {
        if (null == mTitleBar) {
            initTitleBar();
        }
    }

    @Override
    public void onClickLeft() {
        finish();
    }

    @Override
    public void onClickMiddle() {
        // do nothing
    }

    @Override
    public void onClickRight() {
        // do nothing
    }
}
