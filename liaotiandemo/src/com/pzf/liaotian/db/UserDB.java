package com.pzf.liaotian.db;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pzf.liaotian.bean.User;
/**
 * @desc:用户数据库
 * @author: pangzf
 * @date: 2015年1月21日 下午4:32:51
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class UserDB {
	private UserDBHelper helper;

	public UserDB(Context context) {
		helper = new UserDBHelper(context);
	}

	public User selectInfo(String userId) {
		User u = new User();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId + "" });
		if (c.moveToFirst()) {
			u.setHeadIcon(c.getInt(c.getColumnIndex("img")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		} else {
			return null;
		}
		return u;
	}

	public void addUser(List<User> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		for (User u : list) {
			db.execSQL(
					"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
					new Object[] { u.getUserId(), u.getNick(), u.getHeadIcon(),
							u.getChannelId(), u.getGroup() });
		}
		db.close();
	}

	public void addUser(User u) {
		if (selectInfo(u.getUserId()) != null) {
			update(u);
			return;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
				new Object[] { u.getUserId(), u.getNick(), u.getHeadIcon(),
						u.getChannelId(), u.getGroup() });
		db.close();

	}

	public User getUser(String userId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId });
		User u = new User();
		if (c.moveToNext()) {
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getInt(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		}
		return u;
	}

	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

	public List<User> getUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<User> list = new LinkedList<User>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			User u = new User();
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getInt(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
			list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	public void update(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update user set nick=?,img=?,_group=? where userId=?",
				new Object[] { u.getNick(), u.getHeadIcon(), u.getGroup(),
						u.getUserId() });
		db.close();
	}

	public User getLastUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user", null);
		User u = new User();
		while (c.moveToLast()) {
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getInt(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		}
		c.close();
		db.close();
		return u;
	}

	public void delUser(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user where userId=?",
				new Object[] { u.getUserId() });
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}
