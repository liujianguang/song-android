package com.song1.musicno1.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.BaseActivity;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.StartTimerEvent;
import com.song1.musicno1.models.events.play.TimerEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.LocalPlayer;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class PlayService extends Service {

  private static final String EXIT  = "playService.exit";
  private static final String PREV  = "playService.prev";
  private static final String PLAY  = "playService.play";
  private static final String PAUSE = "playService.pause";
  private static final String NEXT  = "playService.next";

  protected Handler handler = new Handler();

  protected Runnable              timerRunnable;
  protected int                   timerValue;
  private   PowerManager.WakeLock wakeLock;
  NotificationManager manager;
  Notification        notification;


  BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      System.out.println("************************action : " + action);
    }
  };

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

    manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    initTelephonyListener();
    createNotification();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String action = intent.getAction();
    System.out.println("action : " + action);
    if (action != null){
      if (action.equals(EXIT)) {
        MainBus.post(new ExitEvent());
      } else if (action.equals(PLAY)) {
        Players.resume();
      } else if (action.equals(PAUSE)) {
        Players.pause();
      } else if (action.equals(NEXT)) {
        Players.next();
      }
    }
    return super.onStartCommand(intent, flags, startId);
  }

  Audio audio;

  @Subscribe
  public void currentAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    audio = PlayerStore.INSTANCE.getCurrentPlayer().getPlayingAudio();
    createNotification();
  }

  @Subscribe
  public void currentState(PlayerStore.PlayerStateChangedEvent event){
    createNotification();
  }

  private void createNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    final Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_play);
    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

    Intent exitIntent = new Intent(EXIT);

    Intent playIntent = new Intent(PLAY);


    Intent pauseIntent = new Intent(PAUSE);


    Intent nextIntent = new Intent(NEXT);


    remoteViews.setOnClickPendingIntent(R.id.exitButton, PendingIntent.getService(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    remoteViews.setOnClickPendingIntent(R.id.exitButton2, PendingIntent.getService(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    remoteViews.setOnClickPendingIntent(R.id.playButton, PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    remoteViews.setOnClickPendingIntent(R.id.pauseButton, PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    remoteViews.setOnClickPendingIntent(R.id.nextButton, PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT));


    if (audio != null) {
      remoteViews.setViewVisibility(R.id.contentLayout, View.VISIBLE);
      remoteViews.setViewVisibility(R.id.otherLayout,View.GONE);

      remoteViews.setTextViewText(R.id.titleTextView, audio.getTitle());
      remoteViews.setTextViewText(R.id.artistTextView, audio.getSubtitle(this));
      int state = PlayerStore.INSTANCE.getCurrentPlayer().getState();
      if (state == Player.State.PAUSED) {
        remoteViews.setViewVisibility(R.id.pauseButton, View.GONE);
        remoteViews.setViewVisibility(R.id.playButton, View.VISIBLE);
      } else {
        remoteViews.setViewVisibility(R.id.pauseButton, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.playButton, View.GONE);
      }
    } else {
      remoteViews.setViewVisibility(R.id.contentLayout, View.GONE);
      remoteViews.setViewVisibility(R.id.otherLayout,View.VISIBLE);
    }

    builder.setSmallIcon(R.drawable.song1)
        .setAutoCancel(false)
        .setContentText(getString(R.string.song1_working))
        .setContentTitle(getString(R.string.song1))
        .setContentIntent(pendingIntent)
        .setContent(remoteViews);

    notification = builder.build();
    notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

    manager.notify(0, notification);
  }

  private int st = -1;

  private void initTelephonyListener() {
    TelephonyManager teleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    teleManager.listen(new PhoneStateListener() {

      @Override
      public void onCallStateChanged(int state, String incomingNumber) {
        System.out.println("state : " + state);
        Player player = PlayerStore.INSTANCE.getCurrentPlayer();
        if (player == null) {
          return;
        }
        if (player.getState() == Player.State.PLAYING) {
          st = player.getState();
        }
        if (st == -1) {
          return;
        }
        switch (state) {
          case TelephonyManager.CALL_STATE_RINGING: //响铃
            Toast.makeText(getApplicationContext(), "Ringing: " + incomingNumber, Toast.LENGTH_LONG).show();
            if (st == Player.State.PLAYING) {
              Players.pause();
            }
            break;
          case TelephonyManager.CALL_STATE_OFFHOOK: //接听
            Toast.makeText(getApplicationContext(), "OffHook: " + incomingNumber, Toast.LENGTH_LONG)
                .show();
            if (st == Player.State.PLAYING) {
              Players.pause();
            }
            break;
          case TelephonyManager.CALL_STATE_IDLE: //挂断
            Toast.makeText(getApplicationContext(), "Idle: " + incomingNumber, Toast.LENGTH_LONG)
                .show();
            if (st == Player.State.PLAYING) {
              Players.resume();
              st = -1;
            }
            break;
        }
      }
    }, PhoneStateListener.LISTEN_CALL_STATE);
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
    System.out.println("stopAllPlayer.....");
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
