package net.yangentao.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeWrap {
	public Type type;

	public TypeWrap(Type type) {
		this.type = type;
	}

	public Type getActuralType0() {
		return toParameterized().getActualTypeArguments()[0];
	}

	public boolean isClass() {
		return type instanceof Class;
	}

	public boolean isParameterized() {
		return type instanceof ParameterizedType;
	}

	/**
	 * 参数化的List: List<Person>, List<String>
	 */
	public boolean isParameterizedList() {
		if (isParameterized()) {
			Type raw = toParameterized().getRawType();
			return raw == List.class || raw == ArrayList.class;
		}
		return false;
	}

	public ParameterizedType toParameterized() {
		return (ParameterizedType) type;
	}
}