package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class BrandsShaixuan extends ResponseResult {

	public List<Datas> datas;

	public static class Datas {
		public String name;
	}

}
