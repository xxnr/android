/**
 * 
 */
package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：HotLineResult 类描述： 创建人：尚前琛 创建时间：2015-9-20 下午2:06:37 修改备注：
 */
public class HotLineResult extends ResponseResult {
	public Datas datas;

	public static class Datas {
		public String id;
		public String value;
		public String lable;
		public String key;
	}
}
