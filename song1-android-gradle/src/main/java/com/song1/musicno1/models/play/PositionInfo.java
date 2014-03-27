package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/26/14.
 */
public class PositionInfo {
  public  String uri;
  private String durationStr;
  private String positionStr;

  public String getDurationStr() {
    return durationStr;
  }

  public void setDurationStr(String durationStr) {
    this.durationStr = durationStr;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getPositionStr() {
    return positionStr;
  }

  public void setPositionStr(String positionStr) {
    this.positionStr = positionStr;
  }

  public long getPosition() {
    return 0;
  }

  public long getDuration() {
    return 0;
  }
}
