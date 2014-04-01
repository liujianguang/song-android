package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.common.collect.Maps;
import com.song1.musicno1.activities.CurrentNotworkDeviceActivity;
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
  protected LatestExecutor   executor;
  protected Player           currentPlayer;
  protected SetPlaylistEvent waitingEvent;

  protected Map<String, Playlist> playlistMap = Maps.newHashMap();

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    MainBus.register(this);
    executor = new LatestExecutor();
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
    executor.submit(() -> {
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
      Intent selectPlayer = new Intent(this, CurrentNotworkDeviceActivity.class);
      selectPlayer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(selectPlayer);
    }
  }

  @Subscribe
  public void play(PlayEvent event) {
    executor.submit(() -> {
      Player player = currentPlayer;
      if (player != null) {
        if (player.getState() == Player.PAUSED) {
          player.play();
        } else {
          Playlist playlist = playlistMap.get(player.getId());
          if (playlist != null) {
            Audio currentAudio = playlist.getCurrentAudio();
            if (currentAudio != null) player.play(currentAudio);
          }
        }
      }
    });
  }

  @Subscribe
  public void pause(PauseEvent event) {
    executor.submit(() -> {
      Player player = currentPlayer;
      if (player != null) {
        player.pause();
      }
    });
  }

  @Subscribe
  public void seek(SeekEvent event) {
    executor.submit(() -> {
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

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
    postEvent(currentPlayer());
    postEvent(currentPlaylist());
  }

  private void postEvent(Object event) {
    if (event != null) {
      MainBus.post(event);
    }
  }
}
