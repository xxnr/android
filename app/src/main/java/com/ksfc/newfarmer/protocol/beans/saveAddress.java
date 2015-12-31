/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：saveAddress 类描述： 创建人：尚前琛 创建时间：2015-7-5 下午3:11:35 修改备注：
 */
public class saveAddress extends ResponseResult {
	public Save datas;

	public static class Save {
		public int total;
		public String rows;
		public String locationUserId;
	}
}
