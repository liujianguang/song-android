package com.song1.musicno1.models.events.upnp;

import com.song1.musicno1.models.play.Player;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class DeviceChangeEvent {
  public List<Player> players;

  public DeviceChangeEvent(List<Player> players) {
    this.players = players;
  }
}
