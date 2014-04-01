package com.song1.musicno1.models.play;

import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.*;

/**
 * Created by windless on 3/28/14.
 */
public class Players {
  public static void play() {
    MainBus.post(new PlayEvent());
  }

  public static void pause() {
    MainBus.post(new PauseEvent());
  }

  public static void seek(int seekTo) {
    MainBus.post(new SeekEvent(seekTo));
  }

  public static void setPlaylist(Playlist playlist) {
    MainBus.post(new SetPlaylistEvent(playlist));
  }

  public static void next() {
    MainBus.post(new NextEvent());
  }

  public static void previous() {
    MainBus.post(new PreviousEvent());
  }
}
