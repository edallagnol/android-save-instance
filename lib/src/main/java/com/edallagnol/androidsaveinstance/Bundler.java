package com.edallagnol.androidsaveinstance;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public abstract class Bundler<T> {
	private static Map<Class, Bundler> customBundlers;

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

	private static <T> Bundler<T> createInstance(Class<? extends Bundler<T>> c) {
		//noinspection TryWithIdenticalCatches
		try {
			Constructor<? extends Bundler<T>> defConst = c.getDeclaredConstructor();
			defConst.setAccessible(true);
			return defConst.newInstance();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Class " + c.getName() + " must have a empty constructor.", e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	static Bundler<?> from(Field field, Class<?> parametrizedSubClass) {
		Class<?> clss = getFieldGenericType(field, parametrizedSubClass);

		Class<? extends Bundler> customBundler = field.getAnnotation(Save.class).value();
		if (customBundler != Bundlers.VoidBundler.class) {
			//noinspection unchecked
			return createInstance((Class)customBundler);
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
			Class<?> typeArg = getFieldGenericType(field, 0, parametrizedSubClass);
			if (Parcelable.class.isAssignableFrom(typeArg)) {
				return Bundlers.ParcelableArrayListBundler.instance;
			}
			if (Serializable.class.isAssignableFrom(typeArg)) {
				return Bundlers.SerializableBundler.instance;
			}
			throw new RuntimeException("The type " + typeArg
					+ " must be Parcelable/Serializable." );
		}

		if (Set.class.isAssignableFrom(clss)) {
			Class<?> typeArg = getFieldGenericType(field, 0, parametrizedSubClass);
			if (Parcelable.class.isAssignableFrom(typeArg)) {
				return Bundlers.ParcelableSetBundler.instance;
			}
			if (Serializable.class.isAssignableFrom(typeArg)) {
				return Bundlers.SerializableBundler.instance;
			}
			throw new RuntimeException("The type " + typeArg.getName()
					+ " must be Parcelable/Serializable.");
		}

		if (Serializable.class.isAssignableFrom(clss)) {
			return Bundlers.SerializableBundler.instance;
		}

		if (CharSequence.class.isAssignableFrom(clss)) {
			return Bundlers.CharSequenceBundler.instance;
		}

		throw new RuntimeException("Bundler for " + field.getType() + " not found.");
	}

	private static Class<?> getFieldGenericType(Field field, Class<?> parametrizedSubClass) {
		Type genericType = field.getGenericType();
		if (genericType instanceof Class) {
			return (Class<?>) genericType;
		}
		// generics
		if (genericType instanceof TypeVariable) {
			return getGenericClassFromType(
					(TypeVariable) genericType,
					field.getDeclaringClass(),
					parametrizedSubClass);
		}
		return field.getType();
	}

	private static Class<?> getFieldGenericType(Field field, int pos, Class<?> parametrizedSubClass) {
		ParameterizedType listType = (ParameterizedType) field.getGenericType();
		Type typeArg = listType.getActualTypeArguments()[pos];
		if (typeArg instanceof Class) {
			return (Class<?>) typeArg;
		}
		if (typeArg instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeArg).getRawType();
		}
		// parameterized type - find definition
		if (typeArg instanceof TypeVariable) {
			return getGenericClassFromType((TypeVariable) typeArg,
					field.getDeclaringClass(),
					parametrizedSubClass);
		}

		throw new AssertionError("Cannot find type of " + field.getDeclaringClass().getName() + "."
				+ field.getName());
	}

	private static Class<?> getGenericClassFromType(TypeVariable genericType,
													Class<?> declaringClass,
													Class<?> parametrizedSubClass) {
		// based on http://stackoverflow.com/a/25974010/2047679
		Map<Type, Class<?>> mapping = new HashMap<>();

		for (Class<?> c = parametrizedSubClass; c != null; ) {
			Type t = c.getGenericSuperclass();
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				Type[] typeArgs = pt.getActualTypeArguments();
				Type rawType = pt.getRawType();
				TypeVariable<?>[] tp = ((GenericDeclaration) rawType).getTypeParameters();

				if (rawType == declaringClass) {
					for (int i = typeArgs.length; i-- != 0; ) {
						if (tp[i].getName().equals(genericType.getName())) {
							Type arg = typeArgs[i];
							if (arg instanceof Class) {
								return (Class<?>) arg;
							} else {
								return mapping.get(arg);
							}
						}
					}
					throw new AssertionError("Cannot find type " + genericType.getName()
							+ " in " + rawType);
				}

				for (int i = typeArgs.length; i-- != 0; ) {
					if (typeArgs[i] instanceof Class<?>) {
						mapping.put(tp[i], (Class<?>) typeArgs[i]);
					} else {
						mapping.put(tp[i], mapping.get(typeArgs[i]));
					}
				}
				c = (Class<?>) rawType;
			} else {
				c = c.getSuperclass();
			}
		}

		throw new AssertionError("Cannot find type " + genericType.getName()
				+ " in " + declaringClass);
	}
}
