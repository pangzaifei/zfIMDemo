package com.pzf.liaotian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.pzf.liaotian.adapter.FaceAdapter;
import com.pzf.liaotian.adapter.FacePageAdeapter;
import com.pzf.liaotian.adapter.MessageAdapter;
import com.pzf.liaotian.album.AlbumHelper;
import com.pzf.liaotian.app.PushApplication;
import com.pzf.liaotian.baidupush.client.PushMessageReceiver;
import com.pzf.liaotian.bean.Message;
import com.pzf.liaotian.bean.MessageItem;
import com.pzf.liaotian.bean.RecentItem;
import com.pzf.liaotian.bean.User;
import com.pzf.liaotian.bean.album.ImageBucket;
import com.pzf.liaotian.bean.album.ImageTool;
import com.pzf.liaotian.common.util.HomeWatcher;
import com.pzf.liaotian.common.util.HomeWatcher.OnHomePressedListener;
import com.pzf.liaotian.common.util.L;
import com.pzf.liaotian.common.util.SendMsgAsyncTask;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.common.util.SoundUtil;
import com.pzf.liaotian.common.util.T;
import com.pzf.liaotian.common.util.TimeUtil;
import com.pzf.liaotian.config.ConstantKeys;
import com.pzf.liaotian.db.MessageDB;
import com.pzf.liaotian.db.RecentDB;
import com.pzf.liaotian.db.UserDB;
import com.pzf.liaotian.view.CirclePageIndicator;
import com.pzf.liaotian.view.JazzyViewPager;
import com.pzf.liaotian.view.JazzyViewPager.TransitionEffect;
import com.pzf.liaotian.view.Util;
import com.pzf.liaotian.xlistview.MsgListView;
import com.pzf.liaotian.xlistview.MsgListView.IXListViewListener;

/**
 * 
 * @desc: 聊天界面主Activity
 * @author: pangzf
 * @date: 2014年11月3日 上午11:05:33
 * @blog:http://blog.csdn.net/pangzaifei
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class MainActivity extends Activity implements OnClickListener,
        PushMessageReceiver.EventHandler, OnTouchListener, IXListViewListener,
        OnHomePressedListener {

    public static final int NEW_MESSAGE = 0x001;// 收到消息
    public static int MSGPAGERNUM;
    private static final int POLL_INTERVAL = 300;
    private static final long DELAY_VOICE = 1000;// 语音录制计时
    private static final int CAMERA_WITH_DATA = 10;

    private SharePreferenceUtil mSpUtil;
    public static String DEFAULT_ID = "1100877319654414526";
    public static String defaulgUserName = "在飞";
    public static String defaulgIcon = "4";
    public static int defaultCount = 0;

    private ImageButton mFaceBtn;
    private boolean isFaceShow = false;
    private InputMethodManager mInputMethodManager;
    private EditText mEtMsg;

    private PushApplication mApplication;

    private LinearLayout mllFace;// 表情显示的布局
    private JazzyViewPager mFaceViewPager;// 表情viewpager
    private int mCurrentPage = 0;// 表情页数
    private List<String> mKeyList;// 表情list

    private Button mBtnSend;// 发送消息按钮
    private static MessageAdapter adapter;// 发送消息展示的adapter
    private MsgListView mMsgListView;// 展示消息的
    private MessageDB mMsgDB;// 保存消息的数据库
    private RecentDB mRecentDB;
    private Gson mGson;
    private WindowManager.LayoutParams mParams;

    private HomeWatcher mHomeWatcher;// home键

    // 接受数据
    private UserDB mUserDB;
    private SendMsgAsyncTask mSendTask;
    private TextView mTvVoiceBtn;// 语音按钮
    private ImageButton mIbMsgBtn;// 文字按钮
    private View mViewVoice;// 语音界面
    private View mViewInput;
    private SoundUtil mSoundUtil;
    private ImageButton mIbVoiceBtn;

    private ImageView mIvDelete;// 语音弹出框的差号按钮
    private LinearLayout mLLDelete;
    private ImageView mIvBigDeleteIcon;
    private View mChatPopWindow;
    private LinearLayout mLlVoiceLoading;// 加载录制loading
    private LinearLayout mLlVoiceRcding;
    private LinearLayout mLlVoiceShort;// 录制时间过短
    private Handler mHandler = new Handler();
    private int flag = 1;
    private boolean isShosrt = false;

    private long mStartRecorderTime;
    private long mEndRecorderTime;

    private ImageView volume;
    private String mRecordTime;
    private TextView mTvVoiceRecorderTime;// 录制的时间
    private int mRcdStartTime = 0;// 录制的开始时间
    private int mRcdVoiceDelayTime = 1000;
    private int mRcdVoiceStartDelayTime = 300;
    private boolean isCancelVoice;// 不显示语音

    /**
     * 表情viewPager切换效果
     */
    private TransitionEffect mEffects[] = { TransitionEffect.Standard,
            TransitionEffect.Tablet, TransitionEffect.CubeIn,
            TransitionEffect.CubeOut, TransitionEffect.FlipVertical,
            TransitionEffect.FlipHorizontal, TransitionEffect.Stack,
            TransitionEffect.ZoomIn, TransitionEffect.ZoomOut,
            TransitionEffect.RotateUp, TransitionEffect.RotateDown,
            TransitionEffect.Accordion, };// 表情翻页效果

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stopRecord();
        }
    };

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSoundUtil.getAmplitude();
            Log.e("fff", "音量:" + amp);
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    /**
     * 录制语音计时器
     * 
     * @desc:
     * @author: pangzf
     * @date: 2014年11月10日 下午3:46:46
     */
    private class VoiceRcdTimeTask implements Runnable {
        int time = 0;

        public VoiceRcdTimeTask(int startTime) {
            time = startTime;
        }

        @Override
        public void run() {
            time++;

            updateTimes(time);
        }
    }

    /**
     * 接收到数据，用来更新listView
     */
    private Handler handler = new Handler() {
        // 接收到消息
        public void handleMessage(android.os.Message msg) {
            if (msg.what == NEW_MESSAGE) {
                // String message = (String) msg.obj;
                com.pzf.liaotian.bean.Message msgItem = (com.pzf.liaotian.bean.Message) msg.obj;
                String userId = msgItem.getUser_id();
                if (!userId.equals(mSpUtil.getUserId()))// 如果不是当前正在聊天对象的消息，不处理
                    return;

                int headId = msgItem.getHead_id();
                /*
                 * try { headId = Integer
                 * .parseInt(JsonUtil.getFromUserHead(message)); } catch
                 * (Exception e) { L.e("head is not integer  " + e); }
                 */
                // ===接收的额数据，如果是record语音的话，用播放展示
                MessageItem item = null;
                RecentItem recentItem = null;
                if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_TEXT) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_TEXT,
                            userId, headId, msgItem.getNick(),
                            msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());

                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_RECORD) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_RECORD,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(
                            MessageItem.MESSAGE_TYPE_RECORD, userId, headId,
                            msgItem.getNick(), msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());
                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_IMG) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_IMG,
                            userId, headId, msgItem.getNick(),
                            msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());
                }

                adapter.upDateMsg(item);// 更新界面
                mMsgDB.saveMsg(msgItem.getUser_id(), item);// 保存数据库
                mRecentDB.saveRecent(recentItem);

                scrollToBottomListItem();

            }
        }

    };

    /**
     * @Description 滑动到列表底部
     */
    private void scrollToBottomListItem() {

        // todo eric, why use the last one index + 2 can real scroll to the
        // bottom?
        if (mMsgListView != null) {
            mMsgListView.setSelection(adapter.getCount() + 1);
        }
    }

    private VoiceRcdTimeTask mVoiceRcdTimeTask;
    private ScheduledExecutorService mExecutor;// 录制计时器
    private Button mBtnAffix;
    private LinearLayout mLlAffix;
    private TextView mTvTakPicture;// 拍照
    private String mTakePhotoFilePath;
    private TextView mIvAffixAlbum;// 相册
    private AlbumHelper albumHelper = null;// 相册管理类
    private static List<ImageBucket> albumList = null;// 相册数据list
    private TextView mTvChatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zf_chat_main);

        mParams = getWindow().getAttributes();

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mSpUtil = PushApplication.getInstance().getSpUtil();
        Set<String> keySet = PushApplication.getInstance().getFaceMap()
                .keySet();
        mKeyList = new ArrayList<String>();
        mKeyList.addAll(keySet);

        MSGPAGERNUM = 0;
        mSoundUtil = SoundUtil.getInstance();

        initView();

        initFacePage();

        mApplication.getNotificationManager().cancel(
                PushMessageReceiver.NOTIFY_ID);
        PushMessageReceiver.mNewNum = 0;

        mUserDB = mApplication.getUserDB();

        // 启动百度推送服务
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY, PushApplication.API_KEY);// 无baidu帐号登录,以apiKey随机获取一个id

        // 设置表情翻页效果
        // mSpUtil.setFaceEffect(8);

        initUserInfo();

    }

    /**
     * 更新文本内容
     * 
     * @param time
     */
    public void updateTimes(final int time) {
        Log.e("fff", "时间:" + time);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTvVoiceRecorderTime.setText(TimeUtil
                        .getVoiceRecorderTime(time));
            }
        });

    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {

    }

    /**
     * @Description 初始化相册数据
     */
    private void initAlbumData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                albumHelper = AlbumHelper.getHelper(MainActivity.this);
                albumList = albumHelper.getImagesBucketList(false);
            }
        }).start();
    }

    private void initView() {
        initAlbumData();
        mTvChatTitle = (TextView) findViewById(R.id.tv_chat_title);
        // 图片附件
        mBtnAffix = (Button) findViewById(R.id.btn_chat_affix);
        mLlAffix = (LinearLayout) findViewById(R.id.ll_chatmain_affix);
        mTvTakPicture = (TextView) findViewById(R.id.tv_chatmain_affix_take_picture);
        mBtnAffix.setOnClickListener(this);
        mTvTakPicture.setOnClickListener(this);
        // 相册
        mIvAffixAlbum = (TextView) findViewById(R.id.tv_chatmain_affix_album);
        mIvAffixAlbum.setOnClickListener(this);

        mFaceBtn = (ImageButton) findViewById(R.id.face_btn);
        mEtMsg = (EditText) findViewById(R.id.msg_et);
        mFaceBtn.setOnClickListener(this);
        mllFace = (LinearLayout) findViewById(R.id.face_ll);
        mFaceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
        mBtnSend = (Button) findViewById(R.id.send_btn);
        mBtnSend.setClickable(true);
        mBtnSend.setEnabled(true);
        mBtnSend.setOnClickListener(this);

        // 消息
        mApplication = PushApplication.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
        mGson = mApplication.getGson();

        adapter = new MessageAdapter(this, initMsgData());
        mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
        // 触摸ListView隐藏表情和输入法
        mMsgListView.setOnTouchListener(this);
        mMsgListView.setPullLoadEnable(false);
        mMsgListView.setXListViewListener(this);
        mMsgListView.setAdapter(adapter);
        mMsgListView.setSelection(adapter.getCount() - 1);

        // mTitleRightBtn.setOnClickListener(this);
        mEtMsgOnKeyListener();

        // 语音
        initRecorderView();

        mIvDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 如果是删除按键,结束录音，还原按钮，删除文件
                stopRecord();
                File file = new File(mSoundUtil.getFilePath(MainActivity.this,
                        mRecordTime).toString());
                if (file.exists()) {
                    file.delete();
                }
            }
        });
        mTvVoicePreeListener();// 按住录音按钮的事件
    }

    /**
     * 按住录音按钮的事件
     */
    private void mTvVoicePreeListener() {
        // 按住录音添加touch事件
        mTvVoiceBtn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(MainActivity.this, "No SDCard",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                // try {
                // mSoundUtil.stopRecord();
                // } catch (IllegalStateException e) {
                // Toast.makeText(MainActivity.this, "麦克风不可用", 0).show();
                // return false;
                // }

                int[] location = new int[2];
                mTvVoiceBtn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
                int[] del_location = new int[2];
                mLLDelete.getLocationInWindow(del_location);
                int del_Y = del_location[1];
                int del_x = del_location[0];
                if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                    if (!Environment.getExternalStorageDirectory().exists()) {
                        Toast.makeText(MainActivity.this, "No SDCard",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                    // 判断手势按下的位置是否是语音录制按钮的范围内
                    mTvVoiceBtn
                            .setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
                    mChatPopWindow.setVisibility(View.VISIBLE);
                    mLlVoiceLoading.setVisibility(View.VISIBLE);
                    mLlVoiceRcding.setVisibility(View.GONE);
                    mLlVoiceShort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                mLlVoiceLoading.setVisibility(View.GONE);
                                mLlVoiceRcding.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    // img1.setVisibility(View.VISIBLE);
                    mLLDelete.setVisibility(View.GONE);
                    startRecord();
                    flag = 2;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        && flag == 2) {// 松开手势时执行录制完成
                    System.out.println("4");
                    mTvVoiceBtn
                            .setBackgroundResource(R.drawable.voice_rcd_btn_nor);

                    // if (event.getY() >= del_Y
                    // && event.getY() <= del_Y + mLLDelete.getHeight()
                    // && event.getX() >= del_x
                    // && event.getX() <= del_x + mLLDelete.getWidth()) {
                    // mChatPopWindow.setVisibility(View.GONE);
                    // // img1.setVisibility(View.VISIBLE);
                    // mLLDelete.setVisibility(View.GONE);
                    // stopRecord();
                    // flag = 1;
                    // File file = new File(mSoundUtil.getFilePath(
                    // MainActivity.this, mRecordTime).toString());
                    // if (file.exists()) {
                    // file.delete();
                    // }
                    //
                    // } else {
                    mLlVoiceRcding.setVisibility(View.GONE);
                    // stopRecord();
                    try {
                        stopRecord();
                    } catch (IllegalStateException e) {
                        Toast.makeText(MainActivity.this, "麦克风不可用", 0).show();
                        isCancelVoice = true;
                    }
                    mEndRecorderTime = System.currentTimeMillis();
                    flag = 1;
                    int mVoiceTime = (int) ((mEndRecorderTime - mStartRecorderTime) / 1000);
                    if (mVoiceTime < 3) {
                        isShosrt = true;
                        mLlVoiceLoading.setVisibility(View.GONE);
                        mLlVoiceRcding.setVisibility(View.GONE);
                        mLlVoiceShort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mLlVoiceShort.setVisibility(View.GONE);
                                mChatPopWindow.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);

                        File file = new File(mSoundUtil.getFilePath(
                                MainActivity.this, mRecordTime).toString());
                        if (file.exists()) {
                            file.delete();
                        }
                        return false;
                    }
                    // ===发送出去,界面展示
                    if (!isCancelVoice) {
                        showVoice(mVoiceTime);
                    }
                    // }
                }
                return false;

            }
        });
    }

    /**
     * 输入框key监听事件
     */
    private void mEtMsgOnKeyListener() {
        mEtMsg.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                            || isFaceShow) {
                        mllFace.setVisibility(View.GONE);
                        isFaceShow = false;
                        // imm.showSoftInput(msgEt, 0);
                        return true;
                    }
                }
                return false;
            }
        });
        mEtMsg.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnSend.setEnabled(true);
                    mBtnAffix.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                } else {
                    mBtnSend.setEnabled(false);
                    mBtnAffix.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 初始化语音布局
     */
    private void initRecorderView() {
        mIbMsgBtn = (ImageButton) findViewById(R.id.ib_chatmain_msg);
        mViewVoice = findViewById(R.id.ll_chatmain_voice);
        mIbVoiceBtn = (ImageButton) findViewById(R.id.ib_chatmain_voice);
        mViewInput = findViewById(R.id.ll_chatmain_input);
        mTvVoiceBtn = (TextView) findViewById(R.id.tv_chatmain_press_voice);
        mIbMsgBtn.setOnClickListener(this);
        mTvVoiceBtn.setOnClickListener(this);
        mIbVoiceBtn.setOnClickListener(this);

        // include包含的布局语音模块
        mIvDelete = (ImageView) this.findViewById(R.id.img1);
        mLLDelete = (LinearLayout) this.findViewById(R.id.del_re);
        mIvBigDeleteIcon = (ImageView) this.findViewById(R.id.sc_img1);
        mChatPopWindow = this.findViewById(R.id.rcChat_popup);
        mLlVoiceRcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding);
        mLlVoiceLoading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading);
        mLlVoiceShort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort);
        volume = (ImageView) this.findViewById(R.id.volume);
        mTvVoiceRecorderTime = (TextView) this
                .findViewById(R.id.tv_voice_rcd_time);
    }

    /**
     * 是否是删除按钮，暂无用
     * 
     * @param deleteImage
     * @param event
     * @return
     */
    protected boolean isDelete(ImageView deleteImage, MotionEvent event) {
        int[] location = new int[2];
        deleteImage.getLocationInWindow(location);
        int width = deleteImage.getWidth();
        int height = deleteImage.getHeight();
        float upY = event.getY();
        float upX = event.getX();
        int imageY = location[1];
        int imageX = location[0];
        if (upY >= imageY && upY <= height + imageY && upX >= imageX
                && upX <= imageX + width) {
            Log.e("fff", "删除");

        }

        return false;
    }

    /**
     * 语音界面展示
     * 
     * @param mVoiceTime
     */
    protected void showVoice(int mVoiceTime) {
        if (mRecordTime == null || "".equals(mRecordTime)) {
            return;
        }
        MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_RECORD,
                mSpUtil.getNick(), System.currentTimeMillis(), mRecordTime,
                mSpUtil.getHeadIcon(), false, 0, mVoiceTime);
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mSpUtil.getUserId(), item);// 消息保存数据库
        // ===发送消息到服务器
        com.pzf.liaotian.bean.Message msgItem = new com.pzf.liaotian.bean.Message(
                MessageItem.MESSAGE_TYPE_RECORD, System.currentTimeMillis(),
                item.getMessage(), "", item.getVoiceTime());
        if ("".equals(mSpUtil.getUserId())) {
            Log.e("fff", "用户id为空3");
            return;
        }
        new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId()).send();// push发送消息到服务器
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_RECORD,
                mSpUtil.getUserId(), defaultCount, defaulgUserName, mSoundUtil
                        .getFilePath(MainActivity.this, item.getMessage())
                        .toString(), 0, System.currentTimeMillis(),
                item.getVoiceTime());
        mRecentDB.saveRecent(recentItem);
    }

    // private String getFileName() {
    // return mSpUtil.getUserId() + "_" + System.currentTimeMillis() + "_send"
    // + "sound.amr";
    // }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
        PushMessageReceiver.ehList.add(this);// 监听推送的消息

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);
        mllFace.setVisibility(View.GONE);
        isFaceShow = false;
        super.onPause();
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
        PushMessageReceiver.ehList.remove(this);// 移除监听
    }

    public static MessageAdapter getMessageAdapter() {
        return adapter;
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<MessageItem> initMsgData() {
        List<MessageItem> list = mMsgDB
                .getMsg(mSpUtil.getUserId(), MSGPAGERNUM);
        List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
        if (list.size() > 0) {
            for (MessageItem entity : list) {
                if (entity.getName().equals("")) {
                    entity.setName(defaulgUserName);
                }
                if (entity.getHeadImg() < 0) {
                    entity.setHeadImg(defaultCount);
                }
                msgList.add(entity);
            }
        }
        return msgList;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.face_btn: {
                if (!isFaceShow) {
                    mInputMethodManager.hideSoftInputFromWindow(
                            mEtMsg.getWindowToken(), 0);
                    try {
                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mllFace.setVisibility(View.VISIBLE);
                    isFaceShow = true;
                } else {
                    mllFace.setVisibility(View.GONE);
                    isFaceShow = false;
                }
                break;
            }

            case R.id.send_btn: {
                // 发送消息
                String msg = mEtMsg.getText().toString();
                MessageItem item = new MessageItem(
                        MessageItem.MESSAGE_TYPE_TEXT, mSpUtil.getNick(),
                        System.currentTimeMillis(), msg, mSpUtil.getHeadIcon(),
                        false, 0, 0);
                adapter.upDateMsg(item);
                mMsgListView.setSelection(adapter.getCount() - 1);
                mMsgDB.saveMsg(mSpUtil.getUserId(), item);// 消息保存数据库
                mEtMsg.setText("");
                // ===发送消息到服务器
                com.pzf.liaotian.bean.Message msgItem = new com.pzf.liaotian.bean.Message(
                        MessageItem.MESSAGE_TYPE_TEXT,
                        System.currentTimeMillis(), msg, "", 0);
                if ("".equals(mSpUtil.getUserId())) {
                    Log.e("fff", "用户id为空");
                    return;
                }
                new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId())
                        .send();// push发送消息到服务器
                // ===保存近期的消息

                RecentItem recentItem = new RecentItem(
                        MessageItem.MESSAGE_TYPE_TEXT, mSpUtil.getUserId(),
                        defaultCount, defaulgUserName, msg, 0,
                        System.currentTimeMillis(), 0);
                mRecentDB.saveRecent(recentItem);
                break;
            }

            case R.id.ib_chatmain_msg: {
                // 切换文字按钮
                if (!mViewVoice.isShown()) {
                    mViewVoice.setVisibility(View.VISIBLE);
                    mViewInput.setVisibility(View.GONE);
                } else {
                    mViewVoice.setVisibility(View.GONE);
                    mViewInput.setVisibility(View.VISIBLE);
                }

                break;
            }

            case R.id.ib_chatmain_voice: {
                // 切换语音按钮
                if (!mViewVoice.isShown()) {
                    mViewVoice.setVisibility(View.VISIBLE);
                    mViewInput.setVisibility(View.GONE);
                } else {
                    mViewVoice.setVisibility(View.GONE);
                    mViewInput.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.tv_chatmain_press_voice: {
                // 按住说话
                // 弹出音量框

                break;
            }
            case R.id.btn_chat_affix: {
                // 图片附件
                if (mLlAffix.isShown()) {
                    mLlAffix.setVisibility(View.GONE);
                } else {
                    mLlAffix.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.tv_chatmain_affix_take_picture: {
                // 拍照
                // Intent takeIntent = new
                // Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // mTakePhotoFilePath = AlbumHelper.getHelper(MainActivity.this)
                // .getFileDiskCache()
                // + File.separator
                // + System.currentTimeMillis() + ".jpg";
                // File file = new File(mTakePhotoFilePath);
                // takeIntent
                // .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                // startActivityForResult(takeIntent, CAMERA_WITH_DATA);
                //
                // Log.e("fff", "图片地址:" + mTakePhotoFilePath);
                // Log.e("fff", "uri地址:" + Uri.fromFile(file).toString());

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mTakePhotoFilePath = AlbumHelper.getHelper(MainActivity.this)
                        .getFileDiskCache()
                        + File.separator
                        + System.currentTimeMillis() + ".jpg";
                // mTakePhotoFilePath = getImageSavePath(String.valueOf(System
                // .currentTimeMillis()) + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(mTakePhotoFilePath)));
                startActivityForResult(intent, CAMERA_WITH_DATA);
                mLlAffix.setVisibility(View.GONE);
                break;
            }
            case R.id.tv_chatmain_affix_album: {
                // 相册
                if (albumList.size() < 1) {
                    Toast.makeText(MainActivity.this, "相册中没有图片",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this,
                        PickPhotoActivity.class);
                intent.putExtra(ConstantKeys.EXTRA_CHAT_USER_ID,
                        mSpUtil.getUserId());
                startActivityForResult(intent, ConstantKeys.ALBUM_BACK_DATA);
                MainActivity.this.overridePendingTransition(
                        R.anim.zf_album_enter, R.anim.zf_stay);
                mLlAffix.setVisibility(View.GONE);

                scrollToBottomListItem();
                break;
            }

        }
    }

    public static String getImageSavePath(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "PngZaiFei-IM"
                + File.separator
                + "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath() + File.separator + fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("fff", "结果:" + resultCode);
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                hanlderTakePhotoData(data);
                break;

            default:
                break;
        }

    }

    /**
     * 处理拍完照的data数据
     * 
     * @param data
     */
    private void hanlderTakePhotoData(Intent data) {
        // Bitmap bitmap = null;
        // if (data == null) {
        // bitmap = ImageTool.createImageThumbnail(mTakePhotoFilePath);
        // } else {
        // Bundle extras = data.getExtras();
        // bitmap = extras == null ? null : (Bitmap) extras.get("data");
        // }
        // if (bitmap == null) {
        // return;
        // }

        if (data == null) {
            // 新建bitmap
            Bitmap newBitmap = ImageTool
                    .createImageThumbnail(mTakePhotoFilePath);
        } else {
            // 生成bitmap
            Bundle extras = data.getExtras();
            Bitmap bitmap = extras == null ? null : (Bitmap) extras.get("data");
            if (bitmap == null) {
                return;
            }
        }

        // listview展示
        MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getNick(), System.currentTimeMillis(),
                mTakePhotoFilePath, mSpUtil.getHeadIcon(), false, 0, 0);
        adapter.upDateMsg(item);

        // 保存到数据库中
        MessageItem messageItem = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getNick(), System.currentTimeMillis(),
                mTakePhotoFilePath, mSpUtil.getHeadIcon(), false, 0, 0);
        mMsgDB.saveMsg(mSpUtil.getUserId(), messageItem);

        // 保存到最近数据库中
        RecentItem recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getUserId(), mSpUtil.getHeadIcon(), mSpUtil.getNick(),
                mTakePhotoFilePath, 0, System.currentTimeMillis(), 0);
        mRecentDB.saveRecent(recentItem);
        // 发送push
        Message message = new Message(MessageItem.MESSAGE_TYPE_IMG,
                System.currentTimeMillis(), messageItem.getMessage(), "", 0);
        if ("".equals(mSpUtil.getUserId())) {
            Log.e("fff", "用户id为空4");
            return;
        }
        new SendMsgAsyncTask(mGson.toJson(message), mSpUtil.getUserId()).send();

    }

    /**
     * 结束录音
     */
    private void stopRecord() throws IllegalStateException {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);

        volume.setImageResource(R.drawable.amp1);
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        if (mSoundUtil != null) {
            mSoundUtil.stopRecord();
        }
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        // ===录音格式：用户id_时间戳_send_sound
        // SoundUtil.getInstance().startRecord(MainActivity.this,
        // id_time_send_sound);
        mStartRecorderTime = System.currentTimeMillis();
        if (mSoundUtil != null) {
            mRecordTime = mSoundUtil.getRecordFileName();
            mSoundUtil.startRecord(MainActivity.this, mRecordTime);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

            mVoiceRcdTimeTask = new VoiceRcdTimeTask(mRcdStartTime);

            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadScheduledExecutor();
                mExecutor.scheduleAtFixedRate(mVoiceRcdTimeTask,
                        mRcdVoiceStartDelayTime, mRcdVoiceDelayTime,
                        TimeUnit.MILLISECONDS);
            }

        }

    }

    /**
     * 表情viwepager
     */
    private void initFacePage() {
        List<View> lv = new ArrayList<View>();
        for (int i = 0; i < PushApplication.NUM_PAGE; ++i) {
            lv.add(getGridView(i));
        }
        FacePageAdeapter adapter = new FacePageAdeapter(lv, mFaceViewPager);
        mFaceViewPager.setAdapter(adapter);
        mFaceViewPager.setCurrentItem(mCurrentPage);
        mFaceViewPager.setTransitionEffect(mEffects[mSpUtil.getFaceEffect()]);// 效果
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);// 圆点
        indicator.setViewPager(mFaceViewPager);
        adapter.notifyDataSetChanged();
        mllFace.setVisibility(View.GONE);
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPage = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // do nothing
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // do nothing
            }
        });

    }

    /**
     * 获取表情GridView
     * 
     * @param i
     * @return
     */
    private GridView getGridView(int i) {
        GridView gv = new GridView(this);
        gv.setNumColumns(7);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        gv.setBackgroundColor(Color.TRANSPARENT);
        gv.setCacheColorHint(Color.TRANSPARENT);
        gv.setHorizontalSpacing(1);
        gv.setVerticalSpacing(1);
        gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        gv.setGravity(Gravity.CENTER);
        gv.setAdapter(new FaceAdapter(this, i));
        gv.setOnTouchListener(forbidenScroll());
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                    int position, long arg3) {
                if (position == PushApplication.NUM) {// 删除键的位置
                    int selection = mEtMsg.getSelectionStart();
                    String text = mEtMsg.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            mEtMsg.getText().delete(start, end);
                            return;
                        }
                        mEtMsg.getText().delete(selection - 1, selection);
                    }
                } else {// 选择表情==
                    int count = mCurrentPage * PushApplication.NUM + position;
                    defaultCount = count;
                    // 注释的部分，在EditText中显示字符串
                    // String ori = msgEt.getText().toString();
                    // int index = msgEt.getSelectionStart();
                    // StringBuilder stringBuilder = new StringBuilder(ori);
                    // stringBuilder.insert(index, keys.get(count));
                    // msgEt.setText(stringBuilder.toString());
                    // msgEt.setSelection(index + keys.get(count).length());

                    // 下面这部分，在EditText中显示表情
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            getResources(), (Integer) PushApplication
                                    .getInstance().getFaceMap().values()
                                    .toArray()[count]);
                    if (bitmap != null) {
                        int rawHeigh = bitmap.getHeight();
                        int rawWidth = bitmap.getHeight();
                        // 设置表情的大小===
                        int newHeight = Util.dip2px(MainActivity.this, 30);
                        int newWidth = Util.dip2px(MainActivity.this, 30);
                        // 计算缩放因子
                        float heightScale = ((float) newHeight) / rawHeigh;
                        float widthScale = ((float) newWidth) / rawWidth;
                        // 新建立矩阵
                        Matrix matrix = new Matrix();
                        matrix.postScale(heightScale, widthScale);
                        // 设置图片的旋转角度
                        // matrix.postRotate(-30);
                        // 设置图片的倾斜
                        // matrix.postSkew(0.1f, 0.1f);
                        // 将图片大小压缩
                        // 压缩后图片的宽和高以及kB大小均会变化
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                rawWidth, rawHeigh, matrix, true);
                        ImageSpan imageSpan = new ImageSpan(MainActivity.this,
                                newBitmap);
                        String emojiStr = mKeyList.get(count);
                        SpannableString spannableString = new SpannableString(
                                emojiStr);
                        spannableString.setSpan(imageSpan,
                                emojiStr.indexOf('['),
                                emojiStr.indexOf(']') + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEtMsg.append(spannableString);
                    } else {
                        String ori = mEtMsg.getText().toString();
                        int index = mEtMsg.getSelectionStart();
                        StringBuilder stringBuilder = new StringBuilder(ori);
                        stringBuilder.insert(index, mKeyList.get(count));
                        mEtMsg.setText(stringBuilder.toString());
                        mEtMsg.setSelection(index
                                + mKeyList.get(count).length());
                    }
                }
            }
        });
        return gv;
    }

    // 防止乱pageview乱滚动
    private OnTouchListener forbidenScroll() {
        return new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 变换语音量的图片
     * 
     * @param signalEMA
     */
    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    @Override
    public void onMessage(Message message) {
        // 接收到消息更新界面
        android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);

    }

    @Override
    public void onBind(String method, int errorCode, String content) {
        if (errorCode == 0) {// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
            User u = new User(mSpUtil.getUserId(), mSpUtil.getChannelId(),
                    mSpUtil.getNick(), mSpUtil.getHeadIcon(), 0);
            mUserDB.addUser(u);// 把自己添加到数据库
            // com.way.bean.Message msgItem = new com.way.bean.Message(
            // System.currentTimeMillis(), " ", mSpUtil.getTag());
            // new SendMsgAsyncTask(mGson.toJson(msgItem), "").send();;
        }

    }

    @Override
    public void onNotify(String title, String content) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected)
            T.showShort(this, "网络连接已断开");

    }

    @Override
    public void onNewFriend(User u) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.msg_listView:
                mInputMethodManager.hideSoftInputFromWindow(
                        mEtMsg.getWindowToken(), 0);
                mllFace.setVisibility(View.GONE);
                isFaceShow = false;
                break;
            case R.id.msg_et:
                mInputMethodManager.showSoftInput(mEtMsg, 0);
                mllFace.setVisibility(View.GONE);
                isFaceShow = false;
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        MSGPAGERNUM++;
        List<MessageItem> msgList = initMsgData();
        int position = adapter.getCount();
        adapter.setmMsgList(msgList);
        mMsgListView.stopRefresh();
        mMsgListView.setSelection(adapter.getCount() - position - 1);
        L.i("MsgPagerNum = " + MSGPAGERNUM + ", adapter.getCount() = "
                + adapter.getCount());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onHomePressed() {
        mApplication.showNotification();
    }

    @Override
    public void onHomeLongPressed() {

    }

}
