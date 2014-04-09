package com.song1.musicno1.models.play;

/**
 * Created by windless on 14-4-9.
 */
public class Volume {
  private int max;
  private int current;

  public Volume(int current, int max) {
    this.current = current;
    this.max = max;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }
}
