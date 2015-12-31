package net.yangentao.util.property;

import net.yangentao.util.PreferenceUtil;

public class PreferPropertyBool extends GetSetter<Boolean> {
	private String key;
	private PreferenceUtil sp;

	public PreferPropertyBool(PreferenceUtil prefer, String key,
			boolean defValue) {
		super(defValue);
		this.key = key;
		sp = prefer;
	}

	@Override
	protected Boolean onInit(Boolean defValue) {
		return sp.getBool(key, defValue);
	}

	@Override
	protected void onChange(Boolean newValue) {
		sp.putBool(key, newValue);
	}
}