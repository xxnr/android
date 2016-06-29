package com.ksfc.newfarmer.http.beans;

import java.util.List;

import com.ksfc.newfarmer.http.ResponseResult;

public class VersionResult extends ResponseResult {
	public Data datas;

	// locationUserId:用户ID,
	// total:count(总条数),
	// rows:List{
	// id（版本id）,
	// preId(上个版本),
	// versionCode(字符),
	// versionNum(版本数字)
	public static class Data {
		public String locationUserId;// 用户ID
		public int total;// (总条数)
		public List<VersionRows> rows;
	}

	public static class VersionRows {
		public String id;
		public String preId;
		public String versionCode;
		public String versionNum;
	}
}
