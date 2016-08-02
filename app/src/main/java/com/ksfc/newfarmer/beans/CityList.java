/**
 * 
 */
package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：QianXihe64 类名称：cityList 类描述： 创建人：王蕾 创建时间：2015-6-11 上午11:50:48 修改备注：
 */
public class CityList extends ResponseResult {
	// "datas": {
	// "total": 12,
	// "rows": [{
	// "id": "06d648d6b3f241f9ba2343a33921bead",
	// "name": "河北3333",
	// "shortName": "H"
	// }, {
	// "id": "10",
	// "name": "上海",
	// "shortName": "S"
	// }, {
	public Data datas;

	public class Data {
		public List<Rows> rows;

		public class Rows {
			public String name;
			public String shortName;
			public String id;
			public String _id;
		}

	}
}
