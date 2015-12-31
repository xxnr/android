package net.yangentao.util.test;

import net.yangentao.util.property.Getter;

public class Test {
	private Getter<String> imei = new Getter<String>("") {

		@Override
		protected String onInit(String defValue) {
			String s = null;
			// s = get imei from system
			return s;
		}

	};
}
