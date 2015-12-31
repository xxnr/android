package com.ksfc.newfarmer.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksfc.newfarmer.db.BaseDao;
import com.ksfc.newfarmer.utils.RndLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 
 * 项目名称：QianXihe 类名称：MessageDao 类描述： 创建人：范东 创建时间：2015年6月16日 下午5:04:16 修改备注：
 */
public class MessageDao extends BaseDao {

	private static final String TAG = "MessageDao";

	public static String TABLE_NAME_MESSAGE = "msg_" + uid;
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_SEND_ID = "sendId";
	public static final String COLUMN_RECEIVE_ID = "receiveId";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_SENDDATE = "sendDate";
	public static final String COLUMN_READSTATUS = "readStatus";
	public static final String COLUMN_DELFLAG = "delFlag";

	public MessageDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/** 每创建一个数据表时都要动态在这个方法中修改表名为：TABLE_NAME+uid */
	@Override
	protected void initTableName(String userId) {
	}

	/** 保存数据 */
	public synchronized boolean saveMessage(Map<String, String> map) {
		if (isDBOpen()) {
			ContentValues values = new ContentValues();

			values.put(COLUMN_ID, map.get("id"));
			values.put(COLUMN_TITLE, map.get("title"));
			values.put(COLUMN_SEND_ID, map.get("sendId"));
			values.put(COLUMN_RECEIVE_ID, map.get("receiveId"));
			values.put(COLUMN_CONTENT, map.get("content"));
			values.put(COLUMN_SENDDATE, map.get("sendData"));
			values.put(COLUMN_READSTATUS, map.get("readStatus"));
			values.put(COLUMN_DELFLAG, map.get("delFlag"));

			if (database.insert(TABLE_NAME_MESSAGE, null, values) > 0) {
				RndLog.i(
						TAG,
						"插入数据的值为：" + map.get("id") + " " + map.get("title")
								+ " " + map.get("sendId") + " "
								+ map.get("receiveId") + " "
								+ map.get("content") + " "
								+ map.get("sendData") + " "
								+ map.get("readStatus") + " "
								+ map.get("delFlag"));
				return true;
			}

		}
		return false;
	}

	/** 根据id读取单条数据 */
	public synchronized Map<String, String> getMessage(String msgId) {
		Map<String, String> data = new HashMap<String, String>();
		if (isDBOpen()) {
			String sql = "SELECT * FROM " + TABLE_NAME_MESSAGE + " WHERE "
					+ COLUMN_ID + " = ?";
			Cursor cursor = database.rawQuery(sql, new String[] { msgId });

			if (cursor.moveToFirst()) {
				String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
				String title = cursor.getString(cursor
						.getColumnIndex(COLUMN_TITLE));
				String sendId = cursor.getString(cursor
						.getColumnIndex(COLUMN_SEND_ID));
				String receiveId = cursor.getString(cursor
						.getColumnIndex(COLUMN_RECEIVE_ID));
				String content = cursor.getString(cursor
						.getColumnIndex(COLUMN_CONTENT));
				String sendDate = cursor.getString(cursor
						.getColumnIndex(COLUMN_SENDDATE));
				String readStatus = cursor.getString(cursor
						.getColumnIndex(COLUMN_READSTATUS));
				String delFlag = cursor.getString(cursor
						.getColumnIndex(COLUMN_DELFLAG));

				data.put("id", id);
				data.put("title", title);
				data.put("sendId", sendId);
				data.put("receiveId", receiveId);
				data.put("content", content);
				data.put("sendDate", sendDate);
				data.put("readStatus", readStatus);
				data.put("delFlag", delFlag);
			}
			cursor.close();
		}
		return data;
	}

	/** 读取所有数据 */
	public synchronized List<Map> getAllMessages() {
		List<Map> list = new ArrayList<Map>();

		if (isDBOpen()) {
			String sql = "SELECT * FROM " + TABLE_NAME_MESSAGE + " ORDER BY "
					+ COLUMN_SENDDATE;
			Cursor cursor = database.rawQuery(sql, new String[] {});
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
				String title = cursor.getString(cursor
						.getColumnIndex(COLUMN_TITLE));
				String sendId = cursor.getString(cursor
						.getColumnIndex(COLUMN_SEND_ID));
				String receiveId = cursor.getString(cursor
						.getColumnIndex(COLUMN_RECEIVE_ID));
				String content = cursor.getString(cursor
						.getColumnIndex(COLUMN_CONTENT));
				String sendDate = cursor.getString(cursor
						.getColumnIndex(COLUMN_SENDDATE));
				String readStatus = cursor.getString(cursor
						.getColumnIndex(COLUMN_READSTATUS));
				String delFlag = cursor.getString(cursor
						.getColumnIndex(COLUMN_DELFLAG));
				Map<String, String> data = new HashMap<String, String>();
				data.put("id", id);
				data.put("title", title);
				data.put("sendId", sendId);
				data.put("receiveId", receiveId);
				data.put("content", content);
				data.put("sendDate", sendDate);
				data.put("readStatus", readStatus);
				data.put("delFlag", delFlag);
				list.add(data);
			}
			cursor.close();
		}
		return list;
	}

	/** 删除单条数据 */
	public synchronized boolean deleteMessage(String msgId) {
		if (isDBOpen()) {
			if (database.delete(TABLE_NAME_MESSAGE, COLUMN_ID + " = ?",
					new String[] { msgId }) > 0) {
				return true;
			}
		}
		return false;
	}

	/** 更改消息的状态 */
	public synchronized boolean updateMessage(String msgId, String status) {
		if (isDBOpen()) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_READSTATUS, status);
			if (database.update(TABLE_NAME_MESSAGE, values, COLUMN_ID + " = ?",
					new String[] { msgId }) > 0) {
				RndLog.i(TAG, "呵呵，我将状态更新为：" + status);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isDBOpen() {
		return true;
	}
}
