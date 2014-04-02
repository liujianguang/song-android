package com.song1.musicno1.entity;

import com.google.gson.annotations.SerializedName;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午10:36
 */
public class SongRsp {
  @SerializedName("music_id")   String musicId;
  @SerializedName("type_id")    String typeId;
  @SerializedName("music_name") String musicName;
  String singer;
  float  duration;
  String album;
  String cover;

  public String getMusicId() {
    return musicId;
  }

  public String getTypeId() {
    return typeId;
  }

  public String getMusicName() {
    return musicName;
  }

  public String getSinger() {
    return singer;
  }

  public float getDuration() {
    return duration;
  }

  public String getAlbum() {
    return album;
  }

  public String getCover() {
    return cover;
  }
}
