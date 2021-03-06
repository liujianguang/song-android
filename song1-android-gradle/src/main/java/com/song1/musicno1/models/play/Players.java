package com.song1.musicno1.models.play;

import android.support.v4.app.FragmentManager;
import com.song1.musicno1.fragments.DeviceFragment;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.stores.PlayerStore;

/**
 * Created by windless on 3/28/14.
 */
public class Players {
  public static void playWithAudio(Audio audio) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.playWithAudio(audio);
    }
  }

  public static void pause() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.pause();
    }
  }

  public static void seek(int seekTo) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.seekTo(seekTo);
    }
  }

  public static void setPlaylist(Playlist playlist, FragmentManager fragmentManager) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) {
      DeviceFragment fragment = new DeviceFragment();
      fragment.onClose(() -> {
        Player newPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
        if (newPlayer != null) {
          newPlayer.setPlaylist(playlist);
        }
      });
      fragment.show(fragmentManager, "Device");
    } else {
      currentPlayer.setPlaylist(playlist);
    }
  }

  public static void next() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.next();
    }
  }

  public static void previous() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.previous();
    }
  }

  public static void setVolume(int volume, boolean showPanel) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.setVolume(volume, showPanel);
    }
  }

  public static void volumeUp(boolean showPanel) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.volumeUp(showPanel);
    }
  }

  public static void volumeDown(boolean showPanel) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.volumeDown(showPanel);
    }
  }

  public static void nextPlayMode() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.nextPlayMode();
    }
  }

  public static void resume() {
    System.out.println("resume");
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    System.out.println(currentPlayer);
    if (currentPlayer != null) {
      currentPlayer.resume();
    }
  }

  public static void play() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      Playlist playlist = currentPlayer.getPlaylist();
      if (playlist != null) {
        if (playlist.getCurrentAudio() == null && playlist.getAudios().size() > 0) {
          playlist.setCurrentAudio(playlist.getAudios().get(0));
        }
        currentPlayer.playWithAudio(playlist.getCurrentAudio());
      }
    }
  }

  public static void randomPlay(Playlist playlist, FragmentManager fragmentManager) {
    playlist.autoNext(Player.PlayMode.SHUFFLE);

    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) {
      DeviceFragment fragment = new DeviceFragment();
      fragment.onClose(() -> {
        Player newPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
        if (newPlayer != null) {
          newPlayer.setPlaylist(playlist);
          newPlayer.setPlayMode(Player.PlayMode.SHUFFLE);
          MainBus.post(new PlayerStore.PlayerModeChangedEvent());
        }
      });
      fragment.show(fragmentManager, "Device");
    } else {
      currentPlayer.setPlaylist(playlist);
      currentPlayer.setPlayMode(Player.PlayMode.SHUFFLE);
      MainBus.post(new PlayerStore.PlayerModeChangedEvent());
    }
  }
}
