package com.edallagnol.androidsaveinstance;

import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Injector<T> {
	private static final Map<Class, Injector> injectorsCache;

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			injectorsCache = new ArrayMap<>();
		} else {
			injectorsCache = new HashMap<>();
		}
	}

	private final Field[] anotated;
	private final Bundler[] bundlers;

	Injector(Field[] anotated, Bundler[] bundlers) {
		this.anotated = anotated;
		this.bundlers = bundlers;
	}

	@SuppressWarnings("unchecked")
	public final void save(T obj, Bundle outstate) {
		try {
			for (int i = 0; i != anotated.length; i++) {
				Field field = anotated[i];
				Bundler b = bundlers[i];
				Object value = field.get(obj);
				b.put(field.getName(), value, outstate);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	public final void restore(T obj, Bundle savedState) {
		try {
			for (int i = 0; i != anotated.length; i++) {
				Field field = anotated[i];
				Bundler b = bundlers[i];
				Object value = b.get(field.getName(), savedState);
				field.set(obj, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> Injector<T> from(Class<T> clss) {
		Injector<T> cached = injectorsCache.get(clss);
		if (cached == null) {
			cached = Injector.create(clss);
			injectorsCache.put(clss, cached);
		}
		return cached;
	}

	static <T> Injector<T> create(Class<T> clss) {
		List<Field> l = new ArrayList<>();
		List<Bundler> b = new ArrayList<>();

		for (Field field : clss.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Save.class)) {
				continue;
			}
			field.setAccessible(true);
			l.add(field);
			b.add(Bundler.from(field));
		}

		return new Injector<>(
				l.toArray(new Field[l.size()]),
				b.toArray(new Bundler[b.size()])
		);
	}
}
