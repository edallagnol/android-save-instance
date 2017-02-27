package com.edallagnol.androidsaveinstance;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class Injector<T> {
	private static final int INVALID_MODIFIERS = Modifier.STATIC | Modifier.FINAL;
	private static final int CACHE_SIZE = 8; // 8 least recent used entries
	private static final LinkedHashMap<Class, Injector> injectorsCache;

	static {
		injectorsCache = new LinkedHashMap<Class, Injector>(CACHE_SIZE, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Entry eldest) {
				return size() > CACHE_SIZE;
			}
		};
	}

	private final Field[] annotated;
	private final Bundler[] bundlers;

	private Injector(Field[] annotated, Bundler[] bundlers) {
		this.annotated = annotated;
		this.bundlers = bundlers;
	}

	@SuppressWarnings("unchecked")
	final void save(T obj, Bundle outstate) {
		try {
			for (int i = 0; i != annotated.length; i++) {
				Field field = annotated[i];
				Bundler b = bundlers[i];
				Object value = field.get(obj);
				b.put(getName(field), value, outstate);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	final void restore(T obj, Bundle savedState) {
		try {
			for (int i = 0; i != annotated.length; i++) {
				Field field = annotated[i];
				Bundler b = bundlers[i];
				Object value = b.get(getName(field), savedState);
				field.set(obj, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getName(Field f) {
		return f.getDeclaringClass().getName() + " " + f.getName();
	}

	@SuppressWarnings("unchecked")
	static <T> Injector<T> from(Class<T> clss, Class<?> parametrizedSubClass) {
		Injector<T> cached = injectorsCache.get(clss);
		if (cached == null) {
			cached = Injector.create(clss, parametrizedSubClass);
			injectorsCache.put(clss, cached);
		}
		return cached;
	}

	static <T> Injector<T> create(Class<T> clss, Class<?> parametrizedSubClass) {
		List<Field> l = new ArrayList<>();
		List<Bundler> b = new ArrayList<>();

		for (Field field : clss.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Save.class)) {
				continue;
			}
			if ((field.getModifiers() & INVALID_MODIFIERS) != 0) {
				throw new RuntimeException("Field " + clss.getName() + "." + field.getName()
						+ " cannot be final or static.");
			}
			field.setAccessible(true);
			l.add(field);
			b.add(Bundler.from(field, parametrizedSubClass));
		}

		return new Injector<>(
				l.toArray(new Field[l.size()]),
				b.toArray(new Bundler[b.size()])
		);
	}
}
