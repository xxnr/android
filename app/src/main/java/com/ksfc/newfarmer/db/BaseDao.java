package com.ksfc.newfarmer.db;

import com.ksfc.newfarmer.RndApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 项目名称：newFarmer 类名称：BaseDao 类描述： 所有操作数据库Dao的父类 创建人：范东 创建时间：2015年6月24日
 * 下午5:06:38 修改备注：
 */
public abstract class BaseDao {

	protected KsfcDBOpenHelper dbOpenHelper = null;
	protected SQLiteDatabase database = null;
	protected Context appContext;
	/** 用户id */
	protected static String uid = RndApplication.getInstance().getUid();
	/** 执行数据库操作的返回值 */
	protected long returnId = -1;

	protected BaseDao(Context context) {
		appContext = context;
		dbOpenHelper = KsfcDBOpenHelper.getInstance(context);
		database = dbOpenHelper.getWritableDatabase();
		// uid = RndApplication.getInstance().getUid();
		// initTableName(uid);
	}

	/**
	 * 每创建一个数据表时都要动态在这个方法中修改表名为： TABLE_NAME+uid uid 默认为 100001
	 * 
	 * @param userId
	 */
	protected abstract void initTableName(String userId);

	/**
	 * 在子类中自己判断数据库是否已经打开
	 * 
	 * @return
	 */
	protected abstract boolean isDBOpen();
}
