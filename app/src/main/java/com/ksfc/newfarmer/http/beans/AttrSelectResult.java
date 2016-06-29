package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

public class AttrSelectResult extends ResponseResult {


	/**
	 * _id : {"brand":null,"name":"品类"}
	 * values : ["有机肥","氮磷钾肥","复合肥"]
	 */

	public List<AttributesEntity> attributes;


	public static class AttributesEntity {
		/**
		 * brand : null
		 * name : 品类
		 */
		public IdEntity _id;
		public List<String> values;

		public static class IdEntity {
			public Object brand;
			public String name;

		}
	}
}
