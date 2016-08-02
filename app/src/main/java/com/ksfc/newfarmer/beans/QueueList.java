/**
 * 
 */
package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：QianXihe64 类名称：queueList 类描述： 创建人：王蕾 创建时间：2015-6-11 上午11:45:00 修改备注：
 */
public class QueueList extends ResponseResult {
	public Data datas;

	public class Data {
		public List<Rows> rows;

		public class Rows {
			public String name;
			public String id;
			public String _id;
		}
	}

}
