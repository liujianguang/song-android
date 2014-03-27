package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Player;

/**
 * Created by windless on 3/27/14.
 */
public class CurrentPlayerEvent {

  protected final Player currentPlayer;

  public CurrentPlayerEvent(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }
}
