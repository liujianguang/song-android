package com.song1.musicno1.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午9:41
 */
public class SingerCategoryRsp implements Parcelable {
  String id;
  String name;
  String image;
  boolean isleaf;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getImage() {
    // TODO 不应该在本地记录 host
    return "http://42.121.122.171" + image;
  }

  public boolean isLeaf() {
    return isleaf;
  }

  public static final Creator<SingerCategoryRsp> CREATOR = new Creator<SingerCategoryRsp>() {
    @Override
    public SingerCategoryRsp createFromParcel(Parcel source) {
      SingerCategoryRsp rsp = new SingerCategoryRsp();
      rsp.setId(source.readString());
      rsp.setName(source.readString());
      rsp.setImage(source.readString());
      boolean[] booleans = new boolean[1];
      source.readBooleanArray(booleans);
      rsp.setIsleaf(booleans[0]);
      return rsp;
    }

    @Override
    public SingerCategoryRsp[] newArray(int size) {
      return new SingerCategoryRsp[0];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(image);
    dest.writeBooleanArray(new boolean[]{isleaf});
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setIsleaf(boolean isleaf) {
    this.isleaf = isleaf;
  }
}
