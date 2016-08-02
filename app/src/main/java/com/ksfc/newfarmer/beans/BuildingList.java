/**
 * 
 */
package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：QianXihe64 类名称：BuildingList 类描述： 创建人：王蕾 创建时间：2015-6-11 上午11:56:00 修改备注：
 */
@SuppressWarnings("serial")
public class BuildingList extends ResponseResult {
	public Data datas;

	public static class Data {
		public int total;
		public List<BuildData> rows;
	}

	public static class BuildData {
		public String id;
		public String name;
		public String _id;
	}
}
