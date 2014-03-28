package com.song1.musicno1.models.play;

import com.song1.musicno1.helpers.TimeHelper;

/**
 * Created by windless on 3/26/14.
 */
public class PositionInfo {
  private String uri;
  private int    duration;
  private int    position;

  public void setDurationStr(String durationStr) {
    this.duration = TimeHelper.str2milli(durationStr);
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setPositionStr(String positionStr) {
    this.position = TimeHelper.str2milli(positionStr);
  }

  public int getPosition() {
    return position;
  }

  public int getDuration() {
    return duration;
  }
}
