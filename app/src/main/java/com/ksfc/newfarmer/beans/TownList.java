/**
 * 
 */
package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * 项目名称：QianXihe64 类名称：BuildingList 类描述： 创建人：王蕾 创建时间：2015-6-11 上午11:56:00 修改备注：
 */
@SuppressWarnings("serial")
public class TownList extends ResponseResult {
	public Data datas;

	public static class Data {
		public int total;
		public List<TownData> rows;
	}

	public static class TownData {
		public String id;
		public String name;
		public String provinceId;
		public String cityId;
		public String countyId;
		public String _id;
	}
}
