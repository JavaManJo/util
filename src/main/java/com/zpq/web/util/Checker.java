package com.zpq.web.util;


import com.zpq.web.exception.LogicException;

import java.util.Collection;
import java.util.Map;

/**
 * 通用检查工具
 * 
 * @author zpq 2018年5月11日
 */
public abstract class Checker {

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static void isNull(Object obj, String message) {
		if (obj == null) {
			throw LogicException.valueOfUnknow(message);
		}
	}

	public static boolean notNull(Object obj) {
		return obj != null;
	}

	public static void notNull(Object obj, String message) {
		if (obj != null) {
			throw LogicException.valueOfUnknow(message);
		}
	}

	public static <T> boolean isEmpty(Collection<T> coll) {
		if (coll == null) {
			return true;
		}
		return coll.isEmpty();
	}

	public static <T> void isEmpty(Collection<T> coll, String message) {
		if (coll == null) {
			throw LogicException.valueOfUnknow(message);
		}
		if (coll.isEmpty()) {
			throw LogicException.valueOfUnknow(message);
		}
	}

	public static <T> boolean notEmpty(Collection<T> coll) {
		if (coll == null) {
			return false;
		}
		return !coll.isEmpty();
	}

	public static <T> void notEmpty(Collection<T> coll, String message) {
		if (coll == null) {
			return;
		}
		if (coll.isEmpty()) {
			return;
		}

		throw LogicException.valueOfUnknow(message);
	}

	public static <K, V> boolean isEmpty(Map<K, V> map) {
		if (map == null) {
			return true;
		}
		return map.isEmpty();
	}

	public static <K, V> void isEmpty(Map<K, V> map, String message) {
		if (map == null) {
			throw LogicException.valueOfUnknow(message);
		}
		if (map.isEmpty()) {
			throw LogicException.valueOfUnknow(message);
		}
	}

	public static <K, V> boolean notEmpty(Map<K, V> map) {
		if (map == null) {
			return false;
		}
		return !map.isEmpty();
	}

	public static <K, V> void notEmpty(Map<K, V> map, String message) {
		if (map == null) {
			return;
		}
		if (map.isEmpty()) {
			return;
		}
		throw LogicException.valueOfUnknow(message);
	}

	public static boolean isEq(Number a, Number b) {
		if (!a.getClass().equals(b.getClass())) {
			throw LogicException.valueOfUnknow("数字类型不一致");
		}
		return a.equals(b);
	}

	public static void isEq(Number a, Number b, String message) {
		if (!a.getClass().equals(b.getClass())) {
			throw LogicException.valueOfUnknow("数字类型不一致");
		}
		if (a.equals(b)) {
			throw LogicException.valueOfUnknow(message);
		}
	}

	public static boolean isNe(Number a, Number b) {
		if (!a.getClass().equals(b.getClass())) {
			throw LogicException.valueOfUnknow("数字类型不一致");
		}
		return !a.equals(b);
	}

	public static void isNe(Number a, Number b, String message) {
		if (!a.getClass().equals(b.getClass())) {
			throw LogicException.valueOfUnknow("数字类型不一致");
		}
		if (!a.equals(b)) {
			throw LogicException.valueOfUnknow(message);
		}
	}
}
