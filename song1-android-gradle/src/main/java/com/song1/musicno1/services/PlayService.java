package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.google.common.collect.Maps;
import com.song1.musicno1.event.Event;
import com.song1.musicno1.helpers.LatestExecutor;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.*;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {
  protected LatestExecutor   playExecutor;
  protected OldPlayer        currentPlayer;
  protected SetPlaylistEvent waitingEvent;
  protected LatestExecutor   volumeExecutor;

  protected Map<String, Playlist>  playlistMap = Maps.newHashMap();
  protected Map<String, OldPlayer> playerMap   = Maps.newHashMap();
  protected Handler                handler     = new Handler();

  protected Runnable timerRunnable;
  protected int      timerValue;
  private   boolean  isActivityExited;

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
    isActivityExited = false;

    PlayerStore.INSTANCE.setLocalPlayer(new LocalPlayer(this));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(this);
    stopAllPlayers();
    playExecutor.shutdown();
    volumeExecutor.shutdown();
    if (timerRunnable != null) {
      handler.removeCallbacks(timerRunnable);
    }
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

  private void onPlayerOccupied(OldPlayer player) {
    if (player == currentPlayer) {
      postEvent(new CurrentPlayerOccupiedEvent());
    }
  }

  private void playNext(OldPlayer player) {
    playExecutor.submit(() -> {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        playlist.autoNext(player.getPlayMode());
        if (playlist.getCurrentAudio() == null) {
          player.stop();
        } else {
          player.play(playlist.getCurrentAudio());
        }
      }
    });
  }

  @Subscribe
  public void setPlaylist(SetPlaylistEvent event) {
    OldPlayer player = currentPlayer;
    if (player != null) {
      playlistMap.put(player.getId(), event.getPlaylist());
      postEvent(currentPlaylist());
      postEvent(currentPlayerPlayMode());
      play(new PlayEvent());
    } else {
      waitingEvent = event;
      MainBus.post(new ShowDeviceFragmentEvent());
    }
  }

  @Subscribe
  public void play(PlayEvent event) {
    playExecutor.submit(() -> {
      OldPlayer player = currentPlayer;
      if (player != null) {
        Playlist playlist = playlistMap.get(player.getId());
        if (playlist != null) {
          Audio currentAudio = playlist.getCurrentAudio();
          if (currentAudio != null) {
            player.play(currentAudio);
          }
          MainBus.post(new Event.PlayingAudioEvent(currentAudio));
        }
      }
    });
  }


  @Subscribe
  public void rePlay(Event.RePlayEvent event) {
    OldPlayer player = currentPlayer;
    if (player != null) {
      Playlist playlist = playlistMap.get(player.getId());
      if (playlist != null) {
        playlist.setCurrentAudio(playlist.getAudios().get(0));
        play(new PlayEvent());
      }
    }
  }

  @Subscribe
  public void resume(ResumeEvent event) {
    playExecutor.submit(() -> {
      OldPlayer player = currentPlayer;
      if (player != null && player.getState() == OldPlayer.PAUSED) {
        player.play();
      }
    });
  }

  @Subscribe
  public void pause(PauseEvent event) {
    playExecutor.submit(() -> {
      OldPlayer player = currentPlayer;
      if (player != null) {
        player.pause();
      }
    });
  }

  @Subscribe
  public void seek(SeekEvent event) {
    playExecutor.submit(() -> {
      OldPlayer player = currentPlayer;
      if (player != null) {
        player.seek(event.getSeekTo());
      }
    });
  }

  @Subscribe
  public void next(NextEvent event) {
    OldPlayer player = currentPlayer;
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
    OldPlayer player = currentPlayer;
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
    OldPlayer player = currentPlayer;
    if (player != null) {
      player.nextPlayMode();
      postEvent(currentPlayerPlayMode());
    }
  }

  @Subscribe
  public void setPlayMode(Event.SetPlayModeEvent event) {

  }


  @Produce
  public PositionEvent currentPlayerPosition() {
    OldPlayer player = currentPlayer;
    if (player != null) {
      return new PositionEvent(player.getCurrentAudio(), player.getPosition(), player.getDuration());
    }
    return null;
  }

  @Produce
  public CurrentPlayerStateEvent currentPlayerState() {
    OldPlayer player = currentPlayer;
    if (player != null) {
      return new CurrentPlayerStateEvent(player.getState());
    }
    return null;
  }

  @Produce
  public CurrentPlayerEvent currentPlayer() {
    OldPlayer player = currentPlayer;
    if (player != null) {
      return new CurrentPlayerEvent(player);
    }
    return null;
  }

  @Produce
  public CurrentPlaylistEvent currentPlaylist() {
    OldPlayer player = currentPlayer;
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
    OldPlayer player = currentPlayer;
    if (player != null) {
      return new PlayModeEvent(player.getPlayMode());
    }
    return null;
  }

  public void setCurrentPlayer(OldPlayer currentPlayer) {
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
    OldPlayer player = currentPlayer;
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
    timerValue = event.getSeconds();
    if (event.getSeconds() == 0) {
      if (timerRunnable != null) {
        handler.removeCallbacks(timerRunnable);
        timerRunnable = null;
      }
      MainBus.post(new TimerEvent(timerValue));
    } else {
      if (timerRunnable == null) {
        timerRunnable = new Runnable() {
          @Override
          public void run() {
            if (timerValue == 0) {
              handler.removeCallbacks(this);
              timerRunnable = null;
              MainBus.post(new ExitEvent());
//              if (isActivityExited) {
//                MainBus.post(new ExitEvent());
//              } else {
//                MainBus.post(new Event.ShowExitDialogEvent());
//              }
            } else {
              handler.postDelayed(this, 1000);
            }
            MainBus.post(new TimerEvent(timerValue));
            timerValue--;
          }
        };
        handler.post(timerRunnable);
      }
    }
  }

  private void stopAllPlayers() {
    List<Player> playerList = PlayerStore.INSTANCE.getPlayerList();
    for (Player player : playerList) {
      new Thread(() ->  player.release()).start();
    }
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }

  @Subscribe
  public void onActivityExit(ActivityExitEvent event) {
    isActivityExited = true;
    Log.d(this, "On activity exit");
  }
}
