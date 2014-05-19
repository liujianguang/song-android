package com.song1.musicno1.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import com.song1.musicno1.activities.BaseActivity;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.StartTimerEvent;
import com.song1.musicno1.models.events.play.TimerEvent;
import com.song1.musicno1.models.play.LocalPlayer;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {
  protected Handler handler = new Handler();

  protected Runnable              timerRunnable;
  protected int                   timerValue;
  private   PowerManager.WakeLock wakeLock;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    MainBus.register(this);
    PlayerStore.INSTANCE.setLocalPlayer(new LocalPlayer(this));

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "player_service");
    wakeLock.acquire();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(this);
    wakeLock.release();

    sendBroadcast(new Intent(BaseActivity.FINISH_INTENT));

    stopAllPlayers();
    if (timerRunnable != null) {
      handler.removeCallbacks(timerRunnable);
    }
    Log.d(this, "Exit PlayService");
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
      new Thread(() -> player.release()).start();
    }
    PlayerStore.INSTANCE.clear();
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }
}
