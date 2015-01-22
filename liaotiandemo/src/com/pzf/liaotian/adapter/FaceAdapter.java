package com.pzf.liaotian.adapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pzf.liaotian.R;
import com.pzf.liaotian.app.PushApplication;

/**
 * @desc:表情展示的gridview的adapter
 * @author: pangzf
 * @date: 2014年11月3日 上午11:13:53
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class FaceAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int currentPage = 0;
    private Map<String, Integer> mFaceMap;
    private List<Integer> faceList = new ArrayList<Integer>();// 存放表情资源的list
    private Context mContext;
    private Resources mResources;
    private int mCount = 0;

    public FaceAdapter(Context context, int currentPage) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.currentPage = currentPage;
        mResources = mContext.getResources();
        mFaceMap = PushApplication.getInstance().getFaceMap();
        initData();
    }

    private void initData() {
        for (Map.Entry<String, Integer> entry : mFaceMap.entrySet()) {
            // 此处只显示5张透明背景的表情
            if (mCount >= 0 && mCount <= 4) {
                int id = getPngFace(entry);
                if (id != 0) {
                    faceList.add(id);
                    mCount++;
                }

            } else {
                faceList.add(entry.getValue());
            }
            // =====如果正常情况
            // faceList.add(entry.getValue());
            // ====如果gif表情都换成了png则用一下代码
            // int id = getPngFace(entry);
            // if (id != 0) {
            // faceList.add(id);
            // }

        }
    }

    /**
     * 获取png的表情资源
     * 
     * @param entry
     * @return
     */
    private int getPngFace(Map.Entry<String, Integer> entry) {
        String strName = mResources.getString(entry.getValue());
        String newName = strName.substring(strName.lastIndexOf("/") + 1,
                strName.lastIndexOf("."));
        newName = "d" + newName;
        int id = 0;
        // ===需要png的表情，来替换之后就可以gridview中item中的表情背景不是白色的了
        Field field;
        try {
            field = R.drawable.class.getDeclaredField(newName);
            id = Integer.parseInt(field.get(null).toString());
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int getCount() {
        return PushApplication.NUM + 1;
    }

    @Override
    public Object getItem(int position) {
        return faceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.zf_chat_face, null, false);
            viewHolder.faceIV = (ImageView) convertView
                    .findViewById(R.id.face_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == PushApplication.NUM) {
            viewHolder.faceIV.setImageResource(R.drawable.emotion_del_selector);
            viewHolder.faceIV.setBackgroundDrawable(null);
        } else {
            int count = PushApplication.NUM * currentPage + position;
            // 总共107个表情==
            if (count < 107) {
                viewHolder.faceIV.setImageResource(faceList.get(count));
            } else {
                viewHolder.faceIV.setImageDrawable(null);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView faceIV;
    }
}
