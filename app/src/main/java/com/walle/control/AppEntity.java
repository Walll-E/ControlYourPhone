package com.walle.control;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/2/27.
 */

public class AppEntity implements Parcelable{

    public Drawable drawable;

    public String packageName;

    public String appName;

    protected AppEntity(Parcel in) {
        packageName = in.readString();
        appName = in.readString();
    }

    public static final Creator<AppEntity> CREATOR = new Creator<AppEntity>() {
        @Override
        public AppEntity createFromParcel(Parcel in) {
            return new AppEntity(in);
        }

        @Override
        public AppEntity[] newArray(int size) {
            return new AppEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(packageName);
        parcel.writeString(appName);
    }
}
