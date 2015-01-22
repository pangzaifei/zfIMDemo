package com.pzf.liaotian.baidupush.client;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.pzf.liaotian.MainActivity;
import com.pzf.liaotian.R;
import com.pzf.liaotian.app.PushApplication;
import com.pzf.liaotian.bean.Message;
import com.pzf.liaotian.bean.MessageItem;
import com.pzf.liaotian.bean.RecentItem;
import com.pzf.liaotian.bean.User;
import com.pzf.liaotian.common.util.L;
import com.pzf.liaotian.common.util.NetUtil;
import com.pzf.liaotian.common.util.SendMsgAsyncTask;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.common.util.T;

@SuppressLint("NewApi")
/**
 * 接收到信息，进行解析
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class PushMessageReceiver extends BroadcastReceiver {
    public static final String TAG = PushMessageReceiver.class.getSimpleName();
    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
    public static final String RESPONSE = "response";
    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
    private SharePreferenceUtil mSpUtil;

    public static abstract interface EventHandler {
        public abstract void onMessage(Message message);

        public abstract void onBind(String method, int errorCode, String content);

        public abstract void onNotify(String title, String content);

        public abstract void onNetChange(boolean isNetConnected);

        public void onNewFriend(User u);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // L.d(TAG, ">>> Receive intent: \r\n" + intent);
        L.i("listener num = " + ehList.size());
        if (mSpUtil == null) {
            mSpUtil = PushApplication.getInstance().getSpUtil();
        }
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            // 获取消息内容
            String message = intent.getExtras().getString(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            // 消息的用户自定义内容读取方式
            Log.e("fff","onMessage: " + message);
            try {
                Message msgItem = PushApplication.getInstance().getGson()
                        .fromJson(message, Message.class);
                parseMessage(msgItem);// 预处理，过滤一些消息，比如说新人问候或自己发送的
            } catch (Exception e) {
                // TODO: handle exception
            }

        } else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
            // 处理绑定等方法的返回数据
            // PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
            // 获取方法
            final String method = intent
                    .getStringExtra(PushConstants.EXTRA_METHOD);
            // 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
            // 绑定失败的原因有多种，如网络原因，或access token过期。
            // 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
            // 可以通过限制重试次数，或者在其他时机重新调用来解决。
            final int errorCode = intent
                    .getIntExtra(PushConstants.EXTRA_ERROR_CODE,
                            PushConstants.ERROR_SUCCESS);
            // 返回内容
            final String content = new String(
                    intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

            // 用户在此自定义处理消息,以下代码为demo界面展示用
            L.i("onMessage: method : " + method + ", result : " + errorCode
                    + ", content : " + content);
            paraseContent(context, errorCode, content);// 处理消息

            // 回调函数
            for (int i = 0; i < ehList.size(); i++)
                ((EventHandler) ehList.get(i)).onBind(method, errorCode,
                        content);

            // 可选。通知用户点击事件处理
        } else if (intent.getAction().equals(
                PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
            L.d(TAG, "intent=" + intent.toUri(0));
            String title = intent
                    .getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
            String content = intent
                    .getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
            for (int i = 0; i < ehList.size(); i++)
                ((EventHandler) ehList.get(i)).onNotify(title, content);
        } else if (intent.getAction().equals(
                "android.net.conn.CONNECTIVITY_CHANGE")) {
            boolean isNetConnected = NetUtil.isNetConnected(context);
            for (int i = 0; i < ehList.size(); i++)
                ((EventHandler) ehList.get(i)).onNetChange(isNetConnected);
        }
    }

    /**
     * 解析message
     * 
     * @param msg
     */
    private void parseMessage(Message msg) {
        Gson gson = PushApplication.getInstance().getGson();
        // Message msg = gson.fromJson(message, Message.class);
        L.i("gson ====" + msg.toString());
        String tag = msg.getTag();
        String userId = msg.getUser_id();
        int headId = msg.getHead_id();
        // try {
        // headId = Integer.parseInt(JsonUtil.getFromUserHead(message));
        // } catch (Exception e) {
        // L.e("head is not a Integer....");
        // }
        if (!TextUtils.isEmpty(tag)) {// 如果是带有tag的消息
            if (userId.equals(PushApplication.getInstance().getSpUtil()
                    .getUserId()))
                return;
            User u = new User(userId, msg.getChannel_id(), msg.getNick(),
                    headId, 0);
            mSpUtil.setUserId(userId);
            mSpUtil.setNick(msg.getNick());
            mSpUtil.setChannelId(msg.getChannel_id());
            mSpUtil.setHeadIcon(msg.getHead_id());

            PushApplication.getInstance().getUserDB().addUser(u);// 存入或更新好友
            for (EventHandler handler : ehList)
                handler.onNewFriend(u);
            if (!tag.equals(RESPONSE)) {
                // Intent intenService = new
                // Intent(PushApplication.getInstance(),
                // PreParseService.class);
                // intenService.putExtra("message", message);
                // PushApplication.getInstance().startService(intenService);//
                // 启动服务去回消息
                // L.i("启动服务回复消息");
                L.i("response start");
                Message item = new Message(MessageItem.MESSAGE_TYPE_TEXT,
                        System.currentTimeMillis(), "hi",
                        PushMessageReceiver.RESPONSE, 0);
                if ("".equals(mSpUtil.getUserId())) {
                    Log.e("fff", "用户id为空1");
                    return;
                }
                new SendMsgAsyncTask(gson.toJson(item), userId).send();// 同时也回一条消息给对方1
                L.i("response end");
            }
        } else {// 普通消息，
            if (PushApplication.getInstance().getSpUtil().getMsgSound())// 如果用户开启播放声音
                PushApplication.getInstance().getMediaPlayer().start();// 播报声音===
            if (ehList.size() > 0) {// 有监听的时候，传递下去
                for (int i = 0; i < ehList.size(); i++)
                    ((EventHandler) ehList.get(i)).onMessage(msg);
            } else {
                // 通知栏提醒，保存数据库
                // show notify
                showNotify(msg);

                RecentItem recentItem = null;
                MessageItem item = null;
                if (msg.getMessagetype() == MessageItem.MESSAGE_TYPE_TEXT) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
                            msg.getNick(), System.currentTimeMillis(),
                            msg.getMessage(), headId, true, 1,
                            msg.getVoiceTime());
                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_TEXT,
                            userId, headId, msg.getNick(), msg.getMessage(), 0,
                            System.currentTimeMillis(), msg.getVoiceTime());

                } else if (msg.getMessagetype() == MessageItem.MESSAGE_TYPE_RECORD) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_RECORD,
                            msg.getNick(), System.currentTimeMillis(),
                            msg.getMessage(), headId, true, 1,
                            msg.getVoiceTime());
                    recentItem = new RecentItem(
                            MessageItem.MESSAGE_TYPE_RECORD, userId, headId,
                            msg.getNick(), msg.getMessage(), 0,
                            System.currentTimeMillis(), msg.getVoiceTime());
                }else if (msg.getMessagetype() == MessageItem.MESSAGE_TYPE_IMG) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                            msg.getNick(), System.currentTimeMillis(),
                            msg.getMessage(), headId, true, 1,
                            msg.getVoiceTime());
                    recentItem = new RecentItem(
                            MessageItem.MESSAGE_TYPE_IMG, userId, headId,
                            msg.getNick(), msg.getMessage(), 0,
                            System.currentTimeMillis(), msg.getVoiceTime());
                }

                PushApplication.getInstance().getMessageDB()
                        .saveMsg(userId, item);
                PushApplication.getInstance().getRecentDB()
                        .saveRecent(recentItem);
            }
        }
    }

    @SuppressWarnings("deprecation")
    /**
     * 显示notification
     * @param message  显示的内容
     */
    private void showNotify(Message message) {
        // TODO Auto-generated method stub
        mNewNum++;
        // 更新通知栏
        PushApplication application = PushApplication.getInstance();

        int icon = R.drawable.notify_newmessage;
        CharSequence tickerText = message.getNick() + ":"
                + message.getMessage();
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        notification.flags = Notification.FLAG_NO_CLEAR;
        // 设置默认声音
        // notification.defaults |= Notification.DEFAULT_SOUND;
        // 设定震动(需加VIBRATE权限)
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.contentView = null;

        Intent intent = new Intent(application, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
                intent, 0);
        notification.setLatestEventInfo(PushApplication.getInstance(),
                application.getSpUtil().getNick() + " (" + mNewNum + "条新消息)",
                tickerText, contentIntent);
        // 下面是4.0通知栏api
        // Bitmap headBm = BitmapFactory.decodeResource(
        // application.getResources(), PushApplication.heads[Integer
        // .parseInt(JsonUtil.getFromUserHead(message))]);
        // Notification.Builder mNotificationBuilder = new
        // Notification.Builder(application)
        // .setTicker(tickerText)
        // .setContentTitle(JsonUtil.getFromUserNick(message))
        // .setContentText(JsonUtil.getMsgContent(message))
        // .setSmallIcon(R.drawable.notify_newmessage)
        // .setLargeIcon(headBm).setWhen(System.currentTimeMillis())
        // .setContentIntent(contentIntent);
        // Notification n = mNotificationBuilder.getNotification();
        // n.flags |= Notification.FLAG_NO_CLEAR;
        //
        // n.defaults |= Notification.DEFAULT_VIBRATE;

        application.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
    }

    /**
     * 处理登录结果
     * 
     * @param errorCode
     * @param content
     */
    private void paraseContent(final Context context, int errorCode,
            String content) {
        // TODO Auto-generated method stub
        if (errorCode == 0) {
            String appid = "";
            String channelid = "";
            String userid = "";

            try {
                JSONObject jsonContent = new JSONObject(content);
                JSONObject params = jsonContent
                        .getJSONObject("response_params");
                appid = params.getString("appid");
                channelid = params.getString("channel_id");
                userid = params.getString("user_id");
            } catch (JSONException e) {
                L.e(TAG, "Parse bind json infos error: " + e);
            }
            // SharePreferenceUtil util = PushApplication.getInstance()
            // .getSpUtil();
            // User u = new User(userid, channelid, msg.getNick(),
            // headId, 0);
            mSpUtil.setAppId(appid);
            mSpUtil.setChannelId(channelid);
            mSpUtil.setUserId(userid);
        } else {
            if (NetUtil.isNetConnected(context)) {
                if (errorCode == 30607) {
                    T.showLong(context, "账号已过期，请重新登录");
                    // 跳转到重新登录的界面
                } else {
                    T.showLong(context, "启动失败，正在重试...");
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            PushManager.startWork(context,
                                    PushConstants.LOGIN_TYPE_API_KEY,
                                    PushApplication.API_KEY);
                        }
                    }, 2000);// 两秒后重新开始验证
                }
            } else {
                T.showLong(context, R.string.net_error_tip);
            }
        }
    }
}
