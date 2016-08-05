package com.smily.mybatis;

import org.jetbrains.annotations.Contract;

public final class EnumHelper {
	private EnumHelper() {
	}

	@Contract("_, null -> null")
	public static <T extends Enum<T>> T parse(Class<T> enumType, String name) {
		if (name == null) {
			return null;
		}

		T[] items = enumType.getEnumConstants();
		if (items != null) {
			for (T item : items) {
				if (item.name().equalsIgnoreCase(name)) {
					return item;
				}
			}
		}

		return null;
	}

	@Contract("_, null -> fail")
	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		if (name == null) {
			throw new IllegalArgumentException("Enum name must not be null");
		}

		T ret = parse(enumType, name);
		if (ret == null) {
			throw new IllegalArgumentException("No enum constant " + enumType.getName() + "." + name);
		}

		return ret;
	}
}
