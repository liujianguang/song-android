package com.song1.musicno1.entity;

import com.google.gson.annotations.SerializedName;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午9:14
 */

public class ChartCategoryRsp {
  String id;
  String name;
  String image;
  @SerializedName("isleaf") boolean isLeaf;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getIamge() {
    return image;
  }

  public boolean isLeaf() {
    return isLeaf;
  }
}
