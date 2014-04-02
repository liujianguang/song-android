package com.song1.musicno1.models.events.play;

/**
 * Created by windless on 4/2/14.
 */
public class PlayModeEvent {
  protected final int playMode;

  public PlayModeEvent(int playMode) {
    this.playMode = playMode;
  }

  public int getPlayMode() {
    return playMode;
  }
}
