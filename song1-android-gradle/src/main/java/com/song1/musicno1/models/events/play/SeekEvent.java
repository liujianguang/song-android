package com.song1.musicno1.models.events.play;

/**
 * Created by windless on 3/31/14.
 */
public class SeekEvent {
  protected final int seekTo;

  public SeekEvent(int seekTo) {
    this.seekTo = seekTo;
  }

  public int getSeekTo() {
    return seekTo;
  }
}
