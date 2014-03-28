package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Audio;

/**
 * Created by windless on 3/27/14.
 */
public class PlayEvent {
  public Audio audio;

  public PlayEvent(Audio audio) {
    this.audio = audio;
  }

  public PlayEvent() {
  }
}
