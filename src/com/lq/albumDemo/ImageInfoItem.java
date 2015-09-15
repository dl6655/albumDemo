package com.lq.albumDemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by dingli on 2015-9-10.
 */
public class ImageInfoItem implements Serializable,Parcelable {

    private static final long serialVersionUID = 401314654472890540L;


    public String folderPath = "";

    public String filePath = "";

    public String fileName = "";

    public long fileSize;

    public int count;

    public long ModifiedDate;

    public boolean iSelected;

    public long duration;


    public boolean isHidden;

    public int orientation;


    public long dbId;

    /**
     * 图片的长和宽 用来后期判断是否需要压缩图片的
     */
    public int picWidth;
    public int picHeight;

    /**
     * 用来记录压缩后的地址的
     */
    public String compressPath;

    public int rotateDegree = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ImageInfoItem> CREATOR = new Parcelable.Creator<ImageInfoItem>() {
        //重写Creator

        @Override
        public ImageInfoItem createFromParcel(Parcel source) {
            ImageInfoItem info = new ImageInfoItem();
            info.filePath = source.readString();
            info.fileName = source.readString();
            info.fileSize = source.readLong();
            info.count = source.readInt();
            info.ModifiedDate = source.readLong();
            info.dbId = source.readLong();
            return info;

        }

        @Override
        public ImageInfoItem[] newArray(int size) {
            return null;
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeInt(count);
        dest.writeLong(ModifiedDate);
        dest.writeLong(dbId);
    }
}
