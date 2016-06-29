package com.ksfc.newfarmer.http.beans;

import java.io.Serializable;

public class GoodsSum implements Serializable {
	public int state;
	public float price;
	public float discount;
	public int num;

	public GoodsSum(int state, float price, float discount, int num) {
		super();
		this.state = state;
		this.price = price;
		this.discount = discount;
		this.num = num;
	}

}
