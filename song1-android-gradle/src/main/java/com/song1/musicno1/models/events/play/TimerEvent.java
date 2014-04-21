package com.song1.musicno1.models.events.play;

/**
 * Created by windless on 14-4-21.
 */
public class TimerEvent {
  protected final int timerValue;

  public TimerEvent(int timerValue) {
    this.timerValue = timerValue;
  }

  public int getTimerValue() {
    return timerValue;
  }
}
