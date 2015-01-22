package com.pzf.liaotian.album;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

import com.pzf.liaotian.bean.album.ImageBucket;
import com.pzf.liaotian.bean.album.ImageItem;

/**
 * @desc:相册相关处理
 * @author: pangzf
 * @date: 2014年11月12日 下午3:17:45
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class AlbumHelper {
    Context context = null;
    ContentResolver contentResolver = null;

    // 缩略图列表
    HashMap<String, String> thumbnailList = new HashMap<String, String>();
    List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

    private static AlbumHelper instance = null;

    private AlbumHelper(Context cxt) {
        if (null == this.context && null != cxt) {
            this.context = cxt;
            contentResolver = context.getContentResolver();
        }
    }

    public static AlbumHelper getHelper(Context cxt) {
        if (null == instance) {
            instance = new AlbumHelper(cxt);
        }
        return instance;
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail() {
        String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA };
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI,
                    projection, null, null, null);
            getThumbnailColumnData(cursor);
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    /**
     * 从数据库中得到缩略图
     * 
     * @param cur
     */
    private void getThumbnailColumnData(Cursor cur) {
        try {
            if (null == cur)
                return;
            if (cur.moveToFirst()) {
                @SuppressWarnings("unused")
                int cId;
                int image_id;
                String image_path;
                int _idColumn = cur.getColumnIndex(Thumbnails._ID);
                int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
                int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

                do {
                    cId = cur.getInt(_idColumn);
                    image_id = cur.getInt(image_idColumn);
                    image_path = cur.getString(dataColumn);
                    thumbnailList.put("" + image_id, image_path);
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
        }
    }



    // 是否创建图片集
    boolean hasBuildImagesBucketList = false;

    /**
     * @Description 获取图片集
     */
    private void buildImagesBucketList() {
        Cursor cur = null;
        // long startTime = System.currentTimeMillis();
        try {
            // 构造缩略图索引
            getThumbnail();

            // 构造相册索引
            String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
                    Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME,
                    Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME };

            // 得到一个游标
            cur = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, null);
            if (null == cur)
                return;

            if (cur.moveToFirst()) {
                // 获取指定列的索引
                int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
                int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
                // int photoNameIndex =
                // cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
                // int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
                // int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
                int bucketDisplayNameIndex = cur
                        .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
                int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
                // int picasaIdIndex =
                // cur.getColumnIndexOrThrow(Media.PICASA_ID);
                // 获取图片总数
                @SuppressWarnings("unused")
                int totalNum = cur.getCount();

                do {
                    String id = cur.getString(photoIDIndex);
                    // String name = cur.getString(photoNameIndex);
                    String path = cur.getString(photoPathIndex);
                    // String title = cur.getString(photoTitleIndex);
                    // String size = cur.getString(photoSizeIndex);
                    String bucketName = cur.getString(bucketDisplayNameIndex);
                    String bucketId = cur.getString(bucketIdIndex);
                    // String picasaId = cur.getString(picasaIdIndex);

                    ImageBucket bucket = bucketList.get(bucketId);
                    if (bucket == null) {
                        bucket = new ImageBucket();
                        bucketList.put(bucketId, bucket);
                        bucket.imageList = new ArrayList<ImageItem>();
                        bucket.bucketName = bucketName;
                    }
                    bucket.count++;
                    ImageItem imageItem = new ImageItem();
                    imageItem.setImageId(id);
                    imageItem.setImagePath(path);
                    imageItem.setThumbnailPath(thumbnailList.get(id));
                    bucket.imageList.add(imageItem);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            cur.close();
        }

        try {
            Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                    .iterator();
            while (itr.hasNext()) {
                Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
                        .next();
                ImageBucket bucket = entry.getValue();
                for (int i = 0; i < bucket.imageList.size(); ++i) {
                    @SuppressWarnings("unused")
                    ImageItem image = bucket.imageList.get(i);
                }
            }
            hasBuildImagesBucketList = true;
        } catch (Exception e) {
        }
    }

    /**
     * 得到图片集
     * 
     * @param refresh
     * @return
     */
    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        try {
            if (refresh || (!refresh && !hasBuildImagesBucketList)) {
                buildImagesBucketList();
            }
            List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
            Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                    .iterator();
            while (itr.hasNext()) {
                Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
                        .next();
                ImageBucket bucket = entry.getValue();
                if (bucket.bucketName.equals("Camera")) {
                    tmpList.add(0, bucket);
                } else {
                    tmpList.add(bucket);
                }
            }
            return tmpList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到原始图像路径
     * 
     * @param image_id
     * @return
     */
    @SuppressWarnings("unused")
    private String getOriginalImagePath(String image_id) {
        try {
            String path = null;
            String[] projection = { Media._ID, Media.DATA };
            Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI,
                    projection, Media._ID + "=" + image_id, null, null);
            if (cursor != null) {
                try {
                    cursor.moveToFirst();
                    path = cursor.getString(cursor.getColumnIndex(Media.DATA));
                } catch (Exception e) {
                } finally {
                    cursor.close();
                }

            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得缓存目录
     * 
     * @return
     */
    public String getFileDiskCache() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || Environment.isExternalStorageRemovable()) {
            // sdcard路径
            File file = new File(context.getExternalFilesDir(
                    android.os.Environment.DIRECTORY_PICTURES).getPath()
                    + File.separator + "takephoto");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            // 缓存
            File file = new File(context.getFilesDir().getPath()
                    + File.separator + "takephoto");
            if (!file.exists()) {
                file.mkdirs();
            }

            return file.getAbsolutePath();
        }
    }

}
