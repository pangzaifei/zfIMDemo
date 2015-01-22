package com.pzf.liaotian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.utils.Util;
import com.google.gson.Gson;
import com.pzf.liaotian.adapter.ImageGridAdapter;
import com.pzf.liaotian.adapter.MessageAdapter;
import com.pzf.liaotian.app.PushApplication;
import com.pzf.liaotian.bean.Message;
import com.pzf.liaotian.bean.MessageItem;
import com.pzf.liaotian.bean.RecentItem;
import com.pzf.liaotian.bean.album.ImageItem;
import com.pzf.liaotian.common.util.SendMsgAsyncTask;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.config.ConstantKeys;
import com.pzf.liaotian.db.MessageDB;
import com.pzf.liaotian.db.RecentDB;

/**
 * @desc:相册图片列表
 * @author: pangzf
 * @date: 2014年11月12日 下午4:40:54
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class ImageGridActivity extends TitleBarActivity implements
        OnTouchListener {

    private static Context mContext = null;
    private static ImageGridAdapter mAdapter = null;
    private static TextView mFinishTv = null;
    private List<ImageItem> mDataList = null;
    private GridView mGridView = null;
    private TextView mPreviewTv = null;
    private String mName = null;
    private static int mSelectTotal = 0;
    private static Map<Integer, ImageItem> mSelectedMap = new TreeMap<Integer, ImageItem>();
    private SharePreferenceUtil mSpUtil;
    private PushApplication mApplication;
    private MessageDB mMsgDB;// 保存消息的数据库
    private RecentDB mRecentDB;
    private Gson mGson;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    // Util.showToast("最多选择" +
                    // ConstantKeys.MAX_SELECT_IMAGE_COUNT
                    // + "张图片");
                    Toast.makeText(
                            ImageGridActivity.this,
                            "最多选择" + ConstantKeys.MAX_SELECT_IMAGE_COUNT
                                    + "张图片", 0).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.act_takephoto_image_grid);
        mSpUtil = PushApplication.getInstance().getSpUtil();
        // 消息
        mApplication = PushApplication.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
        mGson = mApplication.getGson();
        mContext = this;
        mName = (String) getIntent().getSerializableExtra(
                ConstantKeys.EXTRA_ALBUM_NAME);
        mDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                ConstantKeys.EXTRA_IMAGE_LIST);
        // MessageAdapter mAdapter = new MessageAdapter(this, initMsgData());
        initAdapter2();
        initTitle();
        initView();
        // initAdapter();

    }

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

    private void initAdapter2() {
        mAdapter = new ImageGridAdapter(ImageGridActivity.this);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        for (ImageItem m : mDataList) {
            m.setStyle(ConstantKeys.ALBUM_IMAGE_GRIDVIEW_DISPLAYTYPE_SINGLE);
        }
        mAdapter.setData(mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // mAdapter.notifyDataSetChanged();

                handleItemClick(parent, view, position, id);
            }
        });

        if (mName.length() > 12) {
            mName = mName.substring(0, 11) + "...";
        }

        mFinishTv = (TextView) findViewById(R.id.finish);
        mFinishTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
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
                                MessageItem.MESSAGE_TYPE_IMG,
                                mSpUtil.getNick(), System.currentTimeMillis(),
                                imgItem.getImagePath(), mSpUtil.getHeadIcon(),
                                false, 0, 0);
                        mMsgDB.saveMsg(mSpUtil.getUserId(), messageItem);
                        messageItemList.add(messageItem);
                        // 保存到最近数据库中
                        RecentItem recentItem = new RecentItem(
                                MessageItem.MESSAGE_TYPE_IMG, mSpUtil
                                        .getUserId(), mSpUtil.getHeadIcon(),
                                mSpUtil.getNick(), imgItem.getImagePath(), 0,
                                System.currentTimeMillis(), 0);
                        mRecentDB.saveRecent(recentItem);
                        Message message = new Message(
                                MessageItem.MESSAGE_TYPE_IMG, System
                                        .currentTimeMillis(), messageItem
                                        .getMessage(), "", 0);
                        sendMessageList.add(mGson.toJson(message));
                    }
                }

                // 更新界面

                MessageAdapter messageAdapter = MainActivity
                        .getMessageAdapter();
                if (messageAdapter != null) {
                    messageAdapter.upDateMsgByList(messageItemList);
                }
                if ("".equals(mSpUtil.getUserId())) {
                    Log.e("fff", "用户id为空2");
                    return;
                }
                // 发送push
                new SendMsgAsyncTask(sendMessageList, mSpUtil.getUserId())
                        .send();

                // finish();
                Intent data = new Intent();
                data.putExtra("finish", true);
                setResult(RESULT_OK, data);
                ImageGridActivity.this.finish();
            }
        });
        mPreviewTv = (TextView) findViewById(R.id.preview);
        mPreviewTv.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (mSelectedMap != null && mSelectedMap.size() > 0) {
                    Intent intent = new Intent(ImageGridActivity.this,
                            PreviewActivity.class);
                    startActivityForResult(intent,
                            ConstantKeys.ALBUM_PREVIEW_BACK);

                } else {
                    Toast.makeText(ImageGridActivity.this,
                            getString(R.string.need_choose_images), 0).show();
                    // Util.showToast(R.string.need_choose_images);
                }
            }
        });
    }

    public static Map<Integer, ImageItem> getSelectMap() {
        return mSelectedMap;
    }

    public static void setSelectMap(Map<Integer, ImageItem> map) {
        mSelectedMap = map;
    }

    public static int getSelectTotalNum() {
        return mSelectTotal;
    }

    public static void setSelectTotalNum(int number) {
        mSelectTotal = number;
    }

    /**
     * 处理点击事件
     * 
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    protected void handleItemClick(AdapterView<?> parent, View view,
            int position, long id) {
        ImageItem item = mAdapter.getItem(position);
        ImageView selected = (ImageView) view.findViewById(R.id.isselected);
        if (mSelectTotal < ConstantKeys.MAX_SELECT_IMAGE_COUNT) {
            item.setSelected(!item.isSelected());
            if (item.isSelected()) {
                selected.setImageResource(R.drawable.ic_takephoto_album_img_selected);
                mSelectTotal++;
                setSendText(mSelectTotal);
                mSelectedMap.put(position, mDataList.get(position));

            } else if (!item.isSelected()) {
                selected.setImageResource(R.drawable.ic_takephoto_album_img_select_nor);
                mSelectTotal--;
                setSendText(mSelectTotal);
                mSelectedMap.remove(position);
            }
        } else if (mSelectTotal >= ConstantKeys.MAX_SELECT_IMAGE_COUNT) {
            if (item.isSelected() == true) {
                item.setSelected(!item.isSelected());
                selected.setImageResource(R.drawable.ic_takephoto_album_img_select_nor);
                mSelectTotal--;
                mSelectedMap.remove(position);
            } else {
                android.os.Message message = android.os.Message.obtain(
                        mHandler, 0);
                message.sendToTarget();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }

        if (requestCode == ConstantKeys.ALBUM_PREVIEW_BACK) {
            boolean finishActivity = data.getExtras().getBoolean("finish");
            if (finishActivity) {
                // ImageGridActivity.this.finish();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("finish", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onDestroy() {
        if (mSelectedMap != null) {
            mSelectedMap.clear();
        }
        mSelectTotal = 0;
        super.onDestroy();

    }

    /**
     * 图片数
     * 
     * @param selNum
     */
    public static void setSendText(int selNum) {
        if (selNum == 0) {
            mFinishTv.setText(mContext.getResources().getString(R.string.send));
        } else {
            mFinishTv.setText(mContext.getResources().getString(R.string.send)
                    + "(" + selNum + ")");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }
}
