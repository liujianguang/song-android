package com.song1.musicno1.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import com.google.common.collect.Lists;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class WifiModel{

  List<ScanResult>   scanResultList = Lists.newArrayList();
  List<ScanListener> scanListeners  = Lists.newArrayList();

  Context       context;
  WifiManager   wifiManager;
  HandlerThread handlerThread;
  Handler       handler;

  public interface ScanListener {
    public void scanResult(List<ScanResult> scanResultList);
  }

  BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Log.d(WifiModel.this, "action : " + action);
      if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
        dispatchScanResult();
      }
    }
  };

  public WifiModel(Context context) {
    this.context = context;
    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    handlerThread = new HandlerThread("wifiScanThread");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    context.registerReceiver(wifiReceiver, intentFilter);
  }

  public void scan() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(this, "startScan...");
        wifiManager.startScan();
        Log.d(this, "overScan...");
      }
    });
  }

  public void stop() {
    if (handlerThread != null) {
      handlerThread.quit();
    }
    context.unregisterReceiver(wifiReceiver);
  }

  public void addScanListener(ScanListener scanListener) {
    scanListeners.add(scanListener);
  }

  public void removeScanListener(ScanListener scanListener) {
    scanListeners.remove(scanListener);
  }

  private void dispatchScanResult() {
    scanResultList = wifiManager.getScanResults();
    for (ScanListener scanListener : scanListeners) {
      scanListener.scanResult(scanResultList);
    }
  }
}
