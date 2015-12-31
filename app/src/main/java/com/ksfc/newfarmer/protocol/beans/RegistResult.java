/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：RegistResult 类描述： 创建人：尚前琛 创建时间：2015-7-4 上午6:20:48 修改备注：
 */
public class RegistResult extends ResponseResult {
	public Datas datas;

	public static class Datas {
		public String userId;
		public String nickname;
		public String no;
		public String userType;
		public String phone;
	}
}
