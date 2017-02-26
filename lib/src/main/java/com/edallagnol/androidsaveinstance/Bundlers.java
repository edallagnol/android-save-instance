package com.edallagnol.androidsaveinstance;


import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

final class Bundlers {
	static final class ParcelableBundler extends Bundler<Parcelable> {
		public static final Bundler instance = new ParcelableBundler();

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
		public static final Bundler instance = new ParcelableArrayListBundler();
		@Override
		public void put(String key, ArrayList<Parcelable> value, Bundle bundle) {
			bundle.putParcelableArrayList(key, value);
		}

		@Override
		public ArrayList<Parcelable> get(String key, Bundle bundle) {
			return bundle.getParcelableArrayList(key);
		}
	}

	static final class ParcelableArrayBundler extends Bundler<Parcelable[]> {
		public static final Bundler instance = new ParcelableArrayBundler();
		@Override
		public void put(String key, Parcelable[] value, Bundle bundle) {
			bundle.putParcelableArray(key, value);
		}

		@Override
		public Parcelable[] get(String key, Bundle bundle) {
			return bundle.getParcelableArray(key);
		}
	}

	static final class SerializableBundler extends Bundler<Serializable> {
		public static final Bundler instance = new SerializableBundler();
		@Override
		public void put(String key, Serializable value, Bundle bundle) {
			bundle.putSerializable(key, value);
		}

		@Override
		public Serializable get(String key, Bundle bundle) {
			return bundle.getSerializable(key);
		}
	}


	static final class ByteBundler extends Bundler<Byte> {
		public static final Bundler instance = new ByteBundler();
		@Override
		public void put(String key, Byte value, Bundle bundle) {
			bundle.putByte(key, value);
		}

		@Override
		public Byte get(String key, Bundle bundle) {
			return bundle.getByte(key);
		}
	}

	static final class ShortBundler extends Bundler<Short> {
		public static final Bundler instance = new ShortBundler();
		@Override
		public void put(String key, Short value, Bundle bundle) {
			bundle.putShort(key, value);
		}

		@Override
		public Short get(String key, Bundle bundle) {
			return bundle.getShort(key);
		}
	}

	static final class CharBundler extends Bundler<Character> {
		public static final Bundler instance = new CharBundler();
		@Override
		public void put(String key, Character value, Bundle bundle) {
			bundle.putChar(key, value);
		}

		@Override
		public Character get(String key, Bundle bundle) {
			return bundle.getChar(key);
		}
	}

	static final class IntBundler extends Bundler<Integer> {
		public static final Bundler instance = new IntBundler();
		@Override
		public void put(String key, Integer value, Bundle bundle) {
			bundle.putInt(key, value);
		}

		@Override
		public Integer get(String key, Bundle bundle) {
			return bundle.getInt(key);
		}
	}

	static final class LongBundler extends Bundler<Long> {
		public static final Bundler instance = new LongBundler();
		@Override
		public void put(String key, Long value, Bundle bundle) {
			bundle.putLong(key, value);
		}

		@Override
		public Long get(String key, Bundle bundle) {
			return bundle.getLong(key);
		}
	}

	static final class FloatBundler extends Bundler<Float> {
		public static final Bundler instance = new FloatBundler();
		@Override
		public void put(String key, Float value, Bundle bundle) {
			bundle.putFloat(key, value);
		}

		@Override
		public Float get(String key, Bundle bundle) {
			return bundle.getFloat(key);
		}
	}

	static final class DoubleBundler extends Bundler<Double> {
		public static final Bundler instance = new DoubleBundler();
		@Override
		public void put(String key, Double value, Bundle bundle) {
			bundle.putDouble(key, value);
		}

		@Override
		public Double get(String key, Bundle bundle) {
			return bundle.getDouble(key);
		}
	}
}
