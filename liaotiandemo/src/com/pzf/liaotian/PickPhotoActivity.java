package com.pzf.liaotian;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pzf.liaotian.adapter.ImageBucketAdapter;
import com.pzf.liaotian.album.AlbumHelper;
import com.pzf.liaotian.bean.album.ImageBucket;
import com.pzf.liaotian.config.ConstantKeys;

/**
 * @desc:相册列表
 * @author: pangzf
 * @date: 2014年11月12日 下午4:41:05
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class PickPhotoActivity extends TitleBarActivity implements
        OnTouchListener {

    public static Bitmap mPhotoBimap = null;// 图片
    private List<ImageBucket> mDataList = null;
    private ListView mListView;
    private ImageBucketAdapter mAdapter;
    private AlbumHelper mHelper;

    // private Handler mUiHandler;

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
        setContentView(R.layout.act_pick_photo);
        initTitle();
        initData();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("fff", "回调");
        if (RESULT_OK != resultCode) {
            return;
        }

        if (requestCode == ConstantKeys.ALBUM_PREVIEW_BACK) {
            boolean finishActivity = data.getExtras().getBoolean("finish");
            if (finishActivity) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mHelper = AlbumHelper.getHelper(getApplicationContext());
        mDataList = mHelper.getImagesBucketList(false);
        mPhotoBimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_takephoto_default_album_grid_image);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list);
        // mAdapter = new ImageBucketAdapter(this, mDataList);
        mAdapter = new ImageBucketAdapter(this);
        for (ImageBucket m : mDataList) {
            m.setStyle(ConstantKeys.ALBUM_IMAGE_GRIDVIEW_DISPLAYTYPE_SINGLE);
        }
        mAdapter.setData(mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnTouchListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                openImageGridActivity(position);
            }
        });

        // mUiHandler = new Handler() {
        // public void handleMessage(Message msg) {
        // super.handleMessage(msg);
        // switch (msg.what) {
        // case ConstantKeys.HANDLER_CANCEL_SELECTED:
        // mAdapter.setSelectedPosition(-1);
        // mAdapter.notifyDataSetChanged();
        // break;
        // default:
        // break;
        // }
        // }
        // };
    }

    /**
     * 跳到具体的相册内容页
     * 
     * @param position
     */
    private void openImageGridActivity(int position) {
        Intent intent = new Intent(PickPhotoActivity.this,
                ImageGridActivity.class);
        intent.putExtra(ConstantKeys.EXTRA_IMAGE_LIST,
                (Serializable) mDataList.get(position).imageList);
        intent.putExtra(ConstantKeys.EXTRA_ALBUM_NAME,
                mDataList.get(position).bucketName);
        startActivityForResult(intent, ConstantKeys.ALBUM_PREVIEW_BACK);
        // setResult(Activity.RESULT_OK, null);
        // finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // View mDownView = null;
        // @SuppressWarnings("unused")
        // float mDownX = 0;
        // int mDownPosition = -1;
        // VelocityTracker mVelocityTracker = null;
        // switch (motionEvent.getAction()) {
        // case MotionEvent.ACTION_DOWN: {
        // Rect rect = new Rect();
        // int childCount = mListView.getChildCount();
        // int[] listViewCoords = new int[2];
        // mListView.getLocationOnScreen(listViewCoords);
        // int x = (int) motionEvent.getRawX() - listViewCoords[0];
        // int y = (int) motionEvent.getRawY() - listViewCoords[1];
        // View child;
        // for (int i = 0; i < childCount; i++) {
        // child = mListView.getChildAt(i);
        // child.getHitRect(rect);
        // if (rect.contains(x, y)) {
        // mDownView = child;
        // break;
        // }
        // }
        //
        // if (mDownView != null) {
        // mDownX = motionEvent.getRawX();
        // mDownPosition = mListView.getPositionForView(mDownView);
        //
        // mVelocityTracker = VelocityTracker.obtain();
        // mVelocityTracker.addMovement(motionEvent);
        //
        // mAdapter.setSelectedPosition(mDownPosition);
        // mAdapter.notifyDataSetChanged();
        // }
        // }
        // break;
        // case MotionEvent.ACTION_UP:
        // mUiHandler
        // .sendEmptyMessage(ConstantKeys.HANDLER_CANCEL_SELECTED);
        // break;
        // }
        return false;
    }

}
