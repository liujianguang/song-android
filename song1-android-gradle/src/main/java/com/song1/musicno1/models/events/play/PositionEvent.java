package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Audio;

/**
 * Created by windless on 3/27/14.
 */
public class PositionEvent {
  protected final Audio audio;
  protected final long  position;
  protected final long duration;

  public PositionEvent(Audio audio, long position, long duration) {
    this.audio = audio;
    this.position = position;
    this.duration = duration;
  }

  public Audio getAudio() {
    return audio;
  }

  public long getPosition() {
    return position;
  }

  public long getDuration() {
    return duration;
  }
}
