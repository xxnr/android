/**
 * 
 */
package com.ksfc.newfarmer.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：addressData 类描述： 创建人：尚前琛 创建时间：2015-7-5 下午1:49:13 修改备注：
 */
public class AddressList extends ResponseResult {
	public Data datas;

	public static class Data {
		public int total;
		public String locationUserId;
		public List<Address> rows;
	}

	// address:拼接后的地址
	// userId:用户ID
	// addressId:地址唯一标识
	// type:是否默认(1.默认2.非默认)
	// receiptPhone:收货人手机号
	// receiptPeople：收货人名称
	// areaName
	public static class Address implements Serializable{
		public String address;
		public String userId;
		public String addressId;
		public String type;
		public String receiptPhone;
		public String receiptPeople;
		public String areaName;
		public String cityName;
		public String countyName;
		public String areaId;
		public String cityId;
		public String countyId;
		public String zipCode;
		public String townId;
		public String townName;
	}
}
