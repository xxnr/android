package com.ksfc.newfarmer.beans;

import java.io.Serializable;

public class GoodsSum implements Serializable {
	public int state;
	public double price;
	public double discount;
	public int num;

	public GoodsSum(int state, double price, double discount, int num) {
		super();
		this.state = state;
		this.price = price;
		this.discount = discount;
		this.num = num;
	}

}
