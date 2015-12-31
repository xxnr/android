package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

public class CaiResult implements Serializable {

	public List<CaiSum> cai;

	public static class CaiSum {
		public String cat;
		public List<Cai> cai;
	}

	public static class Cai {
		public String img;
		public String name;
		public String desc;
		public String price;
		public String count;

	}

}
