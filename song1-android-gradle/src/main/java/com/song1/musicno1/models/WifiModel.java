package com.song1.musicno1.models;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by kate on 14-3-17.
 */
public class WifiModel implements Handler.Callback {

  Context     context;
  WifiManager wifiManager;
  Handler     handler;

  public WifiModel(Context context) {
    this.context = context;
    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    HandlerThread handlerThread = new HandlerThread("wifiScanThread");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper(), this);
  }

  public void scan() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        wifiManager.startScan();
      }
    });
  }

  @Override
  public boolean handleMessage(Message message) {
    return false;
  }
}
