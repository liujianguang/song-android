package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.OldPlayer;

/**
 * Created by windless on 3/27/14.
 */
public class SelectPlayerEvent {
  public final OldPlayer player;

  public SelectPlayerEvent(OldPlayer player) {
    this.player = player;
  }
}
