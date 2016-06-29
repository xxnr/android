package com.ksfc.newfarmer.http.beans;

import com.google.gson.Gson;
import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

public class IntentionProductsResult extends ResponseResult {

	/**
	 * _id : 56a72696c1813a7c109138c4
	 * name : 全新和悦
	 */

	public List<IntentionProductsEntity> intentionProducts;

	public static IntentionProductsResult objectFromData(String str) {

		return new Gson().fromJson(str, IntentionProductsResult.class);
	}

	public static class IntentionProductsEntity {
		public String _id;
		public String name;

		public static IntentionProductsEntity objectFromData(String str) {

			return new Gson().fromJson(str, IntentionProductsEntity.class);
		}
	}
}
