package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.common.collect.Maps;
import com.song1.musicno1.helpers.LatestExecutor;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Playlist;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.Map;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {
  protected LatestExecutor   playExecutor;
  protected Player           currentPlayer;
  protected SetPlaylistEvent waitingEvent;

  protected Map<String, Playlist> playlistMap = Maps.newHashMap();
  protected LatestExecutor volumeExecutor;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    MainBus.register(this);
    playExecutor = new LatestExecutor();
    volumeExecutor = new LatestExecutor();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(this);
  }

  @Subscribe
  public void selectPlayer(SelectPlayerEvent event) {
    setCurrentPlayer(event.player);

    event.player.onStateChanged((player, state) -> {
      if (player == currentPlayer) {
        postEvent(currentPlayerState());
      }
    });

    event.player.onPositionChanged((player, position, duration) -> {
      if (player == currentPlayer) {
        postEvent(currentPlayerPosition());
      }
    });

    event.player.onPlayComplete((player) -> playNext(player));

    if (waitingEvent != null) {
      setPlaylist(waitingEvent);
      waitingEvent = null;
    }
  }

  private void playNext(Player player) {
    playExecutor.submit(() -> {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        playlist.autoNext(player.getPlayMode());
        if (playlist.getCurrentAudio() == null) {
          player.stop();
        } else {
          play(new PlayEvent());
        }
      }
    });
  }

  @Subscribe
  public void setPlaylist(SetPlaylistEvent event) {
    Player player = currentPlayer;
    if (player != null) {
      playlistMap.put(player.getId(), event.getPlaylist());
      postEvent(currentPlaylist());
      play(new PlayEvent());
    } else {
      waitingEvent = event;
      MainBus.post(new ShowDeviceFragmentEvent());
    }
  }

  @Subscribe
  public void play(PlayEvent event) {
    playExecutor.submit(() -> {
      Player player = currentPlayer;
      if (player != null) {
        Playlist playlist = playlistMap.get(player.getId());
        if (playlist != null) {
          Audio currentAudio = playlist.getCurrentAudio();
          if (currentAudio != null) player.play(currentAudio);
        }
      }
    });
  }

  @Subscribe
  public void resume(ResumeEvent event) {
    playExecutor.submit(() -> {
      Player player = currentPlayer;
      if (player != null && player.getState() == Player.PAUSED) {
        player.play();
      }
    });
  }

  @Subscribe
  public void pause(PauseEvent event) {
    playExecutor.submit(() -> {
      Player player = currentPlayer;
      if (player != null) {
        player.pause();
      }
    });
  }

  @Subscribe
  public void seek(SeekEvent event) {
    playExecutor.submit(() -> {
      Player player = currentPlayer;
      if (player != null) {
        player.seek(event.getSeekTo());
      }
    });
  }

  @Subscribe
  public void next(NextEvent event) {
    Player player = currentPlayer;
    if (player != null) {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        playlist.next(player.getPlayMode());
        play(new PlayEvent());
      }
    }
  }

  @Subscribe
  public void previous(PreviousEvent event) {
    Player player = currentPlayer;
    if (player != null) {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        playlist.previous();
        play(new PlayEvent());
      }
    }
  }

  @Subscribe
  public void nextPlayMode(NextPlayModeEvent event) {
    Player player = currentPlayer;
    if (player != null) {
      player.nextPlayMode();
      postEvent(currentPlayerPlayMode());
    }
  }

  @Produce
  public PositionEvent currentPlayerPosition() {
    Player player = currentPlayer;
    if (player != null) {
      return new PositionEvent(player.getCurrentAudio(), player.getPosition(), player.getDuration());
    }
    return null;
  }

  @Produce
  public CurrentPlayerStateEvent currentPlayerState() {
    Player player = currentPlayer;
    if (player != null) {
      return new CurrentPlayerStateEvent(player.getState());
    }
    return null;
  }

  @Produce
  public CurrentPlayerEvent currentPlayer() {
    Player player = currentPlayer;
    if (player != null) {
      return new CurrentPlayerEvent(player);
    }
    return null;
  }

  @Produce
  public CurrentPlaylistEvent currentPlaylist() {
    Player player = currentPlayer;
    if (player != null) {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        return new CurrentPlaylistEvent(playlist);
      } else {
        return new CurrentPlaylistEvent(new Playlist());
      }
    }
    return null;
  }

  @Produce
  public PlayModeEvent currentPlayerPlayMode() {
    Player player = currentPlayer;
    if (player != null) {
      return new PlayModeEvent(player.getPlayMode());
    }
    return null;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
    postEvent(currentPlayer());
    postEvent(currentPlaylist());
    postEvent(currentPlayerPosition());
    postEvent(currentPlayerState());
    postEvent(currentPlayerPlayMode());
    updateVolume(null);
  }

  private void postEvent(Object event) {
    if (event != null) {
      MainBus.post(event);
    }
  }

  @Subscribe
  public void onUpdateVolumeEvent(UpdateVolumeEvent event) {
    updateVolume(event);
  }

  private void updateVolume(UpdateVolumeEvent event) {
    Player player = currentPlayer;
    if (player != null) {
      volumeExecutor.submit(() -> {
        if (event != null) {
          if (event.isUp()) {
            player.volumeUp();
            MainBus.post(new VolumeEvent(player.getVolume()));
          } else if (event.isDown()) {
            player.volumeDown();
            MainBus.post(new VolumeEvent(player.getVolume()));
          } else if (event.getVolume() != null) {
            player.setVolume(event.getVolume().getCurrent());
          } else {
            MainBus.post(new VolumeEvent(player.getVolume()));
          }
        } else {
          MainBus.post(new VolumeEvent(player.getVolume()));
        }
      });
    }
  }
}
