package com.cn.carigps.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述：广告信息</br>
 *
 * @author Eden Cheng</br>
 * @version 2015年4月23日 上午11:32:53
 */
public class ADInfo implements Parcelable {

    private int Id;
    private String ImgPath;
    private String Url;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }


    @Override
    public String toString() {
        return "ADInfo{" +
                "Id=" + Id +
                ", ImgPath='" + ImgPath + '\'' +
                ", Url='" + Url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Id);
        dest.writeString(this.ImgPath);
        dest.writeString(this.Url);
    }

    public ADInfo() {
    }

    public ADInfo(int imageId) {
        this.Id = imageId;
    }

    protected ADInfo(Parcel in) {
        this.Id = in.readInt();
        this.ImgPath = in.readString();
        this.Url = in.readString();
    }

    public static final Creator<ADInfo> CREATOR = new Creator<ADInfo>() {
        @Override
        public ADInfo createFromParcel(Parcel source) {
            return new ADInfo(source);
        }

        @Override
        public ADInfo[] newArray(int size) {
            return new ADInfo[size];
        }
    };
}
