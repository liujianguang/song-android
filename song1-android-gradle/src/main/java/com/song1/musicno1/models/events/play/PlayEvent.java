package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;

/**
 * Created by windless on 3/27/14.
 */
public class PlayEvent {
  public final Player player;
  public final Audio  audio;

  public PlayEvent(Player player, Audio audio) {
    this.player = player;
    this.audio = audio;
  }
}
