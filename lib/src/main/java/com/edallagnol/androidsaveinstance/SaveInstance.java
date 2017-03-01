package com.edallagnol.androidsaveinstance;

import android.os.Bundle;

@SuppressWarnings("WeakerAccess")
public class SaveInstance {
	private SaveInstance() {
		throw new AssertionError();
	}

	public static void save(Object obj, Bundle outState) {
		save(obj, outState, (Class)obj.getClass());
	}

	public static void restore(Object obj, Bundle bundle) {
		restore(obj, bundle, (Class)obj.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T extends J, J> void save(T obj, Bundle outState, Class<J> baseClass) {
		Class<?> clss = obj.getClass();
		do {
			Injector injector = Injector.from(clss, obj.getClass(), true);
			injector.save(obj, outState);
		} while (clss != baseClass
				&& (clss = clss.getSuperclass()) != null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends J, J> void restore(T obj, Bundle savedState, Class<J> baseClass) {
		if (savedState == null) {
			return;
		}

		Class<?> clss = obj.getClass();
		do {
			Injector injector = Injector.from(clss, obj.getClass(), false);
			injector.restore(obj, savedState);
		} while (clss != baseClass
				&& (clss = clss.getSuperclass()) != null);
	}

	public static <T> void putCustomBundler(Class<T> clss, Bundler<T> bundler) {
		Bundler.putCustomBundler(bundler, clss);
	}
}
