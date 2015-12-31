package com.ksfc.newfarmer.db;

import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.dao.MessageDao;
import com.ksfc.newfarmer.db.dao.ShoppingDao;

import android.content.Context;

/**
 * 
 * 项目名称：QianXihe 类名称：DBConstact 类描述： 此类用于保存创建数据库表语句 创建人：范东 创建时间：2015年6月16日
 * 下午5:04:51 修改备注：
 */
public class DBConstact {
	private static Context context = RndApplication.applicationContext;

	// **************************以下为建表字符串*********************
	/** 服务器推送的消息表 */
	private static String MESSAGE_TABLE_CREATE = String.format(
			"CREATE TABLE IF NOT EXISTS %s " + " (" + MessageDao.COLUMN_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ MessageDao.COLUMN_TITLE + " TEXT, "
					+ MessageDao.COLUMN_SEND_ID + " TEXT, "
					+ MessageDao.COLUMN_RECEIVE_ID + " TEXT, "
					+ MessageDao.COLUMN_SENDDATE + " TEXT, "
					+ MessageDao.COLUMN_READSTATUS + " TEXT, "
					+ MessageDao.COLUMN_CONTENT + " TEXT, "
					+ MessageDao.COLUMN_DELFLAG + " TEXT" + "); ",
			MessageDao.TABLE_NAME_MESSAGE);

	/** 购物车数据表 */
	private static String SHOPPING_TABLE_CREATE = String.format(
			"CREATE TABLE IF NOT EXISTS %s " + " ( " + ShoppingDao.COLUMN_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ShoppingDao.COLUMN_PID + " TEXT, "
					+ ShoppingDao.COLUMN_DESC + " TEXT, "
					+ ShoppingDao.COLUMN_NUMBERS + " TEXT, "
					+ ShoppingDao.COLUMN_PRAISES + " TEXT, "
					+ ShoppingDao.COLUMN_PRICENOW + " TEXT, "
					+ ShoppingDao.COLUMN_PRICEOLD + " TEXT, "
					+ ShoppingDao.COLUMN_STARS + " TEXT, "
					+ ShoppingDao.COLUMN_TITLE + " TEXT, "
					+ ShoppingDao.COLUMN_TOTALSCORE + " TEXT, "
					+ ShoppingDao.COLUMN_TYPE + " TEXT, "
					+ ShoppingDao.COLUMN_URL + " TEXT, "
					+ ShoppingDao.COLUMN_USESCORE + " TEXT" + " ); "

			, ShoppingDao.TABLE_NAME_SHOPPING);

	// **************************以上为建表字符串******************************

	private static String getUid() {
		return RndApplication.getInstance().getUid();
	}

	/** 将要建立的数据表字符串加入到此数据中，方便被调用 */
	public static String[] CREATE_TABLES = { MESSAGE_TABLE_CREATE,
			SHOPPING_TABLE_CREATE

	};
}
