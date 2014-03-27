package com.song1.musicno1.models.events.play;

/**
 * Created by windless on 3/27/14.
 */
public class CurrentPlayerStateEvent {
  public final int state;

  public CurrentPlayerStateEvent(int state) {
    this.state = state;
  }
}
