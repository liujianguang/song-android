package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Audio;

/**
 * Created by windless on 3/27/14.
 */
public class PositionEvent {
  protected final Audio audio;
  protected final int   position;
  protected final int   duration;

  public PositionEvent(Audio audio, int position, int duration) {
    this.audio = audio;
    this.position = position;
    this.duration = duration;
  }

  public Audio getAudio() {
    return audio;
  }

  public int getPosition() {
    return position;
  }

  public int getDuration() {
    return duration;
  }
}
