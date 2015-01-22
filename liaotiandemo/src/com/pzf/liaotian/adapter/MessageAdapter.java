package com.pzf.liaotian.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pzf.liaotian.R;
import com.pzf.liaotian.album.takephoto.BubbleImageHelper;
import com.pzf.liaotian.album.takephoto.MessageBitmapCache;
import com.pzf.liaotian.app.PushApplication;
import com.pzf.liaotian.bean.MessageItem;
import com.pzf.liaotian.common.util.SharePreferenceUtil;
import com.pzf.liaotian.common.util.SoundUtil;
import com.pzf.liaotian.common.util.TimeUtil;
import com.pzf.liaotian.view.GifTextView;

/**
 * @desc发送消息的adapter
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
@SuppressLint("NewApi")
public class MessageAdapter extends BaseAdapter {

    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    public static final int MESSAGE_TYPE_INVALID = -1;
    public static final int MESSAGE_TYPE_MINE_TETX = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_AUDIO = 0x02;
    public static final int MESSAGE_TYPE_OTHER_TEXT = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x05;
    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
    private static final int VIEW_TYPE_COUNT = 9;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MessageItem> mMsgList;
    private SharePreferenceUtil mSpUtil;

    private long mPreDate;

    private SoundUtil mSoundUtil;

    public MessageAdapter(Context context, List<MessageItem> msgList) {
        this.mContext = context;
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
        mSpUtil = PushApplication.getInstance().getSpUtil();
        mSoundUtil = SoundUtil.getInstance();
    }

    public void removeHeadMsg() {
        if (mMsgList.size() - 10 > 10) {
            for (int i = 0; i < 10; i++) {
                mMsgList.remove(i);
            }
            notifyDataSetChanged();
        }
    }

    public void setmMsgList(List<MessageItem> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    public void upDateMsg(MessageItem msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    public void upDateMsgByList(List<MessageItem> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mMsgList.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // final MessageItem item = mMsgList.get(position);
        // boolean isComMsg = item.isComMeg();
        // ViewHolder holder;
        // if (convertView == null
        // || convertView.getTag(R.drawable.ic_launcher + position) == null) {
        // holder = new ViewHolder();
        // if (isComMsg) {
        // convertView = mInflater.inflate(R.layout.chat_item_left, null);
        // } else {
        // convertView = mInflater.inflate(R.layout.chat_item_right, null);
        // }
        // holder.head = (ImageView) convertView.findViewById(R.id.icon);
        // holder.time = (TextView) convertView.findViewById(R.id.datetime);
        // holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
        // holder.rlMessage = (RelativeLayout) convertView
        // .findViewById(R.id.relativeLayout1);
        // holder.ivphoto = (ImageView) convertView
        // .findViewById(R.id.iv_chart_item_photo);
        // holder.progressBar = (ProgressBar) convertView
        // .findViewById(R.id.progressBar1);
        // holder.voiceTime = (TextView) convertView
        // .findViewById(R.id.tv_voice_time);
        // holder.flPickLayout = (FrameLayout) convertView
        // .findViewById(R.id.message_layout);
        // convertView.setTag(R.drawable.ic_launcher + position);
        // } else {
        // holder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
        // + position);
        // }
        // holder.time.setText(TimeUtil.getChatTime(item.getDate()));
        //
        // // if (Math.abs(mPreDate - item.getDate()) < 60000) {
        // // holder.time.setVisibility(View.GONE);
        // // } else {
        // // mPreDate = item.getDate();
        // // holder.time.setVisibility(View.VISIBLE);
        // // }
        //
        // holder.time.setVisibility(View.VISIBLE);
        //
        // holder.head.setBackgroundResource(PushApplication.heads[item
        // .getHeadImg()]);
        // if (!isComMsg && !mSpUtil.getShowHead()) {
        // holder.head.setVisibility(View.GONE);
        // }
        // showTextOrVoiceOrImage(item, holder);
        // holder.msg.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // if (item.getMsgType() == MessageItem.MESSAGE_TYPE_RECORD) {
        // mSoundUtil.playRecorder(mContext, item.getMessage());
        // }
        // }
        // });
        //
        // holder.progressBar.setVisibility(View.GONE);
        // holder.progressBar.setProgress(50);
        // return convertView;

        // ===============
        int type = getItemViewType(position);
        MessageHolderBase holder = null;
        if (null == convertView && null != mInflater) {
            holder = new MessageHolderBase();
            switch (type) {
                case MESSAGE_TYPE_MINE_TETX: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_IMAGE: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_image_message_item, parent, false);
                    holder = new ImageMessageHolder();
                    convertView.setTag(holder);
                    // fillTextMessageHolder(holder, convertView);
                    fillImageMessageHolder((ImageMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_AUDIO: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_TEXT: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_IMAGE: {
                    convertView = mInflater
                            .inflate(R.layout.zf_chat_other_image_message_item,
                                    parent, false);
                    holder = new ImageMessageHolder();
                    convertView.setTag(holder);
                    fillImageMessageHolder((ImageMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_AUDIO: {
                    convertView = mInflater
                            .inflate(R.layout.zf_chat_other_audio_message_item,
                                    parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                default:
                    break;
            }
        } else {
            holder = (MessageHolderBase) convertView.getTag();
        }

        final MessageItem mItem = mMsgList.get(position);
        if (mItem != null) {
            int msgType = mItem.getMsgType();
            if (msgType == MessageItem.MESSAGE_TYPE_TEXT) {
                handleTextMessage((TextMessageHolder) holder, mItem, parent);

            } else if (msgType == MessageItem.MESSAGE_TYPE_IMG) {
                handleImageMessage((ImageMessageHolder) holder, mItem, parent);

            } else if (msgType == MessageItem.MESSAGE_TYPE_RECORD) {
                handleAudioMessage((AudioMessageHolder) holder, mItem, parent);

            }
        }

        return convertView;
    }

    private void handleTextMessage(final TextMessageHolder holder,
            final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);

        // 文字
        holder.msg.insertGif(convertNormalStringToSpannableString(mItem
                .getMessage() + " "));
       
    }

    /**
     * @Description 处理图片消息
     * @param holder
     * @param info
     * @param position
     * @param isMine
     * @param parent
     */
    private void handleImageMessage(final ImageMessageHolder holder,
            final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);

        // 图片文件
        if (mItem.getMessage() != null) {
            // Bitmap bitmap = BitmapFactory.decodeFile(item.getMessage());
            Bitmap bitmap = MessageBitmapCache.getInstance().get(
                    mItem.getMessage());
            if (!mItem.isComMeg()) {
                bitmap = BubbleImageHelper.getInstance(mContext)
                        .getBubbleImageBitmap(bitmap,
                                R.drawable.zf_mine_image_default_bk);
            } else {
                bitmap = BubbleImageHelper.getInstance(mContext)
                        .getBubbleImageBitmap(bitmap,
                                R.drawable.zf_other_image_default_bk);
            }

            if (bitmap != null) {
                holder.ivphoto.setLayoutParams(new FrameLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                holder.ivphoto.setImageBitmap(bitmap);
            }
            // if (isMine) {
            // isMine = false;
            // showTextOrVoiceOrImage(item, holder);
            // }

            holder.flPickLayout.setVisibility(View.VISIBLE);
        } else {
            holder.flPickLayout.setVisibility(View.GONE);
        }
        holder.rlMessage.setVisibility(View.GONE);
    }

    /**
     * @Description 处理语音消息
     * @param holder
     * @param info
     * @param isMine
     * @param parent
     * @param position
     */
    private void handleAudioMessage(final AudioMessageHolder holder,
            final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        // 语音
        holder.msg.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.chatto_voice_playing, 0);
//        holder.ivphoto.setCompoundDrawablesWithIntrinsicBounds(0, 0,
//              R.drawable.chatto_voice_playing, 0);
        holder.voiceTime.setText(TimeUtil.getVoiceRecorderTime(mItem
                .getVoiceTime()));
        holder.msg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mItem.getMsgType() == MessageItem.MESSAGE_TYPE_RECORD) {
                    // 播放语音
                    mSoundUtil.playRecorder(mContext, mItem.getMessage());
                }
            }
        });
    }

    private void handleBaseMessage(MessageHolderBase holder,
            final MessageItem mItem) {
        holder.time.setText(TimeUtil.getChatTime(mItem.getDate()));
        holder.time.setVisibility(View.VISIBLE);
        holder.head.setBackgroundResource(PushApplication.heads[mItem
                .getHeadImg()]);
        // if (!isComMsg && !mSpUtil.getShowHead()) {
        // holder.head.setVisibility(View.GONE);
        // }
        // showTextOrVoiceOrImage(mItem, holder);
        holder.progressBar.setVisibility(View.GONE);
        holder.progressBar.setProgress(50);

        holder.time.setVisibility(View.VISIBLE);
        //
        // holder.head.setBackgroundResource(PushApplication.heads[mItem
        // .getHeadImg()]);

    }

    private void fillBaseMessageholder(MessageHolderBase holder,
            View convertView) {
        holder.head = (ImageView) convertView.findViewById(R.id.icon);
        holder.time = (TextView) convertView.findViewById(R.id.datetime);
        // holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
        holder.rlMessage = (RelativeLayout) convertView
                .findViewById(R.id.relativeLayout1);
        // holder.ivphoto = (ImageView) convertView
        // .findViewById(R.id.iv_chart_item_photo);
        holder.progressBar = (ProgressBar) convertView
                .findViewById(R.id.progressBar1);
        // holder.voiceTime = (TextView) convertView
        // .findViewById(R.id.tv_voice_time);
        holder.flPickLayout = (FrameLayout) convertView
                .findViewById(R.id.message_layout);
    }

    private void fillTextMessageHolder(TextMessageHolder holder,
            View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
    }

    private void fillImageMessageHolder(ImageMessageHolder holder,
            View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.ivphoto = (ImageView) convertView
                .findViewById(R.id.iv_chart_item_photo);
    }

    private void fillAudioMessageHolder(AudioMessageHolder holder,
            View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.voiceTime = (TextView) convertView
                .findViewById(R.id.tv_voice_time);
        holder.ivphoto = (ImageView) convertView
                .findViewById(R.id.iv_chart_item_photo);
        holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
    }

    private static class MessageHolderBase {
        ImageView head;
        TextView time;
        ImageView imageView;
        ProgressBar progressBar;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    private static class TextMessageHolder extends MessageHolderBase {
        /**
         * 文字消息体
         */
        GifTextView msg;
    }

    private static class ImageMessageHolder extends MessageHolderBase {

        /**
         * 图片消息体
         */
        ImageView ivphoto;

    }

    private static class AudioMessageHolder extends MessageHolderBase {
        ImageView ivphoto;
        /**
         * 语音秒数
         */
        TextView voiceTime;
        GifTextView msg;
    }


    /**
     * 另外一种方法解析表情将[表情]换成fxxx
     * 
     * @param message
     *            传入的需要处理的String
     * @return
     */
    private String convertNormalStringToSpannableString(String message) {
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }

        Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            if (PushApplication.getInstance().getFaceMap().containsKey(str2)) {
                String faceName = mContext.getResources().getString(
                        PushApplication.getInstance().getFaceMap().get(str2));
                CharSequence name = options(faceName);
                message = message.replace(str2, name);
            }

        }
        return message;
    }

    /**
     * 取名字f010
     * 
     * @param faceName
     */
    private CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }

    static class ViewHolder {

        ImageView head;
        TextView time;
        GifTextView msg;
        ImageView imageView;
        ProgressBar progressBar;
        TextView voiceTime;
        ImageView ivphoto;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    @Override
    public int getItemViewType(int position) {
        // logger.d("chat#getItemViewType -> position:%d", position);
        try {
            if (position >= mMsgList.size()) {
                return MESSAGE_TYPE_INVALID;
            }

            MessageItem item = mMsgList.get(position);
            if (item != null) {
                boolean comMeg = item.isComMeg();
                int type = item.getMsgType();
                if (comMeg) {
                    // 接受的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_TEXT: {
                            return MESSAGE_TYPE_OTHER_TEXT;
                        }

                        case MessageItem.MESSAGE_TYPE_IMG: {
                            return MESSAGE_TYPE_OTHER_IMAGE;
                        }

                        case MessageItem.MESSAGE_TYPE_RECORD: {
                            return MESSAGE_TYPE_OTHER_AUDIO;
                        }

                        default:
                            break;
                    }
                } else {
                    // 发送的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_TEXT: {
                            return MESSAGE_TYPE_MINE_TETX;

                        }

                        case MessageItem.MESSAGE_TYPE_IMG: {
                            return MESSAGE_TYPE_MINE_IMAGE;

                        }

                        case MessageItem.MESSAGE_TYPE_RECORD: {
                            return MESSAGE_TYPE_MINE_AUDIO;
                        }

                        default:
                            break;
                    }
                }
            }
            return MESSAGE_TYPE_INVALID;
        } catch (Exception e) {
            Log.e("fff", e.getMessage());
            return MESSAGE_TYPE_INVALID;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}