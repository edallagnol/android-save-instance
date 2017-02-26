package com.edallagnol.androidsaveinstance;


import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

final class Bundlers {
	static final class VoidBundler extends Bundler<Object> {
		@Override
		public void put(String key, Object value, Bundle bundle) {}

		@Override
		public Object get(String key, Bundle bundle) {
			return null;
		}
	}

	static final class ParcelableBundler extends Bundler<Parcelable> {
		static final Bundler instance = new ParcelableBundler();

		@Override
		public void put(String key, Parcelable value, Bundle bundle) {
			bundle.putParcelable(key, value);
		}

		@Override
		public Parcelable get(String key, Bundle bundle) {
			return bundle.getParcelable(key);
		}
	}

	static final class ParcelableArrayListBundler extends Bundler<ArrayList<Parcelable>> {
		static final Bundler instance = new ParcelableArrayListBundler();

		@Override
		public void put(String key, ArrayList<Parcelable> value, Bundle bundle) {
			bundle.putParcelableArrayList(key, value);
		}

		@Override
		public ArrayList<Parcelable> get(String key, Bundle bundle) {
			return bundle.getParcelableArrayList(key);
		}
	}

	static final class ParcelableArrayBundler<T extends Parcelable> extends Bundler<T[]> {
		private final Class<T[]> c;

		ParcelableArrayBundler(Class<T[]> c) {
			this.c = c;
		}

		@Override
		public void put(String key, T[] value, Bundle bundle) {
			bundle.putParcelableArray(key, value);
		}

		@Override
		public T[] get(String key, Bundle bundle) {
			Parcelable[] p = bundle.getParcelableArray(key);
			if (p == null) {
				return  null;
			}
			if (c.isInstance(p)) {
				//noinspection unchecked
				return (T[]) p;
			}
			Parcelable[] a = Arrays.copyOf(p, p.length, c);
			//noinspection unchecked
			return (T[]) a;
		}
	}

	static final class SerializableBundler extends Bundler<Serializable> {
		static final Bundler instance = new SerializableBundler();

		@Override
		public void put(String key, Serializable value, Bundle bundle) {
			bundle.putSerializable(key, value);
		}

		@Override
		public Serializable get(String key, Bundle bundle) {
			return bundle.getSerializable(key);
		}
	}
}
