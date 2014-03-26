package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/26/14.
 */
public class PositionInfo {
  public  String uri;
  public  long   position;
  public  long   duration;
  private String durationStr;
  private String positionStr;

  public void setDurationStr(String durationStr) {
    this.durationStr = durationStr;
  }

  public String getDurationStr() {
    return durationStr;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public void setPositionStr(String positionStr) {
    this.positionStr = positionStr;
  }

  public String getPositionStr() {
    return positionStr;
  }
}
