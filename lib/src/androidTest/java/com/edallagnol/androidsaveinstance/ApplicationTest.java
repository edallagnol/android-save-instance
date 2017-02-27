package com.edallagnol.androidsaveinstance;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends InheritanceTest<ApplicationTest.ParcelTest> {
	private static final long ITERATIONS = 1000L;
	@Save private int tInt;
	@Save private double tDouble;
	@Save private String tString;
	@Save private CharSequence tCharSequence;
	@Save private ParcelTest tParcel;
	@Save private List<String> tList;
	@Save private List<ParcelTest> tParcelList;
	@Save private ParcelTest[] tParcelArray;
	@Save private int[] tPrimitiveArray;
	@Save(TestIntBundler.class) private int tBundler;
	@Save private CustomBundlerObj tCustomBundler;
	//same name test
	@Save private int tInheritance;

	@Test
	public void testParcel() throws Exception {
		Bundle test = new Bundle();
		save(test, true);

		Parcel parcel = Parcel.obtain();
		parcel.writeBundle(test);
		parcel.setDataPosition(0);
		test = parcel.readBundle(getClass().getClassLoader());
		parcel.recycle();

		load(test, true);
		compare(true);
	}

	@Test
	public void testReference() throws Exception {
		Bundle test = new Bundle();
		save(test, true);
		load(test, true);
		compare(true);
	}

	@Test
	public void testNoInheritance() throws Exception {
		Bundle test = new Bundle();
		save(test, false);
		load(test, false);
		compare(false);
	}

	// max 3 ms per iteration
	@Test(timeout = ITERATIONS * 3)
	public void performanceInjectorCreationTest() throws Exception {
		long time = System.currentTimeMillis();
		for (long i = ITERATIONS; i-- != 0L; ) {
			Injector.create(ApplicationTest.class, ApplicationTest.class);
		}
		time = System.currentTimeMillis() - time;
		Log.i("AppTest-injector", "Total time: " + time + " ms");
		Log.i("AppTest-injector", "P/ operation time: " + ((double) time / ITERATIONS) + " ms");
	}

	// max 0.6 ms per iteration
	@Test(timeout = (long)(ITERATIONS * 0.6))
	public void performanceSaveLoadTestRefs() throws Exception {
		long time = System.currentTimeMillis();
		for (long i = ITERATIONS; i-- != 0L; ) {
			testReference();
		}
		time = System.currentTimeMillis() - time;
		Log.i("AppTest-saveLoadRefs", "Total time: " + time + " ms");
		Log.i("AppTest-saveLoadRefs", "P/ operation time: " + ((double) time / ITERATIONS) + " ms");
	}

	// max 1 ms per iteration
	@Test(timeout = ITERATIONS)
	public void performanceSaveLoadTest() throws Exception {
		long time = System.currentTimeMillis();
		for (long i = ITERATIONS; i-- != 0L; ) {
			testParcel();
		}
		time = System.currentTimeMillis() - time;
		Log.i("AppTest-saveLoadParcel", "Total time: " + time + " ms");
		Log.i("AppTest-saveLoadParcel", "P/ operation time: " + ((double) time / ITERATIONS)
				+ " ms");
	}

	private void save(Bundle test, boolean inheritanceTest) {
		tInt = 1;
		tDouble = 1.;
		tString = "s";
		tCharSequence = tString;
		tParcel = new ParcelTest(tInt, tString, tDouble);
		tList = new ArrayList<>();
		tList.add(tString);
		tParcelList = new ArrayList<>();
		tParcelList.add(tParcel);
		tParcelArray = new ParcelTest[] { tParcel };
		tPrimitiveArray = new int[] { tInt };
		tBundler = tInt;
		tCustomBundler = new CustomBundlerObj(tInt);
		super.tListGeneric = tParcelList;
		this.tInheritance = 2;
		super.tInheritance = tInt;

		SaveInstance.putCustomBundler(CustomBundlerObj.class, new TestCustomBundler());

		if (inheritanceTest) {
			SaveInstance.save(this, test, InheritanceTest.class);
		} else {
			SaveInstance.save(this, test);
		}
	}

	private void load(Bundle test, boolean inheritanceTest) {
		tInt = 0;
		tDouble = 0.;
		tString = null;
		tCharSequence = null;
		tParcel = null;
		tList = null;
		tParcelList = null;
		tParcelArray = null;
		tPrimitiveArray = null;
		tBundler = 0;
		super.tListGeneric = null;
		this.tInheritance = 0;
		super.tInheritance = 0;

		if (inheritanceTest) {
			SaveInstance.restore(this, test, InheritanceTest.class);
		} else {
			SaveInstance.restore(this, test);
		}
	}

	private void compare(boolean inheritanceTest) {
		Assert.assertEquals(tInt, 1);
		Assert.assertEquals(tDouble, 1.);
		Assert.assertEquals(tString, "s");
		Assert.assertEquals(tCharSequence, tString);
		Assert.assertEquals(tParcel, new ParcelTest(tInt, tString, tDouble));
		Assert.assertEquals(tList, Collections.singletonList("s"));
		Assert.assertEquals(tParcelList, Collections.singletonList(
				new ParcelTest(tInt, tString, tDouble)));
		Assert.assertEquals(tParcelArray[0], tParcel);
		Assert.assertEquals(tPrimitiveArray[0], tInt);
		Assert.assertEquals(tBundler, tInt + 2);
		Assert.assertEquals(tCustomBundler, new CustomBundlerObj(tInt));
		if (inheritanceTest) {
			Assert.assertEquals(this.tParcelList, tListGeneric);
			Assert.assertEquals(this.tInheritance, 2);
			Assert.assertEquals(super.tInheritance, tInt);
		}
	}

	private static class TestIntBundler extends Bundler<Integer> {
		@Override
		public void put(String key, Integer value, Bundle bundle) {
			bundle.putInt(key, value + 1);
		}

		@Override
		public Integer get(String key, Bundle bundle) {
			return bundle.getInt(key) + 1;
		}
	}

	private static class TestCustomBundler extends Bundler<CustomBundlerObj> {
		@Override
		public void put(String key, CustomBundlerObj value, Bundle bundle) {
			if (value == null) {
				bundle.putInt(key, -1);
			} else {
				bundle.putInt(key, value.test);
			}
		}

		@Override
		public CustomBundlerObj get(String key, Bundle bundle) {
			int test = bundle.getInt(key);
			if (test == -1) {
				return null;
			}
			return new CustomBundlerObj(test);
		}
	}

	private static class CustomBundlerObj {
		int test;

		CustomBundlerObj(int test) {
			this.test = test;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CustomBundlerObj that = (CustomBundlerObj) o;

			return test == that.test;

		}

		@Override
		public int hashCode() {
			return test;
		}
	}

	static class ParcelTest implements Parcelable {
		private int pInt;
		private String str;
		private double d;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ParcelTest that = (ParcelTest) o;

			return pInt == that.pInt && Double.compare(that.d, d) == 0
					&& (str != null ? str.equals(that.str) : that.str == null);

		}

		@Override
		public int hashCode() {
			int result;
			long temp;
			result = pInt;
			result = 31 * result + (str != null ? str.hashCode() : 0);
			temp = Double.doubleToLongBits(d);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		ParcelTest(int pInt, String str, double d) {
			this.pInt = pInt;
			this.str = str;
			this.d = d;
		}


		ParcelTest(Parcel in) {
			pInt = in.readInt();
			str = in.readString();
			d = in.readDouble();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(pInt);
			dest.writeString(str);
			dest.writeDouble(d);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public static final Creator<ParcelTest> CREATOR = new Creator<ParcelTest>() {
			@Override
			public ParcelTest createFromParcel(Parcel in) {
				return new ParcelTest(in);
			}

			@Override
			public ParcelTest[] newArray(int size) {
				return new ParcelTest[size];
			}
		};
	}
}