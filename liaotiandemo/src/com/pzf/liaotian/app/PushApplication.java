package com.pzf.liaotian.app;

import java.util.LinkedHashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pzf.liaotian.MainActivity;
import com.pzf.liaotian.R;
import com.pzf.liaotian.baidupush.client.PushMessageReceiver;
import com.pzf.liaotian.baidupush.server.BaiduPush;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.db.MessageDB;
import com.pzf.liaotian.db.RecentDB;
import com.pzf.liaotian.db.UserDB;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PushApplication extends Application {
    public final static String API_KEY = "fiWrR2Ki8NkR6r5GHdM2lY7j";
    public final static String SECRIT_KEY = "PTtP7I1EfBnRMu0LimV8fRSIso0dZtgA";
    public static final String SP_FILE_NAME = "push_msg_sp";
    // ======头像===
    public static final int[] heads = { R.drawable.h0 };
    public static final int NUM_PAGE = 6;// 总共有多少页
    public static int NUM = 20;// 每页20个表情,还有最后一个删除button
    private static PushApplication mApplication;
    private BaiduPush mBaiduPushServer;
    private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
    private SharePreferenceUtil mSpUtil;
    private UserDB mUserDB;
    private MessageDB mMsgDB;
    private RecentDB mRecentDB;
    // private List<User> mUserList;
    private MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Gson mGson;

    public synchronized static PushApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initFaceMap();
        initData();
    }

    private void initData() {
        mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                SECRIT_KEY, API_KEY);
        // 不转换没有 @Expose 注解的字段
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        mUserDB = new UserDB(this);
        mMsgDB = new MessageDB(this);
        mRecentDB = new RecentDB(this);
        // mUserList = mUserDB.getUser();
        mMediaPlayer = MediaPlayer.create(this, R.raw.office);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    public synchronized UserDB getUserDB() {
        if (mUserDB == null)
            mUserDB = new UserDB(this);
        return mUserDB;
    }

    public synchronized BaiduPush getBaiduPush() {
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                    SECRIT_KEY, API_KEY);
        return mBaiduPushServer;

    }

    public synchronized Gson getGson() {
        if (mGson == null)
            // 不转换没有 @Expose 注解的字段
            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create();
        return mGson;
    }

    public synchronized MessageDB getMessageDB() {
        if (mMsgDB == null)
            mMsgDB = new MessageDB(this);
        return mMsgDB;
    }

    public synchronized RecentDB getRecentDB() {
        if (mRecentDB == null)
            mRecentDB = new RecentDB(this);
        return mRecentDB;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.office);
        return mMediaPlayer;
    }

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }

    public Map<String, Integer> getFaceMap() {
        if (!mFaceMap.isEmpty())
            return mFaceMap;
        return null;
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
    public void showNotification() {
        if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
            return;

        int icon = R.drawable.notify_general;
        CharSequence tickerText = getResources().getString(
                R.string.app_is_run_background);
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.notify_status_bar_latest_event_view);
        contentView.setImageViewResource(R.id.icon,
                heads[mSpUtil.getHeadIcon()]);
        contentView.setTextViewText(R.id.title, mSpUtil.getNick());
        contentView.setTextViewText(R.id.text, tickerText);
        contentView.setLong(R.id.time, "setTime", when);
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        // 下面是4.0notify
        // Bitmap icon = BitmapFactory.decodeResource(getResources(),
        // heads[mSpUtil.getHeadIcon()]);
        // Notification.Builder notificationBuilder = new Notification.Builder(
        // this).setContentTitle(mSpUtil.getNick())
        // .setContentText(tickerText)
        // .setSmallIcon(R.drawable.notify_general)
        // .setWhen(System.currentTimeMillis())
        // .setContentIntent(contentIntent).setLargeIcon(icon);
        // Notification n = notificationBuilder.getNotification();
        // n.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(PushMessageReceiver.NOTIFY_ID,
                mNotification);
    }

    private void initFaceMap() {
        // TODO Auto-generated method stub
        mFaceMap.put("[呲牙]", R.drawable.f000);
        mFaceMap.put("[调皮]", R.drawable.f001);
        mFaceMap.put("[流汗]", R.drawable.f002);
        mFaceMap.put("[偷笑]", R.drawable.f003);
        mFaceMap.put("[再见]", R.drawable.f004);
        mFaceMap.put("[敲打]", R.drawable.f005);
        mFaceMap.put("[擦汗]", R.drawable.f006);
        mFaceMap.put("[猪头]", R.drawable.f007);
        mFaceMap.put("[玫瑰]", R.drawable.f008);
        mFaceMap.put("[流泪]", R.drawable.f009);
        mFaceMap.put("[大哭]", R.drawable.f010);
        mFaceMap.put("[嘘]", R.drawable.f011);
        mFaceMap.put("[酷]", R.drawable.f012);
        mFaceMap.put("[抓狂]", R.drawable.f013);
        mFaceMap.put("[委屈]", R.drawable.f014);
        mFaceMap.put("[便便]", R.drawable.f015);
        mFaceMap.put("[炸弹]", R.drawable.f016);
        mFaceMap.put("[菜刀]", R.drawable.f017);
        mFaceMap.put("[可爱]", R.drawable.f018);
        mFaceMap.put("[色]", R.drawable.f019);
        mFaceMap.put("[害羞]", R.drawable.f020);

        mFaceMap.put("[得意]", R.drawable.f021);
        mFaceMap.put("[吐]", R.drawable.f022);
        mFaceMap.put("[微笑]", R.drawable.f023);
        mFaceMap.put("[发怒]", R.drawable.f024);
        mFaceMap.put("[尴尬]", R.drawable.f025);
        mFaceMap.put("[惊恐]", R.drawable.f026);
        mFaceMap.put("[冷汗]", R.drawable.f027);
        mFaceMap.put("[爱心]", R.drawable.f028);
        mFaceMap.put("[示爱]", R.drawable.f029);
        mFaceMap.put("[白眼]", R.drawable.f030);
        mFaceMap.put("[傲慢]", R.drawable.f031);
        mFaceMap.put("[难过]", R.drawable.f032);
        mFaceMap.put("[惊讶]", R.drawable.f033);
        mFaceMap.put("[疑问]", R.drawable.f034);
        mFaceMap.put("[睡]", R.drawable.f035);
        mFaceMap.put("[亲亲]", R.drawable.f036);
        mFaceMap.put("[憨笑]", R.drawable.f037);
        mFaceMap.put("[爱情]", R.drawable.f038);
        mFaceMap.put("[衰]", R.drawable.f039);
        mFaceMap.put("[撇嘴]", R.drawable.f040);
        mFaceMap.put("[阴险]", R.drawable.f041);

        mFaceMap.put("[奋斗]", R.drawable.f042);
        mFaceMap.put("[发呆]", R.drawable.f043);
        mFaceMap.put("[右哼哼]", R.drawable.f044);
        mFaceMap.put("[拥抱]", R.drawable.f045);
        mFaceMap.put("[坏笑]", R.drawable.f046);
        mFaceMap.put("[飞吻]", R.drawable.f047);
        mFaceMap.put("[鄙视]", R.drawable.f048);
        mFaceMap.put("[晕]", R.drawable.f049);
        mFaceMap.put("[大兵]", R.drawable.f050);
        mFaceMap.put("[可怜]", R.drawable.f051);
        mFaceMap.put("[强]", R.drawable.f052);
        mFaceMap.put("[弱]", R.drawable.f053);
        mFaceMap.put("[握手]", R.drawable.f054);
        mFaceMap.put("[胜利]", R.drawable.f055);
        mFaceMap.put("[抱拳]", R.drawable.f056);
        mFaceMap.put("[凋谢]", R.drawable.f057);
        mFaceMap.put("[饭]", R.drawable.f058);
        mFaceMap.put("[蛋糕]", R.drawable.f059);
        mFaceMap.put("[西瓜]", R.drawable.f060);
        mFaceMap.put("[啤酒]", R.drawable.f061);
        mFaceMap.put("[飘虫]", R.drawable.f062);

        mFaceMap.put("[勾引]", R.drawable.f063);
        mFaceMap.put("[OK]", R.drawable.f064);
        mFaceMap.put("[爱你]", R.drawable.f065);
        mFaceMap.put("[咖啡]", R.drawable.f066);
        mFaceMap.put("[钱]", R.drawable.f067);
        mFaceMap.put("[月亮]", R.drawable.f068);
        mFaceMap.put("[美女]", R.drawable.f069);
        mFaceMap.put("[刀]", R.drawable.f070);
        mFaceMap.put("[发抖]", R.drawable.f071);
        mFaceMap.put("[差劲]", R.drawable.f072);
        mFaceMap.put("[拳头]", R.drawable.f073);
        mFaceMap.put("[心碎]", R.drawable.f074);
        mFaceMap.put("[太阳]", R.drawable.f075);
        mFaceMap.put("[礼物]", R.drawable.f076);
        mFaceMap.put("[足球]", R.drawable.f077);
        mFaceMap.put("[骷髅]", R.drawable.f078);
        mFaceMap.put("[挥手]", R.drawable.f079);
        mFaceMap.put("[闪电]", R.drawable.f080);
        mFaceMap.put("[饥饿]", R.drawable.f081);
        mFaceMap.put("[困]", R.drawable.f082);
        mFaceMap.put("[咒骂]", R.drawable.f083);

        mFaceMap.put("[折磨]", R.drawable.f084);
        mFaceMap.put("[抠鼻]", R.drawable.f085);
        mFaceMap.put("[鼓掌]", R.drawable.f086);
        mFaceMap.put("[糗大了]", R.drawable.f087);
        mFaceMap.put("[左哼哼]", R.drawable.f088);
        mFaceMap.put("[哈欠]", R.drawable.f089);
        mFaceMap.put("[快哭了]", R.drawable.f090);
        mFaceMap.put("[吓]", R.drawable.f091);
        mFaceMap.put("[篮球]", R.drawable.f092);
        mFaceMap.put("[乒乓球]", R.drawable.f093);
        mFaceMap.put("[NO]", R.drawable.f094);
        mFaceMap.put("[跳跳]", R.drawable.f095);
        mFaceMap.put("[怄火]", R.drawable.f096);
        mFaceMap.put("[转圈]", R.drawable.f097);
        mFaceMap.put("[磕头]", R.drawable.f098);
        mFaceMap.put("[回头]", R.drawable.f099);
        mFaceMap.put("[跳绳]", R.drawable.f100);
        mFaceMap.put("[激动]", R.drawable.f101);
        mFaceMap.put("[街舞]", R.drawable.f102);
        mFaceMap.put("[献吻]", R.drawable.f103);
        mFaceMap.put("[左太极]", R.drawable.f104);

        mFaceMap.put("[右太极]", R.drawable.f105);
        mFaceMap.put("[闭嘴]", R.drawable.f106);
    }

}
