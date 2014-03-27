package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Player;

/**
 * Created by windless on 3/27/14.
 */
public class SelectPlayerEvent {
  public final Player player;

  public SelectPlayerEvent(Player player) {
    this.player = player;
  }
}
