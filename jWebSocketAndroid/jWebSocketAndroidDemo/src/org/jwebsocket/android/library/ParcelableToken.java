package org.jwebsocket.android.library;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableToken implements Parcelable {

	public static final Parcelable.Creator<ParcelableToken> CREATOR = new Parcelable.Creator<ParcelableToken>() {
		public ParcelableToken createFromParcel(Parcel in) {
			return new ParcelableToken(in);
		}

		public ParcelableToken[] newArray(int size) {
			return new ParcelableToken[size];
		}
	};

	public ParcelableToken(Parcel in) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
	}

}
