package net.yangentao.util;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;

public class ArraysUtil {
	@SuppressWarnings("unchecked")
	public static <T> T[] copyOf(T[] original, int newLength) {
		return (T[]) copyOf(original, newLength, original.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T, U> T[] copyOf(U[] original, int newLength,
			Class<? extends T[]> newType) {
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
				: (T[]) Array
						.newInstance(newType.getComponentType(), newLength);
		System.arraycopy(original, 0, copy, 0,
				Math.min(original.length, newLength));
		return copy;
	}

	private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex
					+ ") > toIndex(" + toIndex + ")");
		if (fromIndex < 0)
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		if (toIndex > arrayLen)
			throw new ArrayIndexOutOfBoundsException(toIndex);
	}

	// Like public version, but without range checks.
	private static int binarySearch0(Object[] a, int fromIndex, int toIndex,
			Object key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			@SuppressWarnings("rawtypes")
			Comparable midVal = (Comparable) a[mid];
			@SuppressWarnings("unchecked")
			int cmp = midVal.compareTo(key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	public static <T> int binarySearch(T[] a, int fromIndex, int toIndex,
			T key, Comparator<? super T> c) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key, c);
	}

	// Like public version, but without range checks.
	private static <T> int binarySearch0(T[] a, int fromIndex, int toIndex,
			T key, Comparator<? super T> c) {
		if (c == null) {
			return binarySearch0(a, fromIndex, toIndex, key);
		}
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = a[mid];
			int cmp = c.compare(midVal, key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	@SuppressWarnings("unchecked")
	public static <T, U> T[] copyOfRange(U[] original, int from, int to,
			Class<? extends T[]> newType) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
				: (T[]) Array
						.newInstance(newType.getComponentType(), newLength);
		System.arraycopy(original, from, copy, 0,
				Math.min(original.length - from, newLength));
		return copy;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] copyOfRange(T[] original, int from, int to) {
		return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
	}

	@SuppressWarnings("null")
	public static <T> void reverse(List<T> list) {
		if (null == list && list.size() <= 0) {
			return;
		}
		int size = list.size();
		for (int i = 0; i < list.size() / 2; ++i) {
			T tmp = list.get(i);
			list.set(i, list.get(size - 1 - i));
			list.set(size - 1 - i, tmp);
		}
	}
}
