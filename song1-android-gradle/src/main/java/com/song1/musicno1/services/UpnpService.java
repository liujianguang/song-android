package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.cybergarage.upnp.std.av.controller.MediaController;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM5:14
 */
public class UpnpService extends Service {
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mediaController = new MediaController();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}

