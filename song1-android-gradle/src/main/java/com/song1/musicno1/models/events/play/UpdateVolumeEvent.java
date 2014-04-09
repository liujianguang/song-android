package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Volume;

/**
 * Created by windless on 14-4-9.
 */
public class UpdateVolumeEvent {
  public static final int UP   = 0;
  public static final int DOWN = 1;

  protected Volume volume;

  private int upOrDown = -1;

  public UpdateVolumeEvent() {

  }

  public UpdateVolumeEvent(int upOrDown) {
    this.upOrDown = upOrDown;
  }

  public boolean isUp() {
    return upOrDown == UP;
  }

  public boolean isDown() {
    return upOrDown == DOWN;
  }

  public UpdateVolumeEvent(Volume volume) {
    this.volume = volume;
  }

  public Volume getVolume() {
    return volume;
  }
}
