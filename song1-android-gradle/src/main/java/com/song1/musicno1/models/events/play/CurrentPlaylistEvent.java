package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.Playlist;

/**
 * Created by windless on 4/1/14.
 */
public class CurrentPlaylistEvent {
  protected final Playlist playlist;

  public CurrentPlaylistEvent(Playlist playlist) {
    this.playlist = playlist;
  }

  public Playlist getPlaylist() {
    return playlist;
  }
}
