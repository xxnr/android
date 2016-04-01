/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：JifenData 类描述： 创建人：尚前琛 创建时间：2015-7-3 下午8:22:39 修改备注：
 */
public class JifenData extends ResponseResult {
	public Data datas;

	public static class Data {
		public String pointLaterTrade;
	}


}
