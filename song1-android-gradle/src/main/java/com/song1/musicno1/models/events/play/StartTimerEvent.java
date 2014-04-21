package com.song1.musicno1.models.events.play;

/**
 * Created by windless on 14-4-21.
 */
public class StartTimerEvent {
  protected final int minutes;

  public StartTimerEvent(int minutes) {
    this.minutes = minutes;
  }

  public int getMinutes() {
    return minutes;
  }
}
