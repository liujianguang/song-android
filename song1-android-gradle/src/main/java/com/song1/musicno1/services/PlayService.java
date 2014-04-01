package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.song1.musicno1.activities.CurrentNotworkDeviceActivity;
import com.song1.musicno1.helpers.LatestExecutor;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Player;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {
  protected LatestExecutor executor;
  protected Player         currentPlayer;
  protected PlayEvent      waitingEvent;

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

    if (waitingEvent != null) {
      play(waitingEvent);
      waitingEvent = null;
    }
  }

  @Subscribe
  public void play(PlayEvent event) {
    Player player = currentPlayer;
    if (player == null) {
      waitingEvent = event;
      Intent selectPlayer = new Intent(this, CurrentNotworkDeviceActivity.class);
      selectPlayer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(selectPlayer);
      return;
    }

    executor.submit(() -> {
      if (event.audio != null) {
        player.play(event.audio);
      } else {
        player.play();
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
    if (currentPlayer != null) {
      return new CurrentPlayerEvent(currentPlayer);
    }
    return null;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
    postEvent(currentPlayer());
  }

  private void postEvent(Object event) {
    if (event != null) {
      MainBus.post(event);
    }
  }
}
