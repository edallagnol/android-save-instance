package com.edallagnol.androidsaveinstance;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public abstract class Bundler<T> {
	private static Map<Class, Bundler> customBundlers;
	private static Map<Class, Bundler> cache;

	public abstract void put(String key, T value, Bundle bundle);

	public abstract T get(String key, Bundle bundle);

	static <T> void putCustomBundler(Bundler<T> bundler, Class<T> clss) {
		if (customBundlers == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				customBundlers = new ArrayMap<>();
			} else {
				customBundlers = new HashMap<>();
			}
		}
		customBundlers.put(clss, bundler);
	}

	private static <T> Bundler<T> createAndCache(Class<? extends Bundler<T>> c) {
		if (cache == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				cache = new ArrayMap<>();
			} else {
				cache = new HashMap<>();
			}
		}

		@SuppressWarnings("unchecked")
		Bundler<T> bundler = cache.get(c);
		if (bundler == null) {
			//noinspection TryWithIdenticalCatches
			try {
				Constructor<? extends Bundler<T>> defConst = c.getDeclaredConstructor();
				defConst.setAccessible(true);
				bundler = defConst.newInstance();
				cache.put(c, bundler);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		return bundler;
	}

	static Bundler<?> from(Field field) {
		Class<?> clss = field.getType();

		Class<? extends Bundler> customBundler = field.getAnnotation(Save.class).value();
		if (customBundler != Bundlers.VoidBundler.class) {
			//noinspection unchecked
			return createAndCache((Class)customBundler);
		}

		if (customBundlers != null) {
			Bundler<?> bundler = customBundlers.get(clss);
			if (bundler != null) {
				return bundler;
			}
		}

		if (clss.isPrimitive()) {
			return Bundlers.SerializableBundler.instance;
		}

		if (Parcelable.class.isAssignableFrom(clss)) {
			return Bundlers.ParcelableBundler.instance;
		}

		if (clss.isArray()) {
			if (clss.getComponentType().isPrimitive()) {
				return Bundlers.SerializableBundler.instance;
			}
			if (Parcelable.class.isAssignableFrom(clss.getComponentType())) {
				//noinspection unchecked
				return new Bundlers.ParcelableArrayBundler(clss);
			}
			if (Serializable.class.isAssignableFrom(clss.getComponentType())) {
				return Bundlers.SerializableBundler.instance;
			}
		}

		if (List.class.isAssignableFrom(clss)) {
			ParameterizedType listType = (ParameterizedType) field.getGenericType();
			Class<?> typeArg = (Class<?>) listType.getActualTypeArguments()[0];
			if (Parcelable.class.isAssignableFrom(typeArg)) {
				/*if (!ArrayList.class.isAssignableFrom(clss)) {
					Log.w("AndroidSaveInstance", "Only ArrayList is Supported!");
				}*/
				return Bundlers.ParcelableArrayListBundler.instance;
			}
			if (Serializable.class.isAssignableFrom(typeArg)) {
				return Bundlers.SerializableBundler.instance;
			}
			throw new RuntimeException("The type " + typeArg.getName()
					+ " must be Parcelable/Serializable." );
		}

		if (Serializable.class.isAssignableFrom(clss)) {
			return Bundlers.SerializableBundler.instance;
		}

		throw new RuntimeException("Bundler for " + field.getType() + " not found.");
	}
}
