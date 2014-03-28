package com.song1.musicno1.models.play;

import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.PauseEvent;
import com.song1.musicno1.models.events.play.PlayEvent;

/**
 * Created by windless on 3/28/14.
 */
public class Players {
  public static void play(Audio audio) {
    MainBus.post(new PlayEvent(audio));
  }

  public static void play() {
    MainBus.post(new PlayEvent());
  }

  public static void pause() {
    MainBus.post(new PauseEvent());
  }
}
