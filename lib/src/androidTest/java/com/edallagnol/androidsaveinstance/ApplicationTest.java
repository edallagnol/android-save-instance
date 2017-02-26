package com.edallagnol.androidsaveinstance;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
	@Save private int tInt;
	@Save private double tDouble;
	@Save private String tString;
	@Save private ParcelTest tParcel;
	@Save private List<String> tList;
	@Save private List<ParcelTest> tParcelList;

	@Test
	public void test() throws Exception {
		Bundle test = new Bundle();
		save(test);

		Parcel parcel = Parcel.obtain();
		parcel.writeBundle(test);
		parcel.setDataPosition(0);
		test = parcel.readBundle(getClass().getClassLoader());
		parcel.recycle();

		load(test);

		Assert.assertEquals(tInt, 1);
		Assert.assertEquals(tDouble, 1.);
		Assert.assertEquals(tString, "s");
		Assert.assertEquals(tParcel, new ParcelTest(tInt, tString, tDouble));
		Assert.assertEquals(tList, Collections.singletonList("s"));
		Assert.assertEquals(tParcelList, Collections.singletonList(
				new ParcelTest(tInt, tString, tDouble)));
	}

	public void save(Bundle test) {
		tInt = 1;
		tDouble = 1.;
		tString = "s";
		tParcel = new ParcelTest(tInt, tString, tDouble);
		tList = new ArrayList<>();
		tList.add(tString);
		tParcelList = new ArrayList<>();
		tParcelList.add(tParcel);

		SaveInstance.save(this, test);
	}

	public void load(Bundle test) {
		tInt = 0;
		tDouble = 0.;
		tString = null;
		tParcel = null;
		tList = null;
		tParcelList = null;

		SaveInstance.restore(this, test);
	}

	private static class ParcelTest implements Parcelable {
		private int pInt;
		private String str;
		private double d;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ParcelTest that = (ParcelTest) o;

			if (pInt != that.pInt) return false;
			if (Double.compare(that.d, d) != 0) return false;
			return str != null ? str.equals(that.str) : that.str == null;

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

		public ParcelTest(int pInt, String str, double d) {
			this.pInt = pInt;
			this.str = str;
			this.d = d;
		}

		protected ParcelTest(Parcel in) {
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