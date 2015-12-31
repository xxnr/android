package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class ClassIDResult extends ResponseResult {

	public List<Datas> categories;

	public static class Datas {
		public String name;
		public String id;
	}
}
