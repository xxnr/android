package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

public class BrandsResult extends ResponseResult {
	/**
	 * _id : 568d66996595a09843cc5efe
	 * name : 江淮
	 */

	public List<BrandsEntity> brands;

	public static class BrandsEntity {
		public String _id;
		public String name;
	}
}
