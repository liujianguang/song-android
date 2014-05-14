package com.song1.musicno1.models.events.upnp;

import com.song1.musicno1.models.play.OldPlayer;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class DeviceChangeEvent {
  public List<OldPlayer> players;

  public DeviceChangeEvent(List<OldPlayer> players) {
    this.players = players;
  }
}
