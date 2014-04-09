package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Volume;

/**
 * Created by windless on 14-4-9.
 */
public class VolumeEvent {
  protected final Volume volume;

  public VolumeEvent(Volume volume) {
    this.volume = volume;
  }

  public Volume getVolume() {
    return volume;
  }
}
