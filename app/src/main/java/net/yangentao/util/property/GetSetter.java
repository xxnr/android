package net.yangentao.util.property;

import net.yangentao.util.Util;

public class GetSetter<T> extends Getter<T> {

	public GetSetter(T defValue) {
		super(defValue);
	}

	protected void onChange(T newValue) {

	}

	public void set(T value) {
		inited = true;
		if (!Util.equal(this.value, value)) {
			this.value = value;
			onChange(this.value);
		}
	}
}