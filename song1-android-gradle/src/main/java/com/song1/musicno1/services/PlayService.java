package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.google.common.collect.Maps;
import com.song1.musicno1.helpers.LatestExecutor;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Playlist;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import java.util.Map;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {
  protected LatestExecutor   playExecutor;
  protected Player           currentPlayer;
  protected SetPlaylistEvent waitingEvent;
  protected LatestExecutor   volumeExecutor;

  protected Map<String, Playlist> playlistMap = Maps.newHashMap();
  protected Map<String, Player>   playerMap   = Maps.newHashMap();
  protected Handler               handler     = new Handler();

  protected final int[] TIMER_VALUES = new int[]{0, 15, 30, 45, 60, 120};

  protected Runnable timerRunnable;
  protected int      timerValue;

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
    stopAllPlayers();
    playExecutor.shutdown();
    volumeExecutor.shutdown();
    Log.d(this, "Exit PlayService");
  }

  @Subscribe
  public void selectPlayer(SelectPlayerEvent event) {
    playerMap.put(event.player.getId(), event.player);
    volumeExecutor.submit(() -> {
      event.player.updateVolume();
    });

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

    event.player.onOccupied((player) -> onPlayerOccupied(player));

    if (waitingEvent != null) {
      setPlaylist(waitingEvent);
      waitingEvent = null;
    }

  }

  private void onPlayerOccupied(Player player) {
    if (player == currentPlayer) {
      postEvent(new CurrentPlayerOccupiedEvent());
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
//            player.updateVolume();
            MainBus.post(new VolumeEvent(player.getVolume()));
          }
        } else {
//          player.updateVolume();
          MainBus.post(new VolumeEvent(player.getVolume()));
        }
      });
    }
  }

  @Subscribe
  public void startTimer(StartTimerEvent event) {
    timerValue = TIMER_VALUES[event.getMinutes()] * 60;
    if (event.getMinutes() == 0) {
      if (timerRunnable != null) {
        handler.removeCallbacks(timerRunnable);
        timerRunnable = null;
      }
    } else {
      if (timerRunnable == null) {
        timerRunnable = new Runnable() {
          @Override
          public void run() {
            timerValue--;
            if (timerValue == 0) {
              handler.removeCallbacks(this);
              timerRunnable = null;
              stopAllPlayers();
            } else {
              handler.postDelayed(this, 1000);
            }
            MainBus.post(new TimerEvent(timerValue));
          }
        };
        handler.post(timerRunnable);
      }
    }
  }

  private void stopAllPlayers() {
    for (Player player : playerMap.values()) {
      new Thread(() -> player.stop()).start();
    }
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }
}
