package com.song1.musicno1.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.vender.WebServer;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import javax.inject.Inject;
import java.io.IOException;

/**
 * User: windless
 * Date: 13-6-9
 * Time: PM2:40
 */
public class HttpService extends Service {
  private static HttpService _instance;

  private WebServer httpServer;
  private int port = 16000;

  private WifiManager.WifiLock wifi_lock;

  @Inject   WifiManager           wifiManager;
  protected PowerManager.WakeLock wakeLock;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public static HttpService instance() {
    return _instance;
  }

  public String share(String local_path) {
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    int ipAddress = wifiInfo.getIpAddress();

    return String.format("http://%s:%d/shared?file=%s",
        intToIp(ipAddress), port,
        Uri.encode(local_path)
    );
  }

  public static String intToIp(int i) {
    return (i & 0xFF) + "." +
        ((i >> 8) & 0xFF) + "." +
        ((i >> 16) & 0xFF) + "." +
        ((i >> 24) & 0xFF);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    _instance = this;

    App.inject(this);

    MainBus.register(this);
    Log.init();

    wifi_lock = wifiManager.createWifiLock("HTTP");
    wifi_lock.acquire();

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "http_service");
    wakeLock.acquire();

    boolean isStarted = false;
    int count = 0;
    while (!isStarted && count < 20) {
      int port = new_port();
      httpServer = new WebServer(port, this);
      try {
        count++;
        httpServer.start();
        isStarted = true;
      } catch (IOException e) {
        Log.e(this, String.format("启动 HTTP Service 失败, port: %d: %s", port, e.getMessage()));
      }
    }
    if (!isStarted) {
      Toast.makeText(this, getString(R.string.start_http_service_failed), Toast.LENGTH_LONG).show();
    }

    createNotification();
  }

  private void createNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    final Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_play);

    builder.setSmallIcon(R.drawable.song1)
        .setAutoCancel(false)
        .setContentText(getString(R.string.song1_working))
        .setContentTitle(getString(R.string.song1))
        .setContentIntent(pendingIntent);

    Notification notification = builder.build();
    notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.notify(0, notification);
  }

  private int new_port() {
    port++;
    return port;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(this);

    httpServer.stop();
    _instance = null;
    wifi_lock.release();
    wifi_lock = null;
    wakeLock.release();

    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.cancel(0);
    Log.d(this, "Exit HttpService");
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }
}
