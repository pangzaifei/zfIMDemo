package com.pzf.liaotian.common.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.pzf.liaotian.app.PushApplication;

/**
 * @desc:声音工具类，包括录音，播放录音等
 * @author: pangzf
 * @date: 2014年11月5日 下午2:50:35
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class SoundUtil {
    private static final double EMA_FILTER = 0.6;
    private static SoundUtil INSTANCE;
    private static MediaRecorder mMediaRecorder;
    private double mEMA = 0.0;
    private MediaPlayer mMediaPlayer;

    public SoundUtil() {
    }

    public static SoundUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (SoundUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SoundUtil();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * 初始化
     */
    private static void initMedia() throws Exception {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder
                    .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
    }

    /**
     * 开始录音
     * 
     * @param name
     *            声音存储的路径
     */
    public void startRecord(Context context, String name) {
        try {
            initMedia();
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(context, "麦克风不可用", 0).show();
        }
        StringBuilder sb = getFilePath(context, name);
        mMediaRecorder.setOutputFile(sb.toString());
        Log.e("fff", "录音路径:" + sb.toString());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mEMA = 0.0;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public StringBuilder getFilePath(Context context, String name) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("/storage/emulated/0");
    // sb.append("/");
    // sb.append(name);
    // return sb;
    // }

    public StringBuilder getFilePath(Context context, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(getDiskFielsDir(context));
        sb.append(File.separator);
        sb.append(name);
        return sb;
    }

    /**
     * 获得录音的文件名
     * 
     * @param context
     * @param name
     * @return
     */
    public String getRecordFileName() {

        return PushApplication.getInstance().getSpUtil().getUserId() + "_"
                + System.currentTimeMillis() + "record.amr";
        // return System.currentTimeMillis() + ".amr";
    }

    /**
     * 停止录音
     */
    public void stopRecord() throws IllegalStateException {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 获得缓存路径
     * 
     * @param name
     * @return
     */
    // public String getDiskCacheDir(Context context) {
    // String cachePath;
    // if (Environment.MEDIA_MOUNTED.equals(Environment
    // .getExternalStorageState())
    // || !Environment.isExternalStorageRemovable()) {
    // cachePath = context.getExternalCacheDir().getPath();
    // } else {
    // cachePath = context.getCacheDir().getPath();
    // }
    // return cachePath;
    // }
    /**
     * 获取录音地址
     * 
     * @param context
     * @return
     */
    public String getDiskFielsDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalFilesDir(
                    android.os.Environment.DIRECTORY_MUSIC).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获得缓存路径
     * 
     * @param name
     * @return
     */
    // public String getDiskFielsDir(Context context) {
    // String cachePath;
    // if (Environment.MEDIA_MOUNTED.equals(Environment
    // .getExternalStorageState())
    // || !Environment.isExternalStorageRemovable()) {
    // File file = null;
    // if (file == null) {
    // File musicFile = context.getExternalFilesDir(
    // Environment.DIRECTORY_MUSIC);
    // if(musicFile==null){
    //
    // }
    // String path = context.getExternalFilesDir(
    // Environment.DIRECTORY_MUSIC).getPath();
    // file = new File(path);
    // }
    // if (!file.exists()) {
    // file.mkdirs();
    // }
    // cachePath = file.getPath();
    // } else {
    // cachePath = context.getFilesDir().getPath();
    // }
    // return cachePath;
    // }

    public double getAmplitude() {
        if (mMediaRecorder != null)
            return (mMediaRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    /**
     * @Description
     * @param name
     */
    public void playRecorder(Context context, String name) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            // mMediaPlayer.setDataSource(getFilePath(context,
            // name).toString());
            Log.e("fff", "播放路径:" + getFilePath(context, name).toString());
            // String filePath = "/storage/emulated/0/1415255297852.amr";
            // String filePath = getFilePath(context, "1415255297852.amr")
            // .toString();
            String filePath = getFilePath(context, name).toString();
            File file = new File(filePath);
            if (file.exists()) {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer
                        .setOnCompletionListener(new OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                Log.e("fff", "播放方程");
                                mMediaPlayer.release();
                                mMediaPlayer = null;
                            }
                        });
            } else {
                // 不存在语音文件
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}