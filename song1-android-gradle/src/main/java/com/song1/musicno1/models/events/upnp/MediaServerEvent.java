package com.song1.musicno1.models.events.upnp;

import com.song1.musicno1.models.play.MediaServer;

import java.util.List;

/**
 * Created by windless on 14-4-8.
 */
public class MediaServerEvent {
  protected final List<MediaServer> serverList;

  public MediaServerEvent(List<MediaServer> serverList) {
    this.serverList = serverList;
  }

  public List<MediaServer> getServerList() {
    return serverList;
  }
}
