package com.pzf.liaotian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pzf.liaotian.adapter.MessageAdapter;
import com.pzf.liaotian.album.DisplayBitmapCache;
import com.pzf.liaotian.app.PushApplication;
import com.pzf.liaotian.bean.Message;
import com.pzf.liaotian.bean.MessageItem;
import com.pzf.liaotian.bean.RecentItem;
import com.pzf.liaotian.bean.album.ImageItem;
import com.pzf.liaotian.common.util.SendMsgAsyncTask;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.db.MessageDB;
import com.pzf.liaotian.db.RecentDB;
import com.pzf.liaotian.view.CustomViewPager;

/**
 * @desc:图片预览
 * @author: pangzf
 * @date: 2014年11月13日 下午5:35:28
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class PreviewActivity extends TitleBarActivity implements
        OnPageChangeListener, OnClickListener {

    private CustomViewPager mViewPager;
    private ImageView[] mTips;
    private ImageView[] mImageViews;
    private ViewGroup mGroup;
    private TextView mSendTv;
    // private final ImageGridAdapter2 mAdapter =
    // ImageGridActivity.getAdapter();
    private Map<Integer, ImageItem> mSelectedMap = ImageGridActivity
            .getSelectMap();
    private int mSelectTotal = ImageGridActivity.getSelectTotalNum();
    private Map<Integer, Integer> mRemovePositionMap = new HashMap<Integer, Integer>();
    private int mCurImagePosition = -1;
    private SharePreferenceUtil mSpUtil;
    private MessageDB mMsgDB;// 保存消息的数据库
    private RecentDB mRecentDB;
    private PushApplication mApplication;
    private Gson mGson;

    // private IMServiceHelper imServiceHelper = new IMServiceHelper();
    private void initTitle() {
        TextView mBack = new TextView(this);
        mBack.setBackgroundResource(R.drawable.ic_back);
        setTitleLeft(mBack);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(R.string.album);
        tvTitle.setTextSize(getResources().getDimension(R.dimen.title_textsize));
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        setTitleMiddle(tvTitle);

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.act_takephoto_preview);
        mSpUtil = PushApplication.getInstance().getSpUtil();
        mApplication = PushApplication.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
        mGson = mApplication.getGson();
        initTitle();
        initView();
        loadView();
    }

    private void initView() {

        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
        mSendTv = (TextView) findViewById(R.id.send_btn);
        setSendText(mSelectedMap.size());
        mSendTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // imServiceHelper.disconnect(this);
    }

    private void setSendText(int selTotal) {
        if (selTotal > 0) {
            mSendTv.setText(getResources().getString(R.string.send) + "("
                    + selTotal + ")");
        } else {
            mSendTv.setText(getResources().getString(R.string.send));
        }
    }

    private void loadView() {
        mImageViews = new ImageView[mSelectedMap.size()];

        if (mSelectedMap.size() > 1) {
            mTips = new ImageView[mSelectedMap.size()];
            for (int i = 0; i < mTips.length; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LayoutParams(10, 10));
                mTips[i] = imageView;
                if (i == 0) {
                    mTips[i].setBackgroundResource(R.drawable.ic_takephoto_default_dot_down);
                } else {
                    mTips[i].setBackgroundResource(R.drawable.ic_takephoto_default_dot_up);
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 5;
                layoutParams.rightMargin = 5;
                mGroup.addView(imageView, layoutParams);
            }
        }

        Iterator<?> it = mSelectedMap.keySet().iterator();
        int index = -1;
        while (it.hasNext()) {
            int key = (Integer) it.next();
            ImageItem item = mSelectedMap.get(key);
            ImageView imageView = new ImageView(this);
            mImageViews[++index] = imageView;
            Bitmap bmp = DisplayBitmapCache.getInstance(PreviewActivity.this)
                    .get(item.getImagePath());
            if (bmp == null)
                bmp = DisplayBitmapCache.getInstance(PreviewActivity.this).get(
                        item.getThumbnailPath());
            if (bmp != null)
                imageView.setImageBitmap(bmp);
            if (index == 0) {
                mCurImagePosition = key;
            }
        }

        // 设置view pager
        mViewPager.setAdapter(new PreviewAdapter());
        mViewPager.setOnPageChangeListener(this);
        if (mSelectedMap.size() == 1) {
            mViewPager.setScanScroll(false);
        } else {
            mViewPager.setScanScroll(true);
        }
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < mTips.length; i++) {
            if (i == selectItems) {
                mTips[i].setBackgroundResource(R.drawable.ic_takephoto_default_dot_down);
            } else {
                mTips[i].setBackgroundResource(R.drawable.ic_takephoto_default_dot_up);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        @SuppressWarnings("rawtypes")
        Iterator it = mSelectedMap.keySet().iterator();
        int index = -1;
        while (it.hasNext()) {
            int key = (Integer) it.next();
            if (++index == position) {
                mCurImagePosition = key;// 对应适配器中图片列表的真实位置
                // if (mSelectedMap.get(key).isSelected()) {
                // mSelectIv
                // .setImageResource(R.drawable.ic_takephoto_album_img_selected);
                // } else {
                // mSelectIv
                // .setImageResource(R.drawable.ic_takephoto_album_img_select_nor);
                // }
            }
        }
        setImageBackground(position);
    }

    public class PreviewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            try {
                ((ViewGroup) container).addView(mImageViews[position]);
            } catch (Exception e) {
            }
            return mImageViews[position];
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn: {
                handleSendBtn();
                break;
            }

            default:
                break;
        }

    }

    /**
     * 处理发送按钮
     */
    private void handleSendBtn() {

        // Iterator<Integer> iterator = mRemovePositionMap.keySet().iterator();
        // while (iterator.hasNext()) {
        // int key = (Integer) iterator.next();
        // if (mSelectedMap.containsKey(key))
        // mSelectedMap.remove(key);
        // }
        // mRemovePositionMap.clear();
        //
        // if (mSelectedMap.size() > 0) {
        // // List<MessageInfo> messageList = new
        // // ArrayList<MessageInfo>();
        // Iterator<Integer> iter = mSelectedMap.keySet().iterator();
        //
        // List<String> sendMessageList = new ArrayList<String>();//
        // 需要发送到服务器数据的集合列表
        // // List<MessageItem> messageItemList = new ArrayList<MessageItem>();
        // while (iter.hasNext()) {
        // int position = iter.next();
        // ImageItem imgItem = mSelectedMap.get(position);
        // if (imgItem != null
        // && !TextUtils.isEmpty(imgItem.getImagePath())) {
        // }
        // }
        // ImageGridActivity.setSendText(0);
        // ImageGridActivity.setSelectMap(null);
        //
        // // if ("".equals(UserInfoUtils.getUid())) {
        // // Log.e("fff", "用户id为空");
        // // return;
        // // }
        // // 发送图片到服务器
        // // new SendMsgAsyncTask(sendMessageList, mSpUtil.getUserId())
        // // .send();
        // Util.showToast("todo--发送图片到服务器");
        //
        // // Intent data = new Intent();
        // // data.putExtra("finish", true);
        // // setResult(RESULT_OK, data);
        // // PreviewActivity.this.finish();
        // // finish();
        // } else {
        // Util.showToast(R.string.need_choose_images);
        // }
        Iterator<Integer> iterator = mRemovePositionMap.keySet().iterator();
        while (iterator.hasNext()) {
            int key = (Integer) iterator.next();
            if (mSelectedMap.containsKey(key))
                mSelectedMap.remove(key);
        }
        mRemovePositionMap.clear();

        if (mSelectedMap.size() > 0) {
            // List<MessageInfo> messageList = new
            // ArrayList<MessageInfo>();
            Iterator<Integer> iter = mSelectedMap.keySet().iterator();

            List<String> sendMessageList = new ArrayList<String>();// 需要发送到服务器数据的集合列表
            List<MessageItem> messageItemList = new ArrayList<MessageItem>();
            while (iter.hasNext()) {
                int position = iter.next();
                ImageItem imgItem = mSelectedMap.get(position);
                if (imgItem != null
                        && !TextUtils.isEmpty(imgItem.getImagePath())) {
                    // 保存到数据库中
                    MessageItem messageItem = new MessageItem(
                            MessageItem.MESSAGE_TYPE_IMG, mSpUtil.getNick(),
                            System.currentTimeMillis(), imgItem.getImagePath(),
                            mSpUtil.getHeadIcon(), false, 0, 0);
                    mMsgDB.saveMsg(mSpUtil.getUserId(), messageItem);
                    messageItemList.add(messageItem);
                    // 保存到最近数据库中
                    RecentItem recentItem = new RecentItem(
                            MessageItem.MESSAGE_TYPE_IMG, mSpUtil.getUserId(),
                            mSpUtil.getHeadIcon(), mSpUtil.getNick(),
                            imgItem.getImagePath(), 0,
                            System.currentTimeMillis(), 0);
                    mRecentDB.saveRecent(recentItem);
                    Message message = new Message(MessageItem.MESSAGE_TYPE_IMG,
                            System.currentTimeMillis(),
                            messageItem.getMessage(), "", 0);
                    sendMessageList.add(mGson.toJson(message));
                }
            }
            ImageGridActivity.setSendText(0);
            // ImageGridActivity.setAdapterSelectedMap(null);
            mSelectedMap.clear();

            // 更新界面

            MessageAdapter messageAdapter = MainActivity.getMessageAdapter();
            if (messageAdapter != null) {
                messageAdapter.upDateMsgByList(messageItemList);
            }
            if ("".equals(mSpUtil.getUserId())) {
                Log.e("fff", "用户id为空5");
                return;
            }
            // 发送push
            new SendMsgAsyncTask(sendMessageList, mSpUtil.getUserId()).send();

            Intent data = new Intent();
            data.putExtra("finish", true);
            setResult(RESULT_OK, data);
            PreviewActivity.this.finish();
        } else {
            Toast.makeText(PreviewActivity.this, R.string.need_choose_images,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 后退按钮
     */
    // private void handleBackBtn() {
    // @SuppressWarnings("rawtypes")
    // Iterator it = mRemovePositionMap.keySet().iterator();
    // while (it.hasNext()) {
    // int key = (Integer) it.next();
    // if (mSelectedMap.containsKey(key))
    // mSelectedMap.remove(key);
    // }
    // ImageGridActivity.setSelectMap(mSelectedMap);
    // mRemovePositionMap.clear();
    // PreviewActivity.this.finish();
    // }

    /**
     * 选择按钮
     */
    // private void handlerSelectBtn() {
    // if (mSelectedMap.containsKey(mCurImagePosition)) {
    // ImageItem item = mSelectedMap.get(mCurImagePosition);
    // item.setSelected(!item.isSelected());
    // if (item.isSelected()) {
    // int selTotal = mSelectTotal;
    // ImageGridActivity.setSelectTotalNum(++selTotal);
    // if (mRemovePositionMap.containsKey(mCurImagePosition)) {
    // mRemovePositionMap.remove(mCurImagePosition);
    // }
    // ImageGridActivity.setSendText(selTotal);
    // setSendText(selTotal);
    // // mSelectIv
    // // .setImageResource(R.drawable.ic_takephoto_album_img_selected);
    // } else {
    // int selTotal = mSelectTotal;
    // ImageGridActivity.setSelectTotalNum(--selTotal);
    // mRemovePositionMap.put(mCurImagePosition, mCurImagePosition);
    // ImageGridActivity.setSendText(selTotal);
    // setSendText(selTotal);
    // // mSelectIv
    // // .setImageResource(R.drawable.ic_takephoto_album_img_select_nor);
    // }
    // }
    // }

}
