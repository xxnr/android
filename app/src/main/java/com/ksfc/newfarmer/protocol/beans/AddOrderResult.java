package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class AddOrderResult extends ResponseResult {
	public String id;
	public String paymentId;
	public String price;
	public String deposit;
	public String consigneeAddress;
	public String consigneePhone;
	public String consigneeName;
}
